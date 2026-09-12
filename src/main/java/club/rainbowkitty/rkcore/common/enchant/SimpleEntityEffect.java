package club.rainbowkitty.rkcore.common.enchant;

import com.mojang.serialization.MapCodec;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

/**
 * A stateless {@link EnchantmentEntityEffect} — on-hit, on-tick, on-block-hit,
 * on-projectile-spawned — backed by a plain lambda instead of a hand-written record and
 * {@code RecordCodecBuilder}.
 *
 * <p>Only a genuinely parameterised effect (the shape {@code ApplyMobEffect} needs for its
 * configurable fields) needs that full machinery; an effect with nothing to serialize is
 * identified purely by the id it is registered under, so its codec can be a bare
 * {@link MapCodec#unit}. Built through {@link EnchantEffectRegistration#registerEntityEffect}.
 */
public final class SimpleEntityEffect implements EnchantmentEntityEffect {
    private final Action action;
    private final MapCodec<SimpleEntityEffect> codec;

    SimpleEntityEffect(Action action) {
        this.action = action;
        this.codec = MapCodec.unit(() -> this);
    }

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse item,
            Entity entity, Vec3 position) {
        action.apply(serverLevel, enchantmentLevel, item, entity, position);
    }

    @Override
    public MapCodec<SimpleEntityEffect> codec() {
        return codec;
    }

    /** What runs when the effect fires. */
    @FunctionalInterface
    public interface Action {
        /** What runs when the effect fires. */
        void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse item,
                Entity entity, Vec3 position);
    }
}
