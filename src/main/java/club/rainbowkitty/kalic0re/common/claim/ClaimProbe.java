package club.rainbowkitty.kalic0re.common.claim;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * One claim mod's answers to the two questions its own block-use check cannot be asked directly.
 * Claim mods exempt blocks by id, so each implementation reruns its mod's check without the
 * exemption, or as if another block stood in the place of the real one.
 */
public interface ClaimProbe {
    /**
     * Whether the claim mod would refuse this player the block at {@code pos} if it made no
     * exemption for that block: whether they are there only by an exemption, when it lets them in.
     */
    public boolean refusesWithoutExemption(ServerPlayer player, BlockPos pos);

    /**
     * Whether the claim mod would let this player use {@code standIn}, were it the block at
     * {@code hit}'s position, with what they hold in {@code hand}.
     */
    public boolean allowsStandIn(ServerPlayer player, InteractionHand hand, BlockHitResult hit,
            BlockState standIn);
}
