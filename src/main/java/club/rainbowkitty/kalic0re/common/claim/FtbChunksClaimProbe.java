package club.rainbowkitty.kalic0re.common.claim;

import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.api.FTBChunksProperties;
import dev.ftb.mods.ftbchunks.api.FTBChunksTags;
import dev.ftb.mods.ftbchunks.api.Protection;
import dev.ftb.mods.ftbchunks.api.ProtectionPolicy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * FTB Chunks. On Fabric its use-block check is {@code Protection.EDIT_AND_INTERACT_BLOCK}, which
 * allows anything in {@code ftbchunks:interact_whitelist} before looking at the claim; its manager
 * takes any {@link Protection}, so both questions are that rule with the tag check dropped or run
 * on the stand-in.
 *
 * <p>Compiled against FTB Chunks 26.1.2, there being no 26.2 build yet.
 */
public final class FtbChunksClaimProbe implements ClaimProbe {
    // EDIT_AND_INTERACT_BLOCK without its whitelist: the claim's own members only.
    private static final Protection MEMBERS_ONLY = (player, pos, hand, chunk, entity) ->
            chunk != null && chunk.getTeamData().canPlayerUse(player,
                    FTBChunksProperties.BLOCK_EDIT_AND_INTERACT_MODE)
                    ? ProtectionPolicy.ALLOW : ProtectionPolicy.CHECK;

    @Override
    public boolean refusesWithoutExemption(ServerPlayer player, BlockPos pos) {
        return FTBChunksAPI.api().getManager().shouldPreventInteraction(player,
                InteractionHand.MAIN_HAND, pos, MEMBERS_ONLY, null);
    }

    @Override
    public boolean allowsStandIn(ServerPlayer player, InteractionHand hand, BlockHitResult hit,
            BlockState standIn) {
        Protection asStandIn = (actor, pos, actorHand, chunk, entity) ->
                standIn.is(FTBChunksTags.Blocks.INTERACT_WHITELIST_TAG)
                        ? ProtectionPolicy.ALLOW
                        : MEMBERS_ONLY.getProtectionPolicy(actor, pos, actorHand, chunk, entity);
        return !FTBChunksAPI.api().getManager().shouldPreventInteraction(player, hand,
                hit.getBlockPos(), asStandIn, null);
    }
}
