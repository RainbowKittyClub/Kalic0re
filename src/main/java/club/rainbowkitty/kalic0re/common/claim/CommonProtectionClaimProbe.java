package club.rainbowkitty.kalic0re.common.claim;

import java.util.Set;

import eu.pb4.common.protection.api.CommonProtection;
import eu.pb4.common.protection.api.ProtectionProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Every other claim mod implementing Patbox's Common Protection API, best effort. A provider's
 * {@code canInteractBlock} may or may not count its own exemptions, and can only be asked about the
 * block actually in the world, so an unknown mod judges a stand-in block by its own id. GOML's and
 * Flan's providers are skipped: their own probes answer for them, and GOML's counts its config
 * exemption.
 */
public final class CommonProtectionClaimProbe implements ClaimProbe {
    private static final Set<Identifier> PROBED_ELSEWHERE = Set.of(
            Identifier.fromNamespaceAndPath("goml", "claim_protection"),
            Identifier.fromNamespaceAndPath("flan", "provider"));

    @Override
    public boolean refusesWithoutExemption(ServerPlayer player, BlockPos pos) {
        return !allProvidersAllow(player, pos);
    }

    @Override
    public boolean allowsStandIn(ServerPlayer player, InteractionHand hand, BlockHitResult hit,
            BlockState standIn) {
        return allProvidersAllow(player, hit.getBlockPos());
    }

    // Whether every provider not probed elsewhere lets the player use the block at pos.
    private static boolean allProvidersAllow(ServerPlayer player, BlockPos pos) {
        for (Identifier id : CommonProtection.getProviderIds()) {
            ProtectionProvider provider = CommonProtection.getProvider(id);
            if (!PROBED_ELSEWHERE.contains(id) && provider != null && !provider.canInteractBlock(
                    player.level(), pos, player.nameAndId(), player)) {
                return false;
            }
        }
        return true;
    }
}
