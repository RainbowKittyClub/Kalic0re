package club.rainbowkitty.kalic0re.common.effect;

import net.minecraft.resources.Identifier;

/**
 * A declared player effect: a condition under which it is active on a player, and the hooks it
 * fires while active. Registering one with {@link PlayerEffectDispatcher#register} is what makes
 * it run; the item or trinket that grants it is the declaring mod's own, built through
 * {@code common/item} (and {@code common/armor} where the grant is a wearable piece).
 *
 * @param namespace the mod id this effect belongs to
 * @param name the effect's own name, used for its identifier and for any attribute modifier ids
 *        derived from it through {@link PlayerEffectAttributes}
 * @param source when the effect is active on a player
 * @param hooks what the effect does while active
 */
public record PlayerEffect(String namespace, String name, PlayerEffectSource source,
        PlayerEffectHooks hooks) {
    public PlayerEffect {
        if (namespace.isBlank() || name.isBlank()) {
            throw new IllegalArgumentException("PlayerEffect.namespace and name must not be"
                    + " blank, got namespace='" + namespace + "' name='" + name + "'");
        }
    }

    /** The effect's own identifier, at {@code <namespace>:<name>}. */
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(namespace, name);
    }
}
