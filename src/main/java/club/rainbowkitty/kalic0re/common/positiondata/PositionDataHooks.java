package club.rainbowkitty.kalic0re.common.positiondata;

import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The calls a consumer's mixins make to keep every {@link PositionMarks} kind true to its blocks.
 * See {@link PositionMarks} for the mixin sites.
 */
public final class PositionDataHooks {
    private PositionDataHooks() {
    }

    /**
     * A block in {@code chunk} was replaced. Every mark at {@code pos} stays only if the new state
     * holds the kind and the old one did, or the old state was a moving piston (a block landing
     * with the mark placed for it). Stripping a log therefore keeps its mark. Does nothing on a
     * client.
     */
    public static void blockChanged(LevelChunk chunk, BlockPos pos, BlockState old,
            BlockState state) {
        PositionDataEngine.blockChanged(chunk, pos, old, state);
    }

    /**
     * Takes the marks of the blocks a piston will move, once vanilla has settled which they are and
     * before any is set or broken; {@code direction} is where they go. Hand the result to
     * {@link Carry#land} as each destination becomes a moving piston. A client gets an empty carry.
     */
    public static Carry take(Level level, List<BlockPos> toPush, Direction direction) {
        if (level.isClientSide()) {
            return new Carry(Map.of());
        }
        return PositionDataEngine.take(level, toPush, direction);
    }

    /** Marks taken from moving blocks, waiting for their destinations. */
    public static final class Carry {
        private final Map<BlockPos, List<PositionMarks>> carried;

        Carry(Map<BlockPos, List<PositionMarks>> carried) {
            this.carried = carried;
        }

        /**
         * Puts back what was taken for {@code pos}, under the moving-piston block that is there
         * now: a mark of a kind the {@code moved} state holds. It waits there for the block to
         * land, and a block broken in flight loses it.
         */
        public void land(Level level, BlockPos pos, BlockState moved) {
            List<PositionMarks> landing = carried.remove(pos);
            if (landing == null) {
                return;
            }
            LevelChunk chunk = level.getChunkAt(pos);
            for (PositionMarks kind : landing) {
                if (kind.holds(moved)) {
                    kind.add(chunk, pos);
                }
            }
        }
    }
}
