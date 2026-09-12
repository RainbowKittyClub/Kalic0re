package club.rainbowkitty.rkcore.common.effect;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Grants and revokes creative-like flight through {@link Abilities#mayfly}, the same field
 * {@code ServerPlayerGameMode.changeGameModeForPlayer} toggles on a gamemode change — so an
 * equipment-driven flight effect behaves the way that switch already does instead of inventing its
 * own rule.
 *
 * <p>Never touches a player whose own gamemode already owns flight (creative or spectator): that
 * ability belongs to the gamemode, and a {@link PlayerEffectHooks#onDeactivate} firing on unequip
 * must not claw it back out from under an unrelated {@code /gamemode} state. On revoke,
 * {@link Abilities#flying} itself is only forced off once the player is back in range of the
 * ground — vanilla's own {@code isInRangeOfGround} is private, so this ports its body through the
 * public pieces it is built from ({@link Entity#collectAllColliders} and
 * {@link Entity#getAvailableSpaceBelow}) — so unequipping mid-air lets the player fall naturally
 * instead of freezing them in place the instant the item comes off.
 */
public final class PlayerFlight {
    private static final double FLIGHT_DISABLE_RANGE = 1.0;

    private PlayerFlight() {
    }

    /** Grants mayfly and syncs the client, unless the player's own gamemode already grants it. */
    public static void grant(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    /**
     * Revokes mayfly and syncs the client, unless the player's own gamemode owns it. Only forces
     * {@link Abilities#flying} off once the player is near the ground, the same condition
     * {@code ServerPlayerGameMode.changeGameModeForPlayer} uses.
     */
    public static void revoke(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        Abilities abilities = player.getAbilities();
        abilities.mayfly = false;
        if (abilities.flying && isInRangeOfGround(player)) {
            abilities.flying = false;
        }
        player.onUpdateAbilities();
    }

    private static boolean isInRangeOfGround(ServerPlayer player) {
        List<VoxelShape> clipping = Entity.collectAllColliders(player, player.level(),
                player.getBoundingBox());
        return clipping.isEmpty() && player.getAvailableSpaceBelow(FLIGHT_DISABLE_RANGE) < 1.0;
    }
}
