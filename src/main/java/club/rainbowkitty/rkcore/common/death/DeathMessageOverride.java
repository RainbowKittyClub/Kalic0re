package club.rainbowkitty.rkcore.common.death;

import java.util.function.BiPredicate;
import java.util.function.Function;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

/**
 * One themed death message: {@code condition} decides whether it applies to a given death,
 * {@code message} builds the replacement text. See {@link DeathMessageOverrides}.
 */
public record DeathMessageOverride(BiPredicate<ServerPlayer, DamageSource> condition,
        Function<ServerPlayer, Component> message) {
}
