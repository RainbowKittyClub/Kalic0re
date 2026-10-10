package club.rainbowkitty.kalic0re.common.death;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Shared registry for themed death messages, the same shape as
 * {@code common.enchant.HideEquipmentModelEnchantments}: this holds what a consumer mod registers,
 * but the actual {@code @Mixin} routing a {@code CombatTracker.getDeathMessage()} call through
 * {@link #resolve} has to live in each consumer mod, since mixins are never shared code. Vanilla
 * builds a death message at two such calls, {@code ServerPlayer.die} for the broadcast and the kill
 * packet, and {@code TamableAnimal.die} for the note a pet's owner gets; a consumer hooks the ones
 * it cares about.
 *
 * <p>An override is a single function rather than a condition plus a message, so an override that
 * consumes state while deciding — the common case, since "did this death follow from our thing"
 * usually means clearing a flag — does it once, in one place, with its side effect in plain view.
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

    /** The first registered override to return a message for {@code victim}, or {@code vanilla}. */
    public static Component resolve(LivingEntity victim, DamageSource source, Component vanilla) {
        for (LivingDeathMessageOverride override : OVERRIDES) {
            Component message = override.messageFor(victim, source);
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
