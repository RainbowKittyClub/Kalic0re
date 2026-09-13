package club.rainbowkitty.kalic0re.common.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Callbacks a {@link PlayerEffect} fires against the player it is active on. Every method is a
 * no-op by default, so a consumer overrides only the hooks its effect actually needs.
 */
public interface PlayerEffectHooks {
    /** Fires the tick {@link PlayerEffect#source()} first reports active for this player. */
    default void onActivate(ServerPlayer player) {
    }

    /** Fires the tick {@link PlayerEffect#source()} stops reporting active for this player. */
    default void onDeactivate(ServerPlayer player) {
    }

    /** Fires every tick the effect is active, including the tick {@link #onActivate} also fires. */
    default void onTick(ServerPlayer player) {
    }

    /**
     * Fires when the player takes damage while the effect is active.
     *
     * @param amount the damage actually applied, after armor and other reductions
     */
    default void onDamageTaken(ServerPlayer player, DamageSource source, float amount) {
    }

    /**
     * Fires when the player deals damage to another entity while the effect is active.
     *
     * @param amount the damage actually applied, after the target's armor and other reductions
     */
    default void onDamageDealt(ServerPlayer player, LivingEntity target, DamageSource source,
            float amount) {
    }

    /** Fires when the player dies while the effect is active. */
    default void onDeath(ServerPlayer player, DamageSource source) {
    }
}
