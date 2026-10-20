package club.rainbowkitty.kalic0re.common.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Runs every registered {@link PlayerEffect} against every online player.
 *
 * <p>There is no Fabric API event for "an item changed slot", and the one precise vanilla hook for
 * it ({@code LivingEntity.onEquipItem}) can't be shared through a mixin the way this codebase
 * relocates ordinary classes between mods — see
 * {@code rkc/docs/kalic0re/plan/player-effect-framework.md}'s findings. So activation and
 * deactivation are detected the same way
 * {@code common/label/HoverScanner} detects a changed hover target: a per-tick scan comparing each
 * player's currently active effects against what was active last tick.
 */
public final class PlayerEffectDispatcher {
    private static final List<PlayerEffect> EFFECTS = new ArrayList<>();
    private static final Map<UUID, Set<PlayerEffect>> ACTIVE = new HashMap<>();

    private PlayerEffectDispatcher() {
    }

    /** Wires the per-tick scan and the damage, death and disconnect hooks. */
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(PlayerEffectDispatcher::onEndTick);
        ServerPlayerEvents.LEAVE.register(PlayerEffectDispatcher::onLeave);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(PlayerEffectDispatcher::onAfterDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(PlayerEffectDispatcher::onAfterDeath);
    }

    /** Adds an effect. Call before the first tick, i.e. from the mod's initializer. */
    public static void register(PlayerEffect effect) {
        EFFECTS.add(effect);
    }

    private static void onEndTick(MinecraftServer server) {
        // A mod may depend on kalic0re for something else entirely and never register an effect;
        // the scan allocates per player per tick, so don't start it for an empty list.
        if (EFFECTS.isEmpty()) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            update(player);
        }
    }

    // Compares this tick's active effects against last tick's, firing onActivate/onTick/
    // onDeactivate for whatever changed or is still ongoing.
    private static void update(ServerPlayer player) {
        Set<PlayerEffect> previous = ACTIVE.getOrDefault(player.getUUID(), Set.of());
        Set<PlayerEffect> current = new HashSet<>();

        for (PlayerEffect effect : EFFECTS) {
            if (!effect.source().isActive(player)) {
                continue;
            }
            current.add(effect);
            if (!previous.contains(effect)) {
                effect.hooks().onActivate(player);
            }
            effect.hooks().onTick(player);
        }

        for (PlayerEffect effect : previous) {
            if (!current.contains(effect)) {
                effect.hooks().onDeactivate(player);
            }
        }

        if (current.isEmpty()) {
            ACTIVE.remove(player.getUUID());
        } else {
            ACTIVE.put(player.getUUID(), current);
        }
    }

    // Ends every effect still active on a disconnecting player, so a consumer's cleanup isn't
    // skipped by a disconnect mid-effect.
    private static void onLeave(ServerPlayer player) {
        Set<PlayerEffect> active = ACTIVE.remove(player.getUUID());
        if (active != null) {
            for (PlayerEffect effect : active) {
                effect.hooks().onDeactivate(player);
            }
        }
    }

    private static void onAfterDamage(LivingEntity entity, DamageSource source,
            float baseDamageTaken, float damageTaken, boolean blocked) {
        if (entity instanceof ServerPlayer victim) {
            for (PlayerEffect effect : activeOn(victim)) {
                effect.hooks().onDamageTaken(victim, source, damageTaken);
            }
        }

        // getEntity() is the credited attacker, resolving through a projectile to whoever fired
        // it; getDirectEntity() would be the projectile itself, which isn't what "dealt" means.
        Entity attacker = source.getEntity();
        if (attacker instanceof ServerPlayer attackingPlayer) {
            for (PlayerEffect effect : activeOn(attackingPlayer)) {
                effect.hooks().onDamageDealt(attackingPlayer, entity, source, damageTaken);
            }
        }
    }

    private static void onAfterDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof ServerPlayer player) {
            for (PlayerEffect effect : activeOn(player)) {
                effect.hooks().onDeath(player, source);
            }
        }
    }

    private static Set<PlayerEffect> activeOn(ServerPlayer player) {
        return ACTIVE.getOrDefault(player.getUUID(), Set.of());
    }
}
