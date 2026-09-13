package club.rainbowkitty.kalic0re.common.armor.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.core.HolderLookup;

import club.rainbowkitty.kalic0re.common.armor.ArmorSet;

/**
 * Adds every provider a declared set needs to a datagen pack, so a mod's entrypoint names its sets
 * once rather than wiring five providers itself.
 *
 * <p>Each lambda below types its parameter {@code FabricPackOutput} explicitly. Without that,
 * {@code DataGenerator.PackGenerator}'s own single-argument overload captures it and hands over a
 * plain {@code PackOutput}, which fails to compile against providers that need the Fabric one.
 * Keeping that here is most of the point: a consuming mod never meets it.
 */
public final class ArmorDatagen {
    private ArmorDatagen() {
    }

    /** Adds the providers for sets that ship their own textures rather than deriving them. */
    public static void addProviders(FabricDataGenerator.Pack pack, List<ArmorSet> sets) {
        addProviders(pack, sets, List.of());
    }

    /**
     * Adds every provider the given sets need: equipment assets, item definitions and models,
     * derived textures, English names, and a dye recipe per dyeable piece.
     *
     * @param pack the pack to add to
     * @param sets every set the mod declares
     * @param art base art for the sets whose textures are derived; a set absent from this list
     *     keeps whatever textures its mod ships, so an empty list means none are derived
     */
    public static void addProviders(FabricDataGenerator.Pack pack, List<ArmorSet> sets,
            List<ArmorArt> art) {
        List<ArmorSet> declared = List.copyOf(sets);
        List<ArmorArt> derived = List.copyOf(art);

        pack.addProvider((FabricPackOutput output) -> new ArmorEquipmentProvider(output, declared));
        pack.addProvider((FabricPackOutput output) -> new ArmorModelProvider(output, declared));
        pack.addProvider((FabricPackOutput output) -> new ArmorTextureProvider(output, derived));
        pack.addProvider((FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registries) ->
                new ArmorLanguageProvider(output, registries, declared));
        pack.addProvider((FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registries) ->
                new ArmorDyeRecipeProvider(output, registries, declared));
    }
}
