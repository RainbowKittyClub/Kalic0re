package club.rainbowkitty.kalic0re.common.armor.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

//? if >=26.3
/*import net.minecraft.advancements.Advancement;*/
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.CustomCraftingRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
//? if <26.3
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
//? if >=26.3
/*import net.minecraft.data.worldgen.BootstrapContext;*/
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.DyeRecipe;
import net.minecraft.world.item.crafting.Ingredient;
//? if >=26.3
/*import net.minecraft.world.item.crafting.Recipe;*/

import club.rainbowkitty.kalic0re.common.armor.ArmorLayer;
import club.rainbowkitty.kalic0re.common.armor.ArmorPiece;
import club.rainbowkitty.kalic0re.common.armor.ArmorRegistration;
import club.rainbowkitty.kalic0re.common.armor.ArmorSet;

/**
 * A {@code minecraft:dye} crafting recipe for every piece that declares a dyeable layer.
 *
 * <p>Dyeing in 26.2 is an ordinary datapack recipe naming its target item, not a tag-driven special
 * case, so a piece whose layers respond to {@code dyed_color} can never actually be dyed until
 * something writes one. That makes the recipe part of declaring a dyeable layer rather than part of
 * a mod's own recipe book, which is why it lives here.
 */
public final class ArmorDyeRecipeProvider extends FabricRecipeProvider {
    private final List<ArmorSet> sets;

    /**
     * @param output the pack output to write into
     * @param registries the registry future Fabric hands a recipe provider
     * @param sets every set the mod declares; pieces with no dyeable layer are skipped
     */
    public ArmorDyeRecipeProvider(FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registries, List<ArmorSet> sets) {
        super(output, registries);
        this.sets = List.copyOf(sets);
    }

    // 26.3 builds recipes into registry bootstrap contexts rather than a RecipeOutput.
    @Override
    //? if >=26.3 {
    /*protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries,
            BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
    *///?} else {
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries,
            RecipeOutput output) {
    //?}
        String modId = this.output.getModId();
        List<ArmorSet> declared = this.sets;
        //? if >=26.3 {
        /*return new RecipeProvider(recipes, advancements) {
        *///?} else {
        return new RecipeProvider(registries, output) {
        //?}
            @Override
            public void buildRecipes() {
                for (ArmorSet set : declared) {
                    for (ArmorPiece piece : set.pieces()) {
                        if (!isDyeable(piece)) {
                            continue;
                        }
                        Item item = ArmorRegistration.item(set, piece);
                        CustomCraftingRecipeBuilder.customCrafting(RecipeCategory.MISC,
                                (commonInfo, bookInfo) -> new DyeRecipe(commonInfo, bookInfo,
                                        Ingredient.of(item), this.tag(ItemTags.DYES),
                                        new ItemStackTemplate(item)))
                                .unlockedBy(getHasName(item), this.has(item))
                                .group("dyed_armor")
                                .save(this.output, modId + ":" + piece.path() + "_dyed");
                    }
                }
            }
        };
    }

    @Override
    public String getName() {
        return "Armor Dye Recipes";
    }

    // A piece is dyeable when any of its layers reads the dyed_color component at render time.
    private static boolean isDyeable(ArmorPiece piece) {
        return piece.layers().values().stream()
                .flatMap(List::stream)
                .anyMatch(layer -> layer.dye() != ArmorLayer.Dye.NONE);
    }
}
