package club.rainbowkitty.kalic0re.common.datagen;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;

import net.minecraft.data.CachedOutput;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;

/**
 * PNG helpers for datagen providers that write generated images. Shared by relocation, not a mod
 * dependency (see each consuming mod's {@code build.gradle}).
 */
public final class PngAssets {
    /**
     * The chroma key an overlay paints where the result should be nothing at all: magenta, which
     * {@link #composite} erases to full transparency.
     *
     * <p>It has to be a colour rather than transparency because transparency already means
     * something else in an overlay — let the base show through — so cutting the base away needs a
     * reserved colour instead. Magenta is the conventional choice, being a tone art rarely holds.
     */
    public static final int CUTOUT = 0xFF00FF;

    private PngAssets() {
    }

    /**
     * Reads a PNG off the datagen classpath, normalized to {@link BufferedImage#TYPE_INT_ARGB} so
     * callers can composite or sample it without worrying about the source's colour model.
     *
     * @param loader the classloader to resolve with, e.g. {@code Provider.class.getClassLoader()}
     * @param classpathPath the resource path, e.g. {@code assets/<mod>/textures/item/base.png}
     */
    public static BufferedImage read(ClassLoader loader, String classpathPath) {
        try (InputStream in = loader.getResourceAsStream(classpathPath)) {
            if (in == null) {
                throw new IOException("Missing datagen input on the classpath: " + classpathPath);
            }
            BufferedImage raw = ImageIO.read(in);
            BufferedImage argb = new BufferedImage(
                    raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = argb.createGraphics();
            g.drawImage(raw, 0, 0, null);
            g.dispose();
            return argb;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Mirrors {@code DataProvider.saveStable}'s hash-then-{@code writeIfNeeded}, with PNG bytes
     * instead of a {@code JsonElement}, on the shared background executor.
     *
     * @param executorName a short label for the executor thread, e.g. {@code "myModTextures"}
     */
    public static CompletableFuture<?> save(CachedOutput cache, Path path, BufferedImage image,
            String executorName) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HashingOutputStream hashed = new HashingOutputStream(Hashing.sha1(), bytes);
                ImageIO.write(image, "PNG", hashed);
                hashed.close();
                cache.writeIfNeeded(path, bytes.toByteArray(), hashed.hash());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, Util.backgroundExecutor().forName(executorName));
    }

    /**
     * Multiplies every pixel by a colour, the same per-channel operation the client applies to a
     * dyeable layer at render time — so art baked here and a layer tinted live agree pixel for
     * pixel.
     *
     * @param art the source image, left untouched
     * @param colour an opaque ARGB colour, e.g. {@code DyeColor.getTextureDiffuseColor()}
     */
    public static BufferedImage tint(BufferedImage art, int colour) {
        BufferedImage out = new BufferedImage(
                art.getWidth(), art.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < art.getHeight(); y++) {
            for (int x = 0; x < art.getWidth(); x++) {
                out.setRGB(x, y, ARGB.multiply(art.getRGB(x, y), colour));
            }
        }
        return out;
    }

    /**
     * The per-channel median of an image's opaque pixels, as an opaque colour: each channel's
     * median is taken on its own, so the result need not be any one pixel of the image. An even
     * count takes the mean of the middle two, rounded down.
     *
     * @param art the image to sample, which must hold at least one opaque pixel
     */
    public static int medianColour(BufferedImage art) {
        int[][] channels = new int[3][art.getWidth() * art.getHeight()];
        int count = 0;
        for (int y = 0; y < art.getHeight(); y++) {
            for (int x = 0; x < art.getWidth(); x++) {
                int argb = art.getRGB(x, y);
                if (argb >>> 24 == 0xFF) {
                    channels[0][count] = argb >> 16 & 0xFF;
                    channels[1][count] = argb >> 8 & 0xFF;
                    channels[2][count] = argb & 0xFF;
                    count++;
                }
            }
        }
        if (count == 0) {
            throw new IllegalArgumentException("Image has no opaque pixel to take a median of");
        }
        int median = 0xFF000000;
        for (int c = 0; c < 3; c++) {
            int[] sorted = Arrays.copyOf(channels[c], count);
            Arrays.sort(sorted);
            int value = count % 2 == 1
                    ? sorted[count / 2] : (sorted[count / 2 - 1] + sorted[count / 2]) / 2;
            median |= value << (16 - 8 * c);
        }
        return median;
    }

    /**
     * Colours a grayscale image so that {@code referenceGray} becomes {@code colour} exactly and
     * every other gray scales from it: each channel is {@code colour × gray / referenceGray},
     * clamped to 255. Unlike {@link #tint}, which treats white as the colour, this keeps the gray
     * that stood for the colour in the art, so art drawn as the luma of one colour can be
     * recoloured to another with the same brightness relationships.
     *
     * <p>Transparent pixels and {@link #CUTOUT} magenta pass through untouched, so a following
     * {@link #composite} still reads the chroma key.
     *
     * @param gray the source image, left untouched; every other pixel must be gray
     * @param colour an opaque ARGB colour, e.g. from {@link #medianColour}
     * @param referenceGray the gray level in {@code gray} that stands for {@code colour}
     */
    public static BufferedImage colourise(BufferedImage gray, int colour, int referenceGray) {
        if (referenceGray <= 0) {
            throw new IllegalArgumentException(
                    "referenceGray must be positive, but is " + referenceGray);
        }
        BufferedImage out = new BufferedImage(
                gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < gray.getHeight(); y++) {
            for (int x = 0; x < gray.getWidth(); x++) {
                int argb = gray.getRGB(x, y);
                int alpha = argb >>> 24;
                if (alpha == 0 || (argb & 0xFFFFFF) == CUTOUT) {
                    out.setRGB(x, y, argb);
                    continue;
                }
                int level = argb >> 16 & 0xFF;
                if ((argb >> 8 & 0xFF) != level || (argb & 0xFF) != level) {
                    throw new IllegalArgumentException("Pixel (%d, %d) must be gray, but is #%06X"
                            .formatted(x, y, argb & 0xFFFFFF));
                }
                int pixel = alpha << 24;
                for (int shift = 16; shift >= 0; shift -= 8) {
                    int scaled = (int) Math.round((colour >> shift & 0xFF) * level
                            / (double) referenceGray);
                    pixel |= Math.min(255, scaled) << shift;
                }
                out.setRGB(x, y, pixel);
            }
        }
        return out;
    }

    /**
     * Rotates an image 45° about its own center, cropped back to its original dimensions —
     * nearest-neighbour, so pixel-art edges stair-step instead of blurring.
     *
     * @param art the source image, left untouched; the result shares its dimensions
     */
    public static BufferedImage rotate45(BufferedImage art) {
        BufferedImage out = new BufferedImage(
                art.getWidth(), art.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.rotate(Math.toRadians(45), art.getWidth() / 2.0, art.getHeight() / 2.0);
        g.drawImage(art, 0, 0, null);
        g.dispose();
        return out;
    }

    /**
     * Lays an overlay over a base image, honouring the {@link #CUTOUT} chroma key.
     *
     * <p>Straight source-over, so the base shows wherever the overlay does not cover it: a fully
     * transparent overlay pixel is the base at its own colour, a fully opaque one is the overlay's
     * own pixel, and anything between is shading over whatever is underneath. {@link #CUTOUT} is
     * the one thing source-over cannot express, and is read off the overlay rather than off the
     * result so that a base which happened to contain the same magenta could not punch holes in
     * itself.
     *
     * @param base the image underneath, left untouched; the result takes its dimensions
     * @param overlay the image on top, which must match {@code base}'s dimensions
     */
    public static BufferedImage composite(BufferedImage base, BufferedImage overlay) {
        if (overlay.getWidth() != base.getWidth() || overlay.getHeight() != base.getHeight()) {
            throw new IllegalArgumentException(
                    "Overlay must be %d×%d to match its base, but is %d×%d".formatted(
                            base.getWidth(), base.getHeight(),
                            overlay.getWidth(), overlay.getHeight()));
        }
        BufferedImage out = new BufferedImage(
                base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < base.getHeight(); y++) {
            for (int x = 0; x < base.getWidth(); x++) {
                int top = overlay.getRGB(x, y);
                boolean cutout = (top >>> 24) != 0 && (top & 0xFFFFFF) == CUTOUT;
                out.setRGB(x, y, cutout ? 0 : over(base.getRGB(x, y), top));
            }
        }
        return out;
    }

    /**
     * Checks a source image is square at the size the caller works in, naming the file if not.
     *
     * <p>Art indexed pixel by pixel against a fixed size reads off the end of itself when it is not
     * that size. Worth saying which file rather than letting an
     * {@link ArrayIndexOutOfBoundsException} stand in for it: hand-edited art resaved at the wrong
     * size is the likeliest way this ever breaks.
     *
     * @param image the image to check, returned unchanged so a {@link #read} can be wrapped where
     *     it stands
     * @param edge the width and height the caller requires
     * @param name what to call the image if it fails, e.g. the resource path it was read from
     */
    public static BufferedImage sized(BufferedImage image, int edge, String name) {
        if (image.getWidth() != edge || image.getHeight() != edge) {
            throw new IllegalStateException("%s must be %d×%d, but is %d×%d".formatted(
                    name, edge, edge, image.getWidth(), image.getHeight()));
        }
        return image;
    }

    // One pixel of source-over: `top` composited onto `base`, both premultiplied out again so the
    // result is a plain ARGB pixel. Handles the fully transparent and fully opaque ends without
    // special cases, which is why composite has none.
    private static int over(int base, int top) {
        double topAlpha = (top >>> 24) / 255.0;
        double baseAlpha = (base >>> 24) / 255.0;
        double alpha = topAlpha + baseAlpha * (1.0 - topAlpha);
        if (alpha <= 0.0) {
            return 0;
        }
        int out = (int) Math.round(alpha * 255.0) << 24;
        for (int shift = 16; shift >= 0; shift -= 8) {
            double mixed = ((top >> shift & 0xFF) * topAlpha
                    + (base >> shift & 0xFF) * baseAlpha * (1.0 - topAlpha)) / alpha;
            out |= (int) Math.round(mixed) << shift;
        }
        return out;
    }
}
