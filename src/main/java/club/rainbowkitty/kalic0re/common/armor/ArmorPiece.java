package club.rainbowkitty.kalic0re.common.armor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * One wearable piece of a set: the item that gets registered, and how it is drawn.
 *
 * <p>Built through {@link ArmorSet.Builder#piece}, which fills in the defaults every field here
 * would otherwise have to be spelled out for.
 *
 * @param type the armour slot this occupies
 * @param backend how it is drawn; see {@link ArmorBackend}
 * @param path the registry path, without namespace
 * @param fallback the vanilla item a client without this server's resource pack sees instead
 * @param itemModel the model a {@link ArmorBackend#ITEM_MODEL} piece is drawn with, as one model
 *        references another — {@code <namespace>:item/<name>} — absent for a
 *        {@link ArmorBackend#LAYER} piece. Every piece's item *definition* is generated at its own
 *        id either way; this is the geometry that definition points at.
 * @param layers the layers this piece draws, per target; empty for an {@code ITEM_MODEL} piece
 */
public record ArmorPiece(ArmorType type, ArmorBackend backend, String path, Item fallback,
        Optional<Identifier> itemModel, Map<ArmorLayerTarget, List<ArmorLayer>> layers) {
    public ArmorPiece {
        layers = Map.copyOf(layers);
        if (path.isBlank()) {
            throw new IllegalArgumentException("ArmorPiece.path must not be blank");
        }
        if (backend == ArmorBackend.ITEM_MODEL) {
            requireItemModelPiece(type, itemModel, layers);
        } else {
            requireLayerPiece(path, itemModel, layers);
        }
        for (Map.Entry<ArmorLayerTarget, List<ArmorLayer>> entry : layers.entrySet()) {
            requireTargetFitsSlot(path, type, entry.getKey());
            if (entry.getValue().isEmpty()) {
                throw new IllegalArgumentException("ArmorPiece '" + path + "' declares target "
                        + entry.getKey() + " with no layers");
            }
        }
    }

    // An ITEM_MODEL piece is a head item with no equipment asset, which is the whole mechanism:
    // vanilla draws the item's own model exactly when the stack's equippable names no asset.
    private static void requireItemModelPiece(ArmorType type, Optional<Identifier> itemModel,
            Map<ArmorLayerTarget, List<ArmorLayer>> layers) {
        if (type != ArmorType.HELMET) {
            throw new IllegalArgumentException("ArmorBackend.ITEM_MODEL is only drawn in the head"
                    + " slot, got " + type);
        }
        if (itemModel.isEmpty()) {
            throw new IllegalArgumentException("ArmorBackend.ITEM_MODEL needs a model identifier");
        }
        if (!layers.isEmpty()) {
            throw new IllegalArgumentException("ArmorBackend.ITEM_MODEL draws no layers, got "
                    + layers.keySet());
        }
    }

    // A LAYER piece is the converse: an equipment asset and no item model of its own.
    private static void requireLayerPiece(String path, Optional<Identifier> itemModel,
            Map<ArmorLayerTarget, List<ArmorLayer>> layers) {
        if (itemModel.isPresent()) {
            throw new IllegalArgumentException("ArmorPiece '" + path + "' is a LAYER piece, which"
                    + " is drawn from its equipment asset rather than a model");
        }
        if (layers.isEmpty()) {
            throw new IllegalArgumentException("ArmorPiece '" + path + "' is a LAYER piece with no"
                    + " layers, so nothing would be drawn");
        }
    }

    // Vanilla picks the humanoid layer types by armour slot and the rest by wearer, so only a BODY
    // piece can sit on a mount or pet target and only the other slots on a humanoid one.
    private static void requireTargetFitsSlot(String path, ArmorType type,
            ArmorLayerTarget target) {
        if (type == ArmorType.BODY && target.humanoid()) {
            throw new IllegalArgumentException("ArmorPiece '" + path + "' is a BODY piece, which is"
                    + " never drawn on the humanoid target " + target);
        }
        if (type != ArmorType.BODY && !target.humanoid()) {
            throw new IllegalArgumentException("ArmorPiece '" + path + "' is a " + type
                    + " piece, which is only drawn on humanoid targets, got " + target);
        }
    }

    /**
     * Fluent {@link ArmorPiece} declaration, handed to {@link ArmorSet.Builder#piece} with the
     * set's defaults already applied.
     */
    public static final class Builder {
        private final ArmorType type;
        private final Identifier defaultTexture;
        private final Map<ArmorLayerTarget, List<ArmorLayer>> layers =
                new EnumMap<>(ArmorLayerTarget.class);
        private String path;
        private Item fallback;
        private Optional<Identifier> itemModel = Optional.empty();

        // Created by ArmorSet.Builder, which is what knows the set's naming and texture defaults.
        Builder(ArmorType type, String path, Item fallback, Identifier defaultTexture) {
            this.type = type;
            this.path = path;
            this.fallback = fallback;
            this.defaultTexture = defaultTexture;
        }

        /** Overrides the registry path, which defaults to {@code <set>_<slot>}. */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /** Overrides the vanilla item a client without the resource pack sees and wears. */
        public Builder fallback(Item fallback) {
            this.fallback = fallback;
            return this;
        }

        /**
         * Draws this piece as its own model rather than as armour layers, which only the head slot
         * supports; see {@link ArmorBackend#ITEM_MODEL}.
         */
        public Builder itemModel(Identifier model) {
            itemModel = Optional.of(model);
            return this;
        }

        /** Draws the given layers, in order, on one target. */
        public Builder drawsOn(ArmorLayerTarget target, ArmorLayer... layers) {
            this.layers.put(target, List.of(layers));
            return this;
        }

        /** Draws the set's own texture, untinted, on one target. */
        public Builder drawsOn(ArmorLayerTarget target) {
            return drawsOn(target, ArmorLayer.plain(defaultTexture));
        }

        // Called by ArmorSet.Builder once the caller has had its say; ArmorPiece validates.
        ArmorPiece build() {
            ArmorBackend backend = itemModel.isPresent()
                    ? ArmorBackend.ITEM_MODEL : ArmorBackend.LAYER;
            Map<ArmorLayerTarget, List<ArmorLayer>> declared =
                    backend == ArmorBackend.LAYER && layers.isEmpty() ? defaultLayers() : layers;
            return new ArmorPiece(type, backend, path, fallback, itemModel, declared);
        }

        // Vanilla draws leggings from the inner model and everything else from the outer one, and
        // a humanoid baby from its own target whatever the slot. A body piece gets no default:
        // which animal wears it is the set's to say.
        private Map<ArmorLayerTarget, List<ArmorLayer>> defaultLayers() {
            if (type == ArmorType.BODY) {
                return Map.of();
            }
            ArmorLayerTarget outer = type == ArmorType.LEGGINGS
                    ? ArmorLayerTarget.HUMANOID_LEGGINGS : ArmorLayerTarget.HUMANOID;
            List<ArmorLayer> base = List.of(ArmorLayer.plain(defaultTexture));
            Map<ArmorLayerTarget, List<ArmorLayer>> defaults =
                    new EnumMap<>(ArmorLayerTarget.class);
            defaults.put(outer, base);
            defaults.put(ArmorLayerTarget.HUMANOID_BABY, base);
            return defaults;
        }
    }
}
