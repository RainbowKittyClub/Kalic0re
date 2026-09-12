package club.rainbowkitty.rkcore.common.enchant.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.core.HolderLookup;

import club.rainbowkitty.rkcore.common.enchant.EnchantSpec;

/**
 * Writes one {@code data/<namespace>/enchantment/<path>.json} per declared {@link EnchantSpec},
 * through Fabric's own dynamic-registry datagen base class.
 *
 * <p>{@code Registries.ENCHANTMENT} is a datapack registry — the same shape as loot tables or
 * recipes, not the same shape as an {@code Item} — so there is no vanilla bootstrap to work around
 * the way {@code common/armor}'s providers had to for {@code EquipmentAssetProvider}. Fabric API
 * ships the exact base class this needs.
 */
public final class EnchantDatagenProvider extends FabricDynamicRegistryProvider {
    private final List<EnchantSpec> specs;

    /**
     * @param output the pack output to write into
     * @param registriesFuture the registry future Fabric hands a dynamic-registry provider
     * @param specs every enchantment the mod declares
     */
    public EnchantDatagenProvider(FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture, List<EnchantSpec> specs) {
        super(output, registriesFuture);
        this.specs = List.copyOf(specs);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        for (EnchantSpec spec : specs) {
            entries.add(spec.key(), spec.factory().apply(registries));
        }
    }

    @Override
    public String getName() {
        return "Enchantments";
    }
}
