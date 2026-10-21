package club.rainbowkitty.kalic0re.common.portal;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.LongStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Where a portal stands: a region of interior blocks joined at their faces, and the ring of frame
 * blocks a face away from it. Blocks that only touch the interior at an edge, as a rectangle's
 * corners do, are not part of the ring: no shape update from them could ever reach the portal.
 *
 * @param dimension the level the frame is in
 * @param origin the corner of the interior's bounding box with the smallest coordinate on both
 *        in-plane axes, which need not be an interior block
 * @param plane the plane the frame lies in
 * @param width the bounding box along {@link PortalPlane#along}, 1 to {@link #MAX_SIZE}
 * @param height the bounding box along {@link PortalPlane#up}, 1 to {@link #MAX_SIZE}
 * @param cells which blocks of the box are interior, row by row ({@code v * width + u}); null
 *        when all of them are, which every rectangle is stored as
 */
public record PortalFrame(ResourceKey<Level> dimension, BlockPos origin, PortalPlane plane,
        int width, int height, @Nullable BitSet cells) {
    public static final int MAX_SIZE = 32;
    private static final Codec<BitSet> CELLS_CODEC = Codec.LONG_STREAM.comapFlatMap(
            stream -> {
                BitSet cells = BitSet.valueOf(stream.toArray());
                return cells.isEmpty() ? DataResult.error(() -> "PortalFrame cells are empty")
                        : DataResult.success(cells);
            },
            cells -> LongStream.of(cells.toLongArray()));
    public static final Codec<PortalFrame> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(PortalFrame::dimension),
                    BlockPos.CODEC.fieldOf("origin").forGetter(PortalFrame::origin),
                    PortalPlane.CODEC.fieldOf("plane").forGetter(PortalFrame::plane),
                    Codec.intRange(1, MAX_SIZE).fieldOf("width").forGetter(PortalFrame::width),
                    Codec.intRange(1, MAX_SIZE).fieldOf("height").forGetter(PortalFrame::height),
                    CELLS_CODEC.optionalFieldOf("cells")
                            .forGetter(frame -> Optional.ofNullable(frame.cells)))
            .apply(instance, (dimension, origin, plane, width, height, cells) ->
                    new PortalFrame(dimension, origin, plane, width, height, cells.orElse(null))));

    /** Copies {@code cells}, keeping only bits inside the box, and stores a full box as null. */
    public PortalFrame {
        if (width < 1 || width > MAX_SIZE || height < 1 || height > MAX_SIZE) {
            throw new IllegalArgumentException("PortalFrame sides must be 1.." + MAX_SIZE
                    + ", got width=" + width + " height=" + height);
        }
        if (cells != null) {
            BitSet kept = cells.get(0, width * height);
            if (kept.isEmpty()) {
                throw new IllegalArgumentException("PortalFrame has no interior cell");
            }
            cells = kept.cardinality() == width * height ? null : kept;
        }
    }

    /** A rectangle filled to its {@code width} by {@code height} box. */
    public PortalFrame(ResourceKey<Level> dimension, BlockPos origin, PortalPlane plane,
            int width, int height) {
        this(dimension, origin, plane, width, height, null);
    }

    /** Which blocks of the box are interior, as a copy; null for a rectangle. */
    @Override
    public @Nullable BitSet cells() {
        return cells == null ? null : (BitSet) cells.clone();
    }

    /** Whether the interior fills its whole bounding box. */
    public boolean isRectangle() {
        return cells == null;
    }

    /** Whether the block {@code u} along and {@code v} up from the origin is interior. */
    public boolean hasCell(int u, int v) {
        return inside(u, width) && inside(v, height)
                && (cells == null || cells.get(v * width + u));
    }

    /** How many interior blocks there are. */
    public int area() {
        return cells == null ? width * height : cells.cardinality();
    }

    /** The block {@code u} along and {@code v} up from the origin. */
    public BlockPos at(int u, int v) {
        return origin.relative(plane.along(), u).relative(plane.up(), v);
    }

    /** Every interior block; a rectangle's come as reused mutable positions. */
    public Iterable<BlockPos> interior() {
        if (cells == null) {
            return BlockPos.betweenClosed(origin, at(width - 1, height - 1));
        }
        List<BlockPos> blocks = new ArrayList<>(cells.cardinality());
        for (int i = cells.nextSetBit(0); i >= 0; i = cells.nextSetBit(i + 1)) {
            blocks.add(at(i % width, i / width));
        }
        return blocks;
    }

    /** The ring: every block a face away from the interior and not in it, row by row. */
    public List<BlockPos> ring() {
        List<BlockPos> ring = new ArrayList<>(2 * (width + height));
        for (int v = -1; v <= height; v++) {
            for (int u = -1; u <= width; u++) {
                if (isRingCell(u, v)) {
                    ring.add(at(u, v));
                }
            }
        }
        return ring;
    }

    /** The centre of the interior's bounding box, in the middle of the plane. */
    public Vec3 centre() {
        return Vec3.atCenterOf(origin).add(Vec3.atLowerCornerOf(at(width - 1, height - 1))
                .subtract(Vec3.atLowerCornerOf(origin)).scale(0.5));
    }

    /** How many blocks the ring has. */
    public int ringSize() {
        return cells == null ? 2 * (width + height) : ring().size();
    }

    /** Whether {@code pos} is one of the interior blocks. */
    public boolean interiorContains(BlockPos pos) {
        return inPlane(pos) && hasCell(along(pos), up(pos));
    }

    /** Whether {@code pos} is one of the ring blocks. */
    public boolean ringContains(BlockPos pos) {
        return inPlane(pos) && isRingCell(along(pos), up(pos));
    }

    /** Whether this frame's plane, box and interior cells equal {@code other}'s. */
    public boolean sameShape(PortalFrame other) {
        return plane == other.plane && width == other.width && height == other.height
                && Objects.equals(cells, other.cells);
    }

    /** The chunks the ring and interior touch, as {@link ChunkPos#pack(int, int)} keys. */
    public long[] chunks() {
        BlockPos low = at(-1, -1);
        BlockPos high = at(width, height);
        int x0 = SectionPos.blockToSectionCoord(Math.min(low.getX(), high.getX()));
        int x1 = SectionPos.blockToSectionCoord(Math.max(low.getX(), high.getX()));
        int z0 = SectionPos.blockToSectionCoord(Math.min(low.getZ(), high.getZ()));
        int z1 = SectionPos.blockToSectionCoord(Math.max(low.getZ(), high.getZ()));
        long[] chunks = new long[(x1 - x0 + 1) * (z1 - z0 + 1)];
        int i = 0;
        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                chunks[i++] = ChunkPos.pack(x, z);
            }
        }
        return chunks;
    }

    // Not interior, with an interior block on one of its four in-plane faces.
    private boolean isRingCell(int u, int v) {
        return !hasCell(u, v) && (hasCell(u - 1, v) || hasCell(u + 1, v) || hasCell(u, v - 1)
                || hasCell(u, v + 1));
    }

    private boolean inPlane(BlockPos pos) {
        return pos.get(plane.normal()) == origin.get(plane.normal());
    }

    private int along(BlockPos pos) {
        return pos.get(plane.along()) - origin.get(plane.along());
    }

    private int up(BlockPos pos) {
        return pos.get(plane.up()) - origin.get(plane.up());
    }

    private static boolean inside(int offset, int size) {
        return offset >= 0 && offset < size;
    }
}
