package club.rainbowkitty.kalic0re.common.death;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Shared registry for themed death messages. Mixins are never shared code, so each consumer mod
 * owns the one mixin that feeds vanilla's message through {@link #resolve}: on
 * {@code CombatTracker}, a MixinExtras {@code @ModifyReturnValue} on {@code getDeathMessage()} at
 * {@code RETURN}, shadowing the tracker's {@code mob} and {@code entries} and returning
 * {@code resolve(mob, entries, vanilla)}.
 *
 * <p>Inside {@code getDeathMessage()}, never at its callers in {@code ServerPlayer.die} or
 * {@code TamableAnimal.die}: chat mods restyle or hide the message at those calls (StyledChat
 * redirects the one in {@code ServerPlayer.die} and returns a marker for a hidden death), and a
 * wrapper there sees their result and overwrites it. From inside, the override stands in for
 * vanilla's text and they style or hide it as usual.
 *
 * <p>An override is a single function rather than a condition plus a message, so one that
 * consumes state while deciding does it once, in plain view. Every reader of the message asks: a
 * player's death once, a named pet's twice (its owner's note, then the server log), so an override
 * that consumes state answers only the first.
 */
public final class DeathMessageOverrides {
    private static final List<LivingDeathMessageOverride> OVERRIDES = new ArrayList<>();

    private DeathMessageOverrides() {
    }

    /** Adds an override for player deaths, consulted in registration order by {@link #resolve}. */
    public static void register(DeathMessageOverride override) {
        OVERRIDES.add((victim, source) -> victim instanceof ServerPlayer player
                ? override.messageFor(player, source) : null);
    }

    /** Adds an override for any living entity's death, pets included, in the same order. */
    public static void registerLiving(LivingDeathMessageOverride override) {
        OVERRIDES.add(override);
    }

    /**
     * The death message for {@code mob}: the first registered override's, else {@code vanilla}.
     * The killing blow is the last combat entry, as {@code getDeathMessage()} takes it; with none
     * recorded, vanilla's generic message stands.
     */
    public static Component resolve(LivingEntity mob, List<CombatEntry> entries,
            Component vanilla) {
        if (entries.isEmpty()) {
            return vanilla;
        }
        DamageSource source = entries.getLast().source();
        for (LivingDeathMessageOverride override : OVERRIDES) {
            Component message = override.messageFor(mob, source);
            if (message != null) {
                return message;
            }
        }
        return vanilla;
    }

    /** One themed death message for a player: the replacement text, or null to leave it alone. */
    @FunctionalInterface
    public interface DeathMessageOverride {
        /** The message to use for this death, or null if this override does not apply. */
        @Nullable Component messageFor(ServerPlayer player, DamageSource source);
    }

    /** One themed death message for any living entity: the text, or null to leave it alone. */
    @FunctionalInterface
    public interface LivingDeathMessageOverride {
        /** The message to use for this death, or null if this override does not apply. */
        @Nullable Component messageFor(LivingEntity victim, DamageSource source);
    }
}
