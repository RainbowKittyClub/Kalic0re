package club.rainbowkitty.rkcore.common.label;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;

/** Labels blocks of one kind for {@link HoverScanner}. */
public interface BlockLabels {
    /** The label for the block that was hit, or null when it has nothing to show. */
    LabelTarget target(ServerPlayer viewer, ServerLevel level, BlockHitResult hit);
}
