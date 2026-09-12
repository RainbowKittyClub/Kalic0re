package club.rainbowkitty.rkcore.common.enchant;

import com.mojang.serialization.MapCodec;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.Vec3;

/**
 * A stateless {@link EnchantmentLocationBasedEffect} — fired when the enchanted item starts or
 * stops occupying a slot the enchantment's {@code slots} matches, i.e. equip and unequip — backed
 * by a pair of plain lambdas instead of a hand-written record and codec.
 *
 * <p>This is the hook shape a "grant an ability while worn" enchantment needs: vanilla's own
 * {@code EnchantmentAttributeEffect} (attribute modifiers while equipped) is built on the same
 * interface. Built through {@link EnchantEffectRegistration#registerLocationBasedEffect}.
 */
public final class SimpleLocationBasedEffect implements EnchantmentLocationBasedEffect {
    private final OnChanged onChanged;
    private final OnDeactivated onDeactivated;
    private final MapCodec<SimpleLocationBasedEffect> codec;

    SimpleLocationBasedEffect(OnChanged onChanged, OnDeactivated onDeactivated) {
        this.onChanged = onChanged;
        this.onDeactivated = onDeactivated;
        this.codec = MapCodec.unit(() -> this);
    }

    @Override
    public void onChangedBlock(ServerLevel serverLevel, int enchantmentLevel,
            EnchantedItemInUse item, Entity entity, Vec3 position, boolean becameActive) {
        onChanged.accept(serverLevel, enchantmentLevel, item, entity, position, becameActive);
    }

    @Override
    public void onDeactivated(EnchantedItemInUse item, Entity entity, Vec3 position, int level) {
        onDeactivated.accept(item, entity, position, level);
    }

    @Override
    public MapCodec<SimpleLocationBasedEffect> codec() {
        return codec;
    }

    /**
     * Fired when the item starts matching a slot the enchantment applies to ({@code becameActive}
     * true) and, while it keeps matching, once per location-changed check thereafter.
     */
    @FunctionalInterface
    public interface OnChanged {
        /** @see OnChanged */
        void accept(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse item,
                Entity entity, Vec3 position, boolean becameActive);
    }

    /** Fired when the item stops matching, e.g. unequipped or enchantment removed. */
    @FunctionalInterface
    public interface OnDeactivated {
        /** Does nothing; the default for an effect with no unequip behaviour. */
        OnDeactivated NONE = (item, entity, position, level) -> { };

        /** @see OnDeactivated */
        void accept(EnchantedItemInUse item, Entity entity, Vec3 position, int level);
    }
}
