package club.rainbowkitty.kalic0re.common.claim;

import xaero.pac.common.server.ServerData;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.claims.protection.ChunkProtection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Open Parties and Claims. Its API's {@code onBlockInteraction} can skip the target block's
 * exceptions, which answers the first question. The second needs the internal overload that takes
 * the block state rather than reading it from the level ({@code ChunkProtection}); being internal,
 * it is the call most likely to move, and {@link ClaimExemptions} disables a probe that stops
 * linking.
 */
public final class OpacClaimProbe implements ClaimProbe {
    @Override
    public boolean refusesWithoutExemption(ServerPlayer player, BlockPos pos) {
        return OpenPACServerAPI.get(player.level().getServer()).getChunkProtection()
                .onBlockInteraction(player, InteractionHand.MAIN_HAND, null, player.level(), pos,
                        Direction.UP, false, false, false);
    }

    // The internal overload's generics are the server data's own; raw here, as the API leaves them.
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean allowsStandIn(ServerPlayer player, InteractionHand hand, BlockHitResult hit,
            BlockState standIn) {
        MinecraftServer server = player.level().getServer();
        ChunkProtection protection =
                (ChunkProtection) OpenPACServerAPI.get(server).getChunkProtection();
        return !protection.onBlockInteraction(ServerData.from(server), standIn, player, hand, null,
                player.level(), hit.getBlockPos(), hit.getDirection(), false, false);
    }
}
