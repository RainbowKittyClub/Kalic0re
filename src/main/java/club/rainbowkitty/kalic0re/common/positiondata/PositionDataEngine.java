package club.rainbowkitty.kalic0re.common.positiondata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

// The kinds this copy of the package has registered, and what the two hooks do to their marks.
final class PositionDataEngine {
    // An array, read on every block change, and replaced on the rare register.
    private static volatile PositionMarks[] kinds = new PositionMarks[0];

    private PositionDataEngine() {
    }

    // Throws when a kind of this copy already has id.
    static synchronized void requireFree(Identifier id) {
        for (PositionMarks kind : kinds) {
            if (kind.id().equals(id)) {
                throw new IllegalStateException("Position marks " + id + " are already registered");
            }
        }
    }

    static synchronized void add(PositionMarks kind) {
        PositionMarks[] next = Arrays.copyOf(kinds, kinds.length + 1);
        next[kinds.length] = kind;
        kinds = next;
    }

    // Drops a mark at pos unless both the old and the new state hold its kind, so a stale mark
    // never reaches a new holder; a block landing from a moving piston keeps its placed marks.
    static void blockChanged(LevelChunk chunk, BlockPos pos, BlockState old, BlockState state) {
        if (chunk.getLevel().isClientSide()) {
            return;
        }
        boolean landed = old.is(Blocks.MOVING_PISTON);
        for (PositionMarks kind : kinds) {
            if (!kind.holds(state) || !landed && !kind.holds(old)) {
                kind.remove(chunk, pos);
            }
        }
    }

    // Takes the marks of every block a piston is about to move, keyed by where each block lands.
    static PositionDataHooks.Carry take(Level level, List<BlockPos> toPush, Direction direction) {
        Map<BlockPos, List<PositionMarks>> carried = new HashMap<>();
        for (BlockPos pos : toPush) {
            LevelChunk chunk = level.getChunkAt(pos);
            for (PositionMarks kind : kinds) {
                if (kind.has(chunk, pos)) {
                    kind.remove(chunk, pos);
                    carried.computeIfAbsent(pos.relative(direction), target -> new ArrayList<>())
                            .add(kind);
                }
            }
        }
        return new PositionDataHooks.Carry(carried);
    }
}
