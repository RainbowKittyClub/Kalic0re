package club.rainbowkitty.rkcore.common.enchant.datagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.EmptyModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ComponentMatches;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import club.rainbowkitty.rkcore.common.enchant.HiddenItemModelFallback;

/**
 * Writes one {@code assets/<namespace>/items/<item>.json} per declared
 * {@link HiddenItemModelFallback}, replacing the item's plain model with a
 * {@code minecraft:condition} model that swaps in an empty model whenever the stack carries the
 * declared enchantment, at any level.
 *
 * <p>{@code @Environment(CLIENT)} typed builders all the way down ({@link ItemModelUtils},
 * {@link ComponentMatches}, {@link ClientItem}) — same as {@code common.armor}'s
 * {@code ArmorModelProvider} — so a consumer can only add this provider from a datagen source set
 * built with Fabric Loom's {@code client = true}, per {@code common.datagen.PackJson}'s own
 * javadoc on that split.
 */
public final class HiddenItemModelProvider implements DataProvider {
    private final PackOutput.PathProvider definitionPathProvider;
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;
    private final List<HiddenItemModelFallback> fallbacks;

    /**
     * @param output the pack output to write into
     * @param registriesFuture resolves the fallback's enchantment key to a real holder, since
     *        {@code Registries.ENCHANTMENT} is a datapack registry not populated at mod init
     * @param fallbacks every held-item fallback the mod declares
     */
    public HiddenItemModelProvider(FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture,
            List<HiddenItemModelFallback> fallbacks) {
        this.definitionPathProvider =
                output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.registriesFuture = registriesFuture;
        this.fallbacks = List.copyOf(fallbacks);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registriesFuture.thenCompose(registries -> {
            Map<ResourceKey<Item>, ClientItem> definitions = new HashMap<>();
            for (HiddenItemModelFallback fallback : this.fallbacks) {
                definitions.put(fallback.item(), new ClientItem(model(fallback, registries),
                        ClientItem.Properties.DEFAULT));
            }
            return DataProvider.saveAll(cache, ClientItem.CODEC,
                    this.definitionPathProvider::json, definitions);
        });
    }

    @Override
    public String getName() {
        return "Hidden Item Models";
    }

    private static ItemModel.Unbaked model(HiddenItemModelFallback fallback,
            HolderLookup.Provider registries) {
        Holder<Enchantment> enchantment = registries.lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(fallback.enchantment());
        EnchantmentPredicate predicate =
                new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.ANY);
        DataComponentPredicate.Single<EnchantmentsPredicate.Enchantments> matches =
                new DataComponentPredicate.Single<>(DataComponentPredicates.ENCHANTMENTS,
                        EnchantmentsPredicate.enchantments(List.of(predicate)));
        return ItemModelUtils.conditional(new ComponentMatches(matches), new EmptyModel.Unbaked(),
                ItemModelUtils.plainModel(fallback.realModel()));
    }
}
