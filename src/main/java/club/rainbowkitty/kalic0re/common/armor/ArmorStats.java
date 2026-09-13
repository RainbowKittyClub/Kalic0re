package club.rainbowkitty.kalic0re.common.armor;

import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * The numbers behind a set, everything {@code ArmorMaterial} needs bar the equipment asset, which
 * varies per set rather than per stat block.
 *
 * <p>Durability is deliberately absent: every piece this framework registers is unbreakable, so a
 * durability value would be dead weight and {@code repairIngredient} is only here because
 * {@code ArmorMaterial}'s constructor demands one.
 *
 * @param defense armour points per slot; a slot left out of the map gets none
 * @param enchantmentValue enchantability, as on a vanilla armour material
 * @param equipSound played when the piece is put on
 * @param toughness armour toughness, in the same units as diamond's 2.0
 * @param knockbackResistance knockback resistance, only applied when above zero
 * @param repairIngredient unused while pieces are unbreakable; kept for the material constructor
 */
public record ArmorStats(Map<ArmorType, Integer> defense, int enchantmentValue,
        Holder<SoundEvent> equipSound, float toughness, float knockbackResistance,
        TagKey<Item> repairIngredient) {
    /** The repair tag used when a set does not name one, since no piece is repairable anyway. */
    public static final TagKey<Item> UNUSED_REPAIR_TAG = ItemTags.REPAIRS_LEATHER_ARMOR;

    public ArmorStats {
        defense = Map.copyOf(defense);
        if (enchantmentValue < 0) {
            throw new IllegalArgumentException(
                    "ArmorStats.enchantmentValue must be >= 0, got " + enchantmentValue);
        }
        if (toughness < 0.0f || knockbackResistance < 0.0f) {
            throw new IllegalArgumentException("ArmorStats.toughness and knockbackResistance must"
                    + " be >= 0, got toughness=" + toughness
                    + " knockbackResistance=" + knockbackResistance);
        }
    }
}
