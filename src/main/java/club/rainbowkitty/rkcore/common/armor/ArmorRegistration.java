package club.rainbowkitty.rkcore.common.armor;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

import club.rainbowkitty.rkcore.common.item.ItemRegistration;

/** Turns a declared {@link ArmorSet} into registered items. */
public final class ArmorRegistration {
    // Unbreakable pieces never consume durability, so the material's own value is never read.
    private static final int UNUSED_DURABILITY = 1;

    private ArmorRegistration() {
    }

    /**
     * Registers every piece of a set, returning the items by slot. Call before the item registry
     * freezes, i.e. from the mod's initializer.
     */
    public static Map<ArmorType, Item> register(ArmorSet set) {
        Map<ArmorType, Item> items = new EnumMap<>(ArmorType.class);
        for (ArmorPiece piece : set.pieces()) {
            Item item = ItemRegistration.register(set.namespace(), piece.path(),
                    key -> new PolymerArmorItem(piece.fallback(), set.itemId(piece),
                            properties(set, piece, key)));
            items.put(piece.type(), item);
        }
        return items;
    }

    /** A registered piece's item; only valid to call after {@link #register}. */
    public static Item item(ArmorSet set, ArmorPiece piece) {
        return BuiltInRegistries.ITEM.getValueOrThrow(set.itemKey(piece));
    }

    // Mirrors vanilla's armour properties minus durability: with no MAX_DAMAGE the piece is not
    // damageable, so hurtAndBreak no-ops and an anvil has no repair tag to look up.
    private static Item.Properties properties(ArmorSet set, ArmorPiece piece,
            ResourceKey<Item> key) {
        return new Item.Properties()
                .setId(key)
                .attributes(material(set).createAttributes(piece.type()))
                .component(DataComponents.EQUIPPABLE, equippable(set, piece))
                // Redundant server-side, but Polymer builds the client stack from the fallback,
                // whose own MAX_DAMAGE would otherwise draw a durability bar on a piece with none.
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .stacksTo(1);
    }

    // Only createAttributes is read off this, so the asset it names is irrelevant to an
    // ITEM_MODEL piece, which carries no asset of its own.
    private static ArmorMaterial material(ArmorSet set) {
        ArmorStats stats = set.stats();
        return new ArmorMaterial(UNUSED_DURABILITY, stats.defense(), stats.enchantmentValue(),
                stats.equipSound(), stats.toughness(), stats.knockbackResistance(),
                stats.repairIngredient(), set.assetKey());
    }

    // An ITEM_MODEL piece names no asset: that absence is exactly what makes a client draw the
    // item's own model on the head instead of an armour layer.
    private static Equippable equippable(ArmorSet set, ArmorPiece piece) {
        Equippable.Builder builder = Equippable.builder(piece.type().getSlot())
                .setEquipSound(set.stats().equipSound());
        if (piece.backend() == ArmorBackend.LAYER) {
            builder.setAsset(set.assetKey());
        }
        set.allowedWearers().ifPresent(builder::setAllowedEntities);
        set.equippableExtras().accept(builder);
        return builder.build();
    }
}
