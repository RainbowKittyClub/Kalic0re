package club.rainbowkitty.kalic0re.common.positiondata;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/** Optional FAWE position-data codec declarations for this copy's registered mark kinds. */
public final class PositionDataCodecs {
    private PositionDataCodecs() {
    }

    /**
     * Returns protocol-v1 declarations without a FAWE dependency. A consumer's own
     * {@code fawe:position_data} entrypoint supplies these maps after registering its mark kinds.
     * {@code id} is the attachment id; {@code version} identifies one value's encoding. The pure
     * {@code holds} predicate accepts a block state. Owner-thread {@code read} returns one detached
     * compound or null for absence. Owner-thread {@code prepare} validates an optional compound and
     * returns a position consumer that restores it after the block write; empty means removal.
     * Preparing a value never mutates the world. Each shaded consumer must supply its own copy.
     */
    public static List<Map<String, Object>> declarations() {
        return PositionDataEngine.kinds().stream().map(PositionDataCodecs::declaration).toList();
    }

    // Describes one attachment, never its chunk-wide array.
    private static Map<String, Object> declaration(PositionMarks marks) {
        Predicate<BlockState> holds = marks::holds;
        BiFunction<ServerLevel, BlockPos, @Nullable CompoundTag> read = (level, pos) -> {
            LevelChunk chunk = loaded(level, pos);
            if (!marks.has(chunk, pos)) {
                return null;
            }
            if (!marks.holds(chunk.getBlockState(pos))) {
                throw new IllegalStateException("Stale position mark " + marks.id() + " at " + pos);
            }
            CompoundTag value = new CompoundTag();
            value.putBoolean("present", true);
            return value;
        };
        BiFunction<ServerLevel, Optional<CompoundTag>, Consumer<BlockPos>> prepare =
                (level, payload) -> {
                    owner(level);
                    if (payload.isPresent() && (!payload.get().keySet().equals(Set.of("present"))
                            || !ByteTag.ONE.equals(payload.get().get("present")))) {
                        throw new IllegalArgumentException("Invalid position mark " + marks.id());
                    }
                    boolean present = payload.isPresent();
                    return pos -> {
                        LevelChunk chunk = loaded(level, pos);
                        if (present) {
                            if (!marks.holds(chunk.getBlockState(pos))) {
                                throw new IllegalStateException(
                                        "Position mark cannot live at " + pos);
                            }
                            marks.add(chunk, pos);
                        } else {
                            marks.remove(chunk, pos);
                        }
                    };
                };
        return Map.of("id", marks.id().toString(), "version", 1, "holds", holds,
                "read", read, "prepare", prepare);
    }

    // Uses only a ticketed, loaded chunk; restoration must not trigger generation.
    private static LevelChunk loaded(ServerLevel level, BlockPos pos) {
        owner(level);
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) {
            throw new IllegalStateException("Position-data chunk is not loaded at " + pos);
        }
        return chunk;
    }

    // Both reading and restoring attachment data belong to the server thread.
    private static void owner(ServerLevel level) {
        if (!level.getServer().isSameThread()) {
            throw new IllegalStateException("Position-data codecs run on the server thread only");
        }
    }
}
