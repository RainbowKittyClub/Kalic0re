package club.rainbowkitty.rkcore.common.death;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Shared yes/no registry for themed death messages, the same shape as
 * {@code common.enchant.HideEquipmentModelEnchantments}: this holds the predicate a consumer mod
 * registers, but the actual {@code @Mixin} redirecting {@code CombatTracker.getDeathMessage()} to
 * consult it has to live in each consumer mod, since mixins are never shared code.
 */
public final class DeathMessageOverrides {
    private static final List<DeathMessageOverride> OVERRIDES = new ArrayList<>();

    private DeathMessageOverrides() {
    }

    /** Adds an override, checked in registration order by {@link #resolve}. */
    public static void register(DeathMessageOverride override) {
        OVERRIDES.add(override);
    }

    /** Returns the first registered override whose condition matches, otherwise {@code vanilla}. */
    public static Component resolve(ServerPlayer player, DamageSource source, Component vanilla) {
        for (DeathMessageOverride override : OVERRIDES) {
            if (override.condition().test(player, source)) {
                return override.message().apply(player);
            }
        }
        return vanilla;
    }
}
