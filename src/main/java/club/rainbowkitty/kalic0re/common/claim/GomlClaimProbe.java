package club.rainbowkitty.kalic0re.common.claim;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Get Off My Lawn. Its use-block check ({@code EventHandlers}'s {@code UseBlockCallback}) lets
 * anyone use a block its config or {@code goml:allowed_interactions} names, unless they hold a
 * block item; everyone else must be able to modify every claim at the block and at the position
 * beside the face they clicked. {@link ClaimUtils#canModify} is that membership test.
 */
public final class GomlClaimProbe implements ClaimProbe {
    private static final TagKey<Block> ALLOWED_INTERACTIONS = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath("goml", "allowed_interactions"));

    @Override
    public boolean refusesWithoutExemption(ServerPlayer player, BlockPos pos) {
        return !ClaimUtils.canModify(player.level(), pos, player);
    }

    @Override
    public boolean allowsStandIn(ServerPlayer player, InteractionHand hand, BlockHitResult hit,
            BlockState standIn) {
        if (!(player.getItemInHand(hand).getItem() instanceof BlockItem)
                && (GetOffMyLawn.CONFIG.canInteract(standIn.getBlock())
                        || standIn.is(ALLOWED_INTERACTIONS))) {
            return true;
        }
        Level level = player.level();
        BlockPos pos = hit.getBlockPos();
        return ClaimUtils.canModify(level, pos, player)
                && ClaimUtils.canModify(level, pos.relative(hit.getDirection()), player);
    }
}
