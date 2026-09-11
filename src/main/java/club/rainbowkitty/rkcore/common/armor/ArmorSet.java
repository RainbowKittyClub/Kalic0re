package club.rainbowkitty.rkcore.common.armor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

/**
 * A declared armour set: its items, its stats, and how each piece is drawn. Registration and
 * datagen both read one of these, so nothing here may touch a client-only type.
 *
 * <p>Every {@link ArmorBackend#LAYER} piece of a set shares one equipment asset, the way a vanilla
 * material does — a helmet, chestplate and boots all draw from the same
 * {@code textures/entity/equipment/humanoid/} image, and only the target's directory changes.
 *
 * @param namespace the mod id the items and assets are registered under
 * @param name the set's name, used for its asset, its textures and its pieces' registry paths
 * @param stats armour points and the rest of the material numbers
 * @param allowedWearers entity types allowed to wear the set; empty means every entity, as vanilla
 *        armour does
 * @param pieces the set's pieces, one per slot at most
 * @param equippableExtras applied to each piece's {@code Equippable.Builder} after the framework
 *        has set the slot, asset and allowed wearers — where a set adds shearing or
 *        equip-on-interact behaviour
 */
public record ArmorSet(String namespace, String name, ArmorStats stats,
        Optional<HolderSet<EntityType<?>>> allowedWearers, List<ArmorPiece> pieces,
        Consumer<Equippable.Builder> equippableExtras) {
    public ArmorSet {
        pieces = List.copyOf(pieces);
        if (namespace.isBlank() || name.isBlank()) {
            throw new IllegalArgumentException("ArmorSet.namespace and name must not be blank, got"
                    + " namespace='" + namespace + "' name='" + name + "'");
        }
        if (pieces.isEmpty()) {
            throw new IllegalArgumentException("ArmorSet '" + name + "' declares no pieces");
        }
        requireOnePiecePerSlot(name, pieces);
        mergeLayers(name, pieces);
    }

    /** Starts a set declaration; see {@link Builder} for what it fills in by default. */
    public static Builder builder(String namespace, String name) {
        return new Builder(namespace, name);
    }

    /** The set's own identifier — its equipment asset's path, and its layers' default texture. */
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(namespace, name);
    }

    /** The equipment asset every {@link ArmorBackend#LAYER} piece of this set is drawn from. */
    public ResourceKey<EquipmentAsset> assetKey() {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, id());
    }

    /** A piece's item identifier. */
    public Identifier itemId(ArmorPiece piece) {
        return Identifier.fromNamespaceAndPath(namespace, piece.path());
    }

    /** A piece's item registry key. */
    public ResourceKey<Item> itemKey(ArmorPiece piece) {
        return ResourceKey.create(Registries.ITEM, itemId(piece));
    }

    /** Whether any piece is drawn from the set's equipment asset. */
    public boolean hasLayerPieces() {
        return pieces.stream().anyMatch(piece -> piece.backend() == ArmorBackend.LAYER);
    }

    /**
     * Every layer this set draws, merged across its pieces — the shape its equipment asset is
     * written from. Pieces that share a target must declare it identically, which the constructor
     * has already checked.
     */
    public Map<ArmorLayerTarget, List<ArmorLayer>> layers() {
        return mergeLayers(name, pieces);
    }

    // Two pieces in one slot would register the same item twice and silently lose one.
    private static void requireOnePiecePerSlot(String name, List<ArmorPiece> pieces) {
        Map<ArmorType, ArmorPiece> bySlot = new EnumMap<>(ArmorType.class);
        for (ArmorPiece piece : pieces) {
            ArmorPiece existing = bySlot.put(piece.type(), piece);
            if (existing != null) {
                throw new IllegalArgumentException("ArmorSet '" + name + "' declares two "
                        + piece.type() + " pieces: '" + existing.path() + "' and '" + piece.path()
                        + "'");
            }
        }
    }

    // One asset per set means a target declared twice has to agree, since only one of the two
    // layer lists could ever be written. Merged into an EnumMap so the result iterates in a fixed
    // order whatever order the pieces declared their targets in — datagen picks a set's dyed
    // overlay off the first match, and an enum-keyed Map.copyOf iterates differently per JVM run.
    private static Map<ArmorLayerTarget, List<ArmorLayer>> mergeLayers(String name,
            List<ArmorPiece> pieces) {
        Map<ArmorLayerTarget, List<ArmorLayer>> merged = new EnumMap<>(ArmorLayerTarget.class);
        for (ArmorPiece piece : pieces) {
            piece.layers().forEach((target, layers) -> {
                List<ArmorLayer> existing = merged.putIfAbsent(target, layers);
                if (existing != null && !existing.equals(layers)) {
                    throw new IllegalArgumentException("ArmorSet '" + name + "' declares target "
                            + target + " twice with different layers; its pieces share one"
                            + " equipment asset, so they must agree");
                }
            });
        }
        return Collections.unmodifiableMap(merged);
    }

    /**
     * Fluent {@link ArmorSet} declaration. Unset stats are zero, the equip sound is the generic
     * one, and a piece with no explicit path, fallback item or layers gets the defaults described
     * on {@link #piece(ArmorType, Consumer)}.
     */
    public static final class Builder {
        private final String namespace;
        private final String name;
        private final Map<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
        private final List<ArmorPiece> pieces = new ArrayList<>();
        private int enchantmentValue;
        private Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_GENERIC;
        private float toughness;
        private float knockbackResistance;
        private TagKey<Item> repairIngredient = ArmorStats.UNUSED_REPAIR_TAG;
        private Optional<HolderSet<EntityType<?>>> allowedWearers = Optional.empty();
        private Consumer<Equippable.Builder> equippableExtras = builder -> { };

        // Built through ArmorSet.builder, which is where the namespace and name are documented.
        private Builder(String namespace, String name) {
            this.namespace = namespace;
            this.name = name;
        }

        /** Sets the armour points a slot grants. */
        public Builder defense(ArmorType type, int points) {
            defense.put(type, points);
            return this;
        }

        /** Sets enchantability. */
        public Builder enchantmentValue(int value) {
            enchantmentValue = value;
            return this;
        }

        /** Sets the sound played when a piece is put on. */
        public Builder equipSound(Holder<SoundEvent> sound) {
            equipSound = sound;
            return this;
        }

        /** Sets armour toughness, in the same units as diamond's 2.0. */
        public Builder toughness(float value) {
            toughness = value;
            return this;
        }

        /** Sets knockback resistance, which vanilla only applies above zero. */
        public Builder knockbackResistance(float value) {
            knockbackResistance = value;
            return this;
        }

        /** Sets the repair tag, which is unused while pieces are unbreakable. */
        public Builder repairIngredient(TagKey<Item> tag) {
            repairIngredient = tag;
            return this;
        }

        /** Restricts the set to the given entity types; unset, any entity may wear it. */
        @SafeVarargs
        public final Builder allowedWearers(EntityType<?>... types) {
            return allowedWearers(HolderSet.direct(
                    BuiltInRegistries.ENTITY_TYPE::wrapAsHolder, types));
        }

        /** Restricts the set to a set of entity types, e.g. a tag's contents. */
        public Builder allowedWearers(HolderSet<EntityType<?>> types) {
            allowedWearers = Optional.of(types);
            return this;
        }

        /** Adds behaviour to every piece's {@code equippable} component, e.g. shearing. */
        public Builder equippable(Consumer<Equippable.Builder> extras) {
            equippableExtras = extras;
            return this;
        }

        /** Adds a piece with every default in place; see {@link #piece(ArmorType, Consumer)}. */
        public Builder piece(ArmorType type) {
            return piece(type, piece -> { });
        }

        /**
         * Adds a piece, letting the customizer override any of its defaults: a registry path of
         * {@code <set>_<slot>}, the leather item for that slot as its fallback (wolf armour for a
         * body piece), and — for every slot but body — the humanoid layers vanilla draws that slot
         * with, carrying the set's own texture. A body piece has no default target, since which
         * animal wears it is the set's to say.
         */
        public Builder piece(ArmorType type, Consumer<ArmorPiece.Builder> customizer) {
            ArmorPiece.Builder piece = new ArmorPiece.Builder(type, defaultPath(type),
                    defaultFallback(type), Identifier.fromNamespaceAndPath(namespace, name));
            customizer.accept(piece);
            pieces.add(piece.build());
            return this;
        }

        /** Adds the four humanoid pieces with their defaults, the shape of a vanilla armour set. */
        public Builder humanoidPieces() {
            return piece(ArmorType.HELMET).piece(ArmorType.CHESTPLATE)
                    .piece(ArmorType.LEGGINGS).piece(ArmorType.BOOTS);
        }

        /** Validates the declaration and returns it. */
        public ArmorSet build() {
            ArmorStats stats = new ArmorStats(defense, enchantmentValue, equipSound, toughness,
                    knockbackResistance, repairIngredient);
            return new ArmorSet(namespace, name, stats, allowedWearers, pieces, equippableExtras);
        }

        // A body piece is usually a set on its own, so it takes the set's bare name.
        private String defaultPath(ArmorType type) {
            return type == ArmorType.BODY ? name : name + "_" + type.getName();
        }

        // The leather set is the closest vanilla shape for humanoid armour, wolf armour for a body
        // piece; both are what a client without the resource pack ends up wearing.
        private static Item defaultFallback(ArmorType type) {
            return switch (type) {
                case HELMET -> Items.LEATHER_HELMET;
                case CHESTPLATE -> Items.LEATHER_CHESTPLATE;
                case LEGGINGS -> Items.LEATHER_LEGGINGS;
                case BOOTS -> Items.LEATHER_BOOTS;
                case BODY -> Items.WOLF_ARMOR;
            };
        }
    }
}
