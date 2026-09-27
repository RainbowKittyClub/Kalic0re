package club.rainbowkitty.kalic0re.common.claim;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.data.IPermissionContainer;
import io.github.flemmli97.flan.api.permission.BuiltinPermission;
import io.github.flemmli97.flan.api.permission.InteractionOverrideManager;
import io.github.flemmli97.flan.claim.Claim;
import io.github.flemmli97.flan.claim.ClaimStorage;
import io.github.flemmli97.flan.claim.attachment.ClaimAllowListKey;
import io.github.flemmli97.flan.config.ConfigHandler;
import io.github.flemmli97.flan.event.BlockInteractEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Flan. Its use-block check ({@code BlockInteractEvents#useBlocks}) exempts a block through a
 * claim's allow list, its config's id blacklist or an interaction override; any block with a
 * container block entity otherwise needs {@code open_container}. Only a container is probed here,
 * the one kind of block a caller exempts.
 */
public final class FlanClaimProbe implements ClaimProbe {
    @Override
    public boolean refusesWithoutExemption(ServerPlayer player, BlockPos pos) {
        return !ClaimHandler.canInteract(player, pos, BuiltinPermission.OPENCONTAINER);
    }

    // useBlocks' steps that read the block, run on the stand-in; the rest as for any container.
    @Override
    public boolean allowsStandIn(ServerPlayer player, InteractionHand hand, BlockHitResult hit,
            BlockState standIn) {
        ServerLevel level = player.level();
        BlockPos pos = hit.getBlockPos();
        IPermissionContainer claim = ClaimStorage.get(level).getForPermissionCheck(pos);
        if (claim == null) {
            return true;
        }
        if (claim instanceof Claim real && real.allowedEntries.isAllowed(
                ClaimAllowListKey.BLOCK_USE, standIn::is, standIn::is)) {
            return true;
        }
        Identifier id = BuiltInRegistries.BLOCK.getKey(standIn.getBlock());
        if (BlockInteractEvents.contains(id, level.getBlockEntity(pos),
                ConfigHandler.CONFIG.interactBlockBlacklist,
                ConfigHandler.CONFIG.interactBlockEntityTagBlacklist)) {
            return true;
        }
        Identifier permission =
                InteractionOverrideManager.getInstance().getBlockInteract(standIn.getBlock());
        if (permission == null || permission.equals(BuiltinPermission.PROJECTILES)
                || permission.equals(BuiltinPermission.PRESSUREPLATE)) {
            permission = BuiltinPermission.OPENCONTAINER;
        }
        return claim.canInteract(player, permission, pos, false);
    }
}
