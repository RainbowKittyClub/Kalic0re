package club.rainbowkitty.kalic0re.common.positiondata;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * One kind of presence-only mark on block positions, such as "a player placed this log". A mark
 * lives only while the block at its position holds the kind, follows its block through a piston,
 * and never reaches a client. Positions without a mark cost nothing.
 *
 * <p>Register each kind from the mod initializer, with an id in the consumer's own namespace: a
 * chunk read before its kind exists loses that kind's marks on its next save. Registering an id
 * twice in one copy throws. Two mods' relocated copies keep separate kinds, so only an id shared
 * between them can collide, which is why the namespace is the consumer's own.
 *
 * <p>Marks are kept only as long as {@link PositionDataHooks} is told about block changes, and this
 * package has no mixin: the consumer owns the three that call it, since mixins are never shared
 * code.
 * <ul>
 * <li>{@code LevelChunk#setBlockState}: a {@code @ModifyReturnValue} that passes the replaced state
 * to {@link PositionDataHooks#blockChanged} when it is not null.
 * <li>{@code PistonBaseBlock#moveBlocks}: a {@code @WrapOperation} on
 * {@code PistonStructureResolver#getToDestroy} that calls {@link PositionDataHooks#take}, and
 * one on {@code MovingPistonBlock#newMovingBlockEntity}, for every block but the source piston,
 * that calls {@link PositionDataHooks.Carry#land}, sharing the carry between the two.
 * <li>whatever places the blocks a kind marks: a call to {@link #mark} once the block is set.
 * </ul>
 */
public final class PositionMarks {
    // Sorted chunk-local positions, 4 bytes a mark on disk and the same bytes for the same marks.
    private static final Codec<int[]> CODEC =
            Codec.INT_STREAM.comapFlatMap(PositionMarks::decode, Arrays::stream);

    private final Identifier id;
    private final Predicate<BlockState> holds;
    private final AttachmentType<int[]> type;

    private PositionMarks(Identifier id, Predicate<BlockState> holds) {
        this.id = id;
        this.holds = holds;
        this.type = AttachmentRegistry.create(id, builder -> builder.persistent(CODEC));
    }

    /**
     * Registers a kind. Call it during mod initialization, before any world loads.
     *
     * @param id the kind's id, also the id of its chunk attachment
     * @param holds the block states that keep a mark at their position
     * @throws IllegalStateException when this copy of the package has registered {@code id}
     */
    public static PositionMarks register(Identifier id, Predicate<BlockState> holds) {
        PositionDataEngine.requireFree(id);
        PositionMarks marks = new PositionMarks(id, holds);
        PositionDataEngine.add(marks);
        return marks;
    }

    /** The kind's id. */
    public Identifier id() {
        return id;
    }

    /** Whether a block in this state keeps a mark of this kind at its position. */
    public boolean holds(BlockState state) {
        return holds.test(state);
    }

    /**
     * Whether {@code pos} is marked. Never loads a chunk, and answers {@link Presence#UNKNOWN} on a
     * client, in a chunk that is not loaded, or off the server thread. A block that does not hold
     * the kind reads {@link Presence#ABSENT}, whatever is stored under it.
     */
    public Presence presence(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)
                || !serverLevel.getServer().isSameThread()) {
            return Presence.UNKNOWN;
        }
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) {
            return Presence.UNKNOWN;
        }
        return has(chunk, pos) && holds.test(chunk.getBlockState(pos))
                ? Presence.PRESENT : Presence.ABSENT;
    }

    /**
     * Marks {@code pos}, loading its chunk. Does nothing on a client, nor where the block does not
     * hold the kind.
     *
     * @throws IllegalStateException off the server thread
     */
    public void mark(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }
        requireServerThread(level);
        ChunkAccess chunk = level.getChunkAt(pos);
        if (holds.test(chunk.getBlockState(pos))) {
            add(chunk, pos);
        }
    }

    /**
     * Removes the mark at {@code pos}, loading its chunk. Does nothing on a client.
     *
     * @throws IllegalStateException off the server thread
     */
    public void unmark(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }
        requireServerThread(level);
        remove(level.getChunkAt(pos), pos);
    }

    // Whether chunk stores a mark at pos, whether or not the block there holds the kind.
    boolean has(ChunkAccess chunk, BlockPos pos) {
        int[] marks = target(chunk).getAttached(type);
        return marks != null && Arrays.binarySearch(marks, pack(pos)) >= 0;
    }

    // Stores a mark at pos, replacing the whole array so the same marks always save the same bytes.
    void add(ChunkAccess chunk, BlockPos pos) {
        int[] marks = target(chunk).getAttached(type);
        int key = pack(pos);
        if (marks == null) {
            target(chunk).setAttached(type, new int[] {key});
            return;
        }
        int at = Arrays.binarySearch(marks, key);
        if (at >= 0) {
            return;
        }
        int insert = -at - 1;
        int[] next = new int[marks.length + 1];
        System.arraycopy(marks, 0, next, 0, insert);
        next[insert] = key;
        System.arraycopy(marks, insert, next, insert + 1, marks.length - insert);
        target(chunk).setAttached(type, next);
    }

    // Drops the mark at pos, and the attachment once its last mark goes.
    void remove(ChunkAccess chunk, BlockPos pos) {
        int[] marks = target(chunk).getAttached(type);
        if (marks == null) {
            return;
        }
        int at = Arrays.binarySearch(marks, pack(pos));
        if (at < 0) {
            return;
        }
        if (marks.length == 1) {
            target(chunk).removeAttached(type);
            return;
        }
        int[] next = new int[marks.length - 1];
        System.arraycopy(marks, 0, next, 0, at);
        System.arraycopy(marks, at + 1, next, at, next.length - at);
        target(chunk).setAttached(type, next);
    }

    // The position inside its chunk: x and z in the low byte, y above, sign kept by the shift.
    private static int pack(BlockPos pos) {
        return (pos.getY() << 8) | ((pos.getZ() & 15) << 4) | (pos.getX() & 15);
    }

    private static void requireServerThread(Level level) {
        if (!((ServerLevel) level).getServer().isSameThread()) {
            throw new IllegalStateException("Position marks are changed on the server thread only");
        }
    }

    // The chunk as Fabric's attachment target, which ChunkAccess implements at runtime; Loom does
    // not inject that interface from kalic0re's compileOnly fabric-api.
    private static AttachmentTarget target(ChunkAccess chunk) {
        return (AttachmentTarget) chunk;
    }

    // Sorted and without repeats however the file was written; none left means nothing to keep.
    private static DataResult<int[]> decode(IntStream stream) {
        int[] marks = stream.sorted().distinct().toArray();
        return marks.length == 0
                ? DataResult.error(() -> "No mark left to load")
                : DataResult.success(marks);
    }
}
