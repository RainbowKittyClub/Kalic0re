package club.rainbowkitty.kalic0re.common.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * Derives a stable {@link net.minecraft.world.entity.ai.attributes.AttributeModifier} id for a
 * {@link PlayerEffect}, so a hook can add one on {@link PlayerEffectHooks#onActivate} and remove
 * the exact same id on {@link PlayerEffectHooks#onDeactivate} without hand-rolling one per effect.
 */
public final class PlayerEffectAttributes {
    private PlayerEffectAttributes() {
    }

    /**
     * The modifier id {@code effect} should use on {@code attribute}, unique per effect+attribute.
     */
    public static Identifier modifierId(PlayerEffect effect, Holder<Attribute> attribute) {
        Identifier attributeId = attribute.unwrapKey()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Attribute holder has no resource key: " + attribute))
                .identifier();
        String suffix = "/" + attributeId.getNamespace() + "/" + attributeId.getPath();
        return effect.id().withSuffix(suffix);
    }
}
