package club.rainbowkitty.rkcore.common.enchant;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Registers a mod's custom enchantment effects into the code-registered effect-type registries
 * vanilla's own data-driven enchantment system dispatches through —
 * {@code BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE} and its
 * {@code ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE} counterpart. This is the actual hook a consumer
 * uses to run its own Java when a declared {@link EnchantSpec}'s generated JSON references the
 * effect by id; vanilla's own call sites (tick, post-attack, location-changed, ...) do the rest.
 *
 * <p>Call from a mod's {@code onInitialize}, before any datapack reload — the same ordering every
 * other simple registry entry already needs, since an unrecognised effect id fails datapack load.
 */
public final class EnchantEffectRegistration {
    private EnchantEffectRegistration() {
    }

    /** Registers a stateless on-hit/on-tick/on-block-hit/on-projectile-spawned effect. */
    public static SimpleEntityEffect registerEntityEffect(Identifier id,
            SimpleEntityEffect.Action action) {
        SimpleEntityEffect effect = new SimpleEntityEffect(action);
        Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, id, effect.codec());
        return effect;
    }

    /** Registers a stateless equip/unequip effect — attribute-style, but running arbitrary code. */
    public static SimpleLocationBasedEffect registerLocationBasedEffect(Identifier id,
            SimpleLocationBasedEffect.OnChanged onChanged,
            SimpleLocationBasedEffect.OnDeactivated onDeactivated) {
        SimpleLocationBasedEffect effect = new SimpleLocationBasedEffect(onChanged, onDeactivated);
        Registry.register(BuiltInRegistries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, id,
                effect.codec());
        return effect;
    }
}
