package club.rainbowkitty.rkcore.common.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;

/**
 * Grants and revokes creative-like flight through {@link Abilities#mayfly}, the same field
 * {@code ServerPlayerGameMode.changeGameModeForPlayer} toggles on a gamemode change — so an
 * equipment-driven flight effect behaves the way that switch already does instead of inventing its
 * own rule.
 *
 * <p>Never touches a player whose own gamemode already owns flight (creative or spectator): that
 * ability belongs to the gamemode, and a {@link PlayerEffectHooks#onDeactivate} firing on unequip
 * must not claw it back out from under an unrelated {@code /gamemode} state.
 *
 * <p>Unlike {@code changeGameModeForPlayer}, revoke clears {@link Abilities#flying} immediately
 * instead of deferring it until the player is back near the ground. That deferral exists so an
 * admin's {@code /gamemode} demotion doesn't yank a player out of the sky mid-flight; but on a
 * server with {@code allow-flight=false} (the default), leaving {@code flying=true} with
 * {@code mayfly=false} is exactly the state {@code ServerGamePacketListenerImpl}'s anti-float
 * check watches for, and it kicks the player for "floating too long" within a few seconds. An
 * equipment-driven revoke is involuntary the same way running out of {@code LEVITATION} is, so it
 * should end flight outright rather than borrow logic meant for a rare admin action.
 */
public final class PlayerFlight {
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

    /** Revokes mayfly and flying and syncs the client, unless the player's own gamemode owns it. */
    public static void revoke(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        Abilities abilities = player.getAbilities();
        abilities.mayfly = false;
        abilities.flying = false;
        player.onUpdateAbilities();
    }
}
