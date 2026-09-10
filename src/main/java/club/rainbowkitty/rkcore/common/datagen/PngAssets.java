package club.rainbowkitty.rkcore.common.datagen;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;

import net.minecraft.data.CachedOutput;
import net.minecraft.util.Util;

/**
 * PNG helpers for datagen providers that write generated images. Shared by relocation, not a mod
 * dependency (see each consuming mod's {@code build.gradle}).
 */
public final class PngAssets {
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
}
