package club.rainbowkitty.kalic0re.common.enchant;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * A held item whose vanilla item model should render nothing when a specific enchantment is
 * present, at any level — the resource-pack-only, no-mixin fallback for
 * {@link HideEquipmentModelEnchantments} on a client that does not run the consuming mod's mixins
 * at all. Generated at datagen time (see {@code datagen.HiddenItemModelProvider}) as a
 * {@code minecraft:condition} item model replacing the item's own
 * {@code assets/<namespace>/items/*.json}.
 *
 * <p>Armor has no equivalent, so this only ever covers a held item's own model, never a worn
 * equipment layer: {@code EquipmentClientInfo}'s per-layer texture list is fixed at asset-build
 * time, with nothing in its codec that reads the worn stack's own components at render time.
 *
 * <p>Can only render the {@link HiddenEquipmentStyle#HIDDEN} half of what the mixin path offers —
 * an empty model submits no quads, so the enchantment glint's depth-equal test has nothing left to
 * match either. See {@code common/enchant/client/HiddenItemRendering}'s own javadoc for why the
 * mixin path needs real geometry to keep the glint at all; this fallback cannot.
 *
 * @param item the item this replaces the item model of
 * @param enchantment the enchantment whose presence, at any level, swaps in the empty model
 * @param realModel what the item shows when not enchanted with {@link #enchantment}
 */
public record HiddenItemModelFallback(ResourceKey<Item> item, ResourceKey<Enchantment> enchantment,
        Identifier realModel) {
}
