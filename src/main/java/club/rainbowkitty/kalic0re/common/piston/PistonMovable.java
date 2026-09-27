package club.rainbowkitty.kalic0re.common.piston;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Blocks with a block entity that pistons may move anyway, when a registered rule allows it.
 * Vanilla refuses every such block ({@code PistonBaseBlock#isPushable}'s last line,
 * {@code !state.hasBlockEntity()}), the crafter included.
 *
 * <p>A piston removes a pushed block's block entity without running its removal side effects, so
 * a rule must only allow a block whose block entity holds nothing worth keeping: its items would be
 * deleted, not dropped. The moved block gets a fresh block entity where it lands.
 *
 * <p>This holds the rules; each consumer owns the mixin that consults them, since mixins are never
 * shared code: a wrap of {@code hasBlockEntity()} in {@code isPushable} that answers false when
 * {@link #allows} does.
 */
public final class PistonMovable {
    private static final List<Rule> RULES = new ArrayList<>();

    private PistonMovable() {
    }

    /** Adds a rule, consulted for every block with a block entity a piston tries to move. */
    public static void register(Rule rule) {
        RULES.add(rule);
    }

    /** Whether any rule lets a piston move this block despite its block entity. */
    public static boolean allows(BlockState state, Level level, BlockPos pos) {
        for (Rule rule : RULES) {
            if (rule.allows(state, level, pos)) {
                return true;
            }
        }
        return false;
    }

    /** One block's permission to be moved by a piston. */
    @FunctionalInterface
    public interface Rule {
        /** Whether a piston may move this block, block entity and all, now. */
        public boolean allows(BlockState state, Level level, BlockPos pos);
    }
}
