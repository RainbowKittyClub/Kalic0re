package club.rainbowkitty.kalic0re.common.enchant.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.core.HolderLookup;

import club.rainbowkitty.kalic0re.common.enchant.EnchantSpec;
import club.rainbowkitty.kalic0re.common.enchant.HiddenItemModelFallback;

/**
 * Adds every provider a declared enchantment needs to a datagen pack, so a mod's entrypoint names
 * its specs once rather than wiring the enchantment JSON and its enchanting-table tag itself.
 *
 * <p>Lang is not included: {@link net.minecraft.world.item.enchantment.Enchantment.Builder#build}
 * already points the description at {@code enchantment.<namespace>.<path>}, one key per spec — a
 * mod adds that wherever it already writes lang, the same as any other translatable.
 */
public final class EnchantDatagen {
    private EnchantDatagen() {
    }

    /** Adds the enchantment-json and enchanting-table-tag providers for the given specs. */
    public static void addProviders(FabricDataGenerator.Pack pack, List<EnchantSpec> specs) {
        List<EnchantSpec> declared = List.copyOf(specs);

        pack.addProvider((FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registries) ->
                new EnchantDatagenProvider(output, registries, declared));
        pack.addProvider((FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registries) ->
                new EnchantTagProvider(output, registries, declared));
    }

    /**
     * Adds the resource-pack-only held-item fallback provider for the given fallbacks — see
     * {@link HiddenItemModelFallback}. Separate from {@link #addProviders}, since this one needs
     * client-environment typed builders: only call it from a datagen source set built with Fabric
     * Loom's {@code client = true}, the same requirement {@code common.armor}'s
     * {@code ArmorModelProvider} already has.
     */
    public static void addHiddenItemModelProvider(FabricDataGenerator.Pack pack,
            List<HiddenItemModelFallback> fallbacks) {
        List<HiddenItemModelFallback> declared = List.copyOf(fallbacks);

        pack.addProvider((FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registries) ->
                new HiddenItemModelProvider(output, registries, declared));
    }
}
