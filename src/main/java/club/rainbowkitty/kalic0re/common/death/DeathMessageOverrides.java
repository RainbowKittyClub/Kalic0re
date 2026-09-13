package club.rainbowkitty.kalic0re.common.death;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Shared registry for themed death messages, the same shape as
 * {@code common.enchant.HideEquipmentModelEnchantments}: this holds what a consumer mod registers,
 * but the actual {@code @Mixin} redirecting {@code CombatTracker.getDeathMessage()} to consult it
 * has to live in each consumer mod, since mixins are never shared code.
 *
 * <p>An override is a single function rather than a condition plus a message, so an override that
 * consumes state while deciding — the common case, since "did this death follow from our thing"
 * usually means clearing a flag — does it once, in one place, with its side effect in plain view.
 */
public final class DeathMessageOverrides {
    private static final List<DeathMessageOverride> OVERRIDES = new ArrayList<>();

    private DeathMessageOverrides() {
    }

    /** Adds an override, consulted in registration order by {@link #resolve}. */
    public static void register(DeathMessageOverride override) {
        OVERRIDES.add(override);
    }

    /** The first registered override to return a message, or {@code vanilla} if none does. */
    public static Component resolve(ServerPlayer player, DamageSource source, Component vanilla) {
        for (DeathMessageOverride override : OVERRIDES) {
            Component message = override.messageFor(player, source);
            if (message != null) {
                return message;
            }
        }
        return vanilla;
    }

    /** One themed death message: the replacement text, or null to leave this death alone. */
    @FunctionalInterface
    public interface DeathMessageOverride {
        /** The message to use for this death, or null if this override does not apply. */
        @Nullable Component messageFor(ServerPlayer player, DamageSource source);
    }
}
