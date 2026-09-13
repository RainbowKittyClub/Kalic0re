package club.rainbowkitty.kalic0re.common.enchant.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import club.rainbowkitty.kalic0re.common.enchant.EnchantSpec;

/**
 * Adds every spec whose {@link EnchantSpec#inEnchantingTable()} is set to
 * {@code #minecraft:in_enchanting_table} — the tag that gates enchanting-table candidacy
 * regardless of weight or supported items ({@code EnchantmentMenu.getEnchantmentList} filters its
 * whole candidate pool through this tag before weight or item matching is ever consulted; a
 * declared enchantment absent from it never appears at the table).
 */
public final class EnchantTagProvider extends EnchantmentTagsProvider {
    private final List<EnchantSpec> specs;

    /**
     * @param output the pack output to write into
     * @param registriesFuture the registry future Fabric hands a tags provider
     * @param specs every enchantment the mod declares
     */
    public EnchantTagProvider(PackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture, List<EnchantSpec> specs) {
        super(output, registriesFuture);
        this.specs = List.copyOf(specs);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        TagAppender<Enchantment> builder = tag(EnchantmentTags.IN_ENCHANTING_TABLE);
        for (EnchantSpec spec : specs) {
            if (spec.inEnchantingTable()) {
                // addOptional, not add: the registries snapshot this same datagen run validates
                // tags against predates EnchantDatagenProvider's own output — a mod's own
                // just-declared enchantment never resolves there yet, only after this run's json
                // lands on disk and the game reloads. required=false skips that premature check;
                // it does not weaken the tag at real runtime, where the entry does exist by then.
                builder.addOptional(spec.key());
            }
        }
    }
}
