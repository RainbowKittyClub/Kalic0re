package club.rainbowkitty.kalic0re.common.armor.datagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import club.rainbowkitty.kalic0re.common.armor.ArmorBackend;
import club.rainbowkitty.kalic0re.common.armor.ArmorLayer;
import club.rainbowkitty.kalic0re.common.armor.ArmorPiece;
import club.rainbowkitty.kalic0re.common.armor.ArmorSet;

/**
 * Writes each piece's item definition and the flat models behind it.
 *
 * <p>A piece drawn from an equipment asset gets a generated inventory icon; one drawn as its own
 * model gets a definition pointing at the model its mod authored, and no geometry from here.
 *
 * <p>Vanilla's {@code ItemModelGenerators} keeps every per-item helper private, so this writes the
 * same shapes through the public {@code ModelTemplates}/{@code ItemModelUtils} building blocks.
 */
public final class ArmorModelProvider implements DataProvider {
    // What EquipmentClientInfo.Layer.leatherDyeable gives an undyed dyeable layer, so an item icon
    // tinted here matches the body it is worn on.
    private static final int UNDYED_LEATHER = -6265536;

    private final PackOutput.PathProvider definitionPathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    private final List<ArmorSet> sets;

    /**
     * @param output the pack output to write into
     * @param sets every set the mod declares
     */
    public ArmorModelProvider(FabricPackOutput output, List<ArmorSet> sets) {
        this.definitionPathProvider =
                output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK,
                "models");
        this.sets = List.copyOf(sets);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<Identifier, ModelInstance> models = new HashMap<>();
        Map<Identifier, ClientItem> definitions = new HashMap<>();

        for (ArmorSet set : sets) {
            Optional<Identifier> overlay = dyedOverlay(set);
            for (ArmorPiece piece : set.pieces()) {
                definitions.put(set.itemId(piece), new ClientItem(
                        model(set, piece, overlay, models), ClientItem.Properties.DEFAULT));
            }
        }

        return CompletableFuture.allOf(
                DataProvider.saveAll(cache, ModelInstance::get, this.modelPathProvider::json,
                        models),
                DataProvider.saveAll(cache, ClientItem.CODEC, this.definitionPathProvider::json,
                        definitions));
    }

    @Override
    public String getName() {
        return "Armor Item Models";
    }

    // A trim that appears with the dye has an item-icon counterpart in the mod's item texture
    // directory, under the same name the equipment layer uses.
    private static Optional<Identifier> dyedOverlay(ArmorSet set) {
        return set.layers().values().stream()
                .flatMap(List::stream)
                .filter(layer -> layer.dye() == ArmorLayer.Dye.ONLY_IF_DYED)
                .findFirst()
                .map(layer -> layer.texture().withPath(path -> "item/" + path));
    }

    // Registers whatever geometry the piece needs into models, and returns what its definition
    // should point at.
    private static ItemModel.Unbaked model(ArmorSet set, ArmorPiece piece,
            Optional<Identifier> overlay, Map<Identifier, ModelInstance> models) {
        if (piece.backend() == ArmorBackend.ITEM_MODEL) {
            return ItemModelUtils.plainModel(piece.itemModel().orElseThrow());
        }

        // The generated model and the texture it shows share an id, both being the piece's own.
        Identifier icon = set.itemId(piece).withPath(path -> "item/" + path);
        ModelTemplates.FLAT_ITEM.create(icon, TextureMapping.layer0(new Material(icon)),
                models::put);

        if (overlay.isPresent()) {
            Identifier dyedModel = icon.withSuffix("_dyed");
            ModelTemplates.TWO_LAYERED_ITEM.create(dyedModel,
                    TextureMapping.layered(new Material(icon), new Material(overlay.get())),
                    models::put);
            // Undyed shows the baked base alone; the overlay appears only once dyed, matching how
            // EquipmentLayerRenderer skips a dyeable layer whose colour is zero.
            return ItemModelUtils.conditional(
                    ItemModelUtils.hasComponent(DataComponents.DYED_COLOR),
                    ItemModelUtils.tintedModel(dyedModel, ItemModelUtils.constantTint(-1),
                            new Dye(0)),
                    ItemModelUtils.plainModel(icon));
        }
        if (tintsWholeLayer(set)) {
            return ItemModelUtils.tintedModel(icon, new Dye(UNDYED_LEATHER));
        }
        return ItemModelUtils.plainModel(icon);
    }

    // A set whose base layer tints wants its icon tinted the same way, as leather armour is.
    private static boolean tintsWholeLayer(ArmorSet set) {
        return set.layers().values().stream()
                .flatMap(List::stream)
                .anyMatch(layer -> layer.dye() == ArmorLayer.Dye.TINTED);
    }
}
