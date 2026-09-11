package club.rainbowkitty.rkcore.common.armor.datagen;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import club.rainbowkitty.rkcore.common.armor.ArmorLayer;
import club.rainbowkitty.rkcore.common.armor.ArmorLayerTarget;
import club.rainbowkitty.rkcore.common.armor.ArmorSet;

/**
 * Writes one equipment asset per set that has a layer piece, at
 * {@code assets/<namespace>/equipment/<set>.json}.
 *
 * <p>Vanilla's own {@code EquipmentAssetProvider} generates from a hardcoded bootstrap list with no
 * hook to add to, so this writes the same shape through the public {@code EquipmentClientInfo}
 * builder instead. It is also the only place {@link ArmorLayerTarget} is turned into the
 * client-only vanilla enum, which is why it lives in a package a dedicated server never loads.
 */
public final class ArmorEquipmentProvider implements DataProvider {
    private static final Map<ArmorLayerTarget, EquipmentClientInfo.LayerType> LAYER_TYPES =
            resolveLayerTypes();

    private final PackOutput.PathProvider pathProvider;
    private final List<ArmorSet> sets;

    /**
     * @param output the pack output to write into
     * @param sets every set the mod declares; those without a layer piece are skipped
     */
    public ArmorEquipmentProvider(FabricPackOutput output, List<ArmorSet> sets) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
        this.sets = List.copyOf(sets);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> assets = new HashMap<>();
        for (ArmorSet set : sets) {
            if (!set.hasLayerPieces()) {
                continue;
            }
            EquipmentClientInfo.Builder asset = EquipmentClientInfo.builder();
            set.layers().forEach((target, layers) -> asset.addLayers(LAYER_TYPES.get(target),
                    layers.stream().map(ArmorEquipmentProvider::layer)
                            .toArray(EquipmentClientInfo.Layer[]::new)));
            assets.put(set.assetKey(), asset.build());
        }
        return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, this.pathProvider::json,
                assets);
    }

    @Override
    public String getName() {
        return "Armor Equipment Assets";
    }

    // Paired by serialized name rather than by ordinal or enum name, since that is the contract
    // ArmorLayerTarget mirrors; a vanilla rename fails datagen here rather than at render time.
    private static Map<ArmorLayerTarget, EquipmentClientInfo.LayerType> resolveLayerTypes() {
        Map<ArmorLayerTarget, EquipmentClientInfo.LayerType> types =
                new EnumMap<>(ArmorLayerTarget.class);
        for (ArmorLayerTarget target : ArmorLayerTarget.values()) {
            for (EquipmentClientInfo.LayerType type : EquipmentClientInfo.LayerType.values()) {
                if (type.getSerializedName().equals(target.serializedName())) {
                    types.put(target, type);
                }
            }
            if (!types.containsKey(target)) {
                throw new IllegalStateException("No EquipmentClientInfo.LayerType is serialized"
                        + " as '" + target.serializedName() + "', which " + target + " mirrors");
            }
        }
        return types;
    }

    // The dyeable flag is the layer's own: a layer that never tints passes false and so carries no
    // dyeable block at all, which is what keeps an undyed piece from rendering in leather brown.
    private static EquipmentClientInfo.Layer layer(ArmorLayer layer) {
        return switch (layer.dye()) {
            case NONE -> new EquipmentClientInfo.Layer(layer.texture());
            case TINTED -> EquipmentClientInfo.Layer.leatherDyeable(layer.texture(), true);
            case ONLY_IF_DYED -> EquipmentClientInfo.Layer.onlyIfDyed(layer.texture(), true);
        };
    }
}
