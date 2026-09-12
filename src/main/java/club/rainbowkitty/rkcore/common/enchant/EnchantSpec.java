package club.rainbowkitty.rkcore.common.enchant;

import java.util.function.Function;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * A declared enchantment: its registry key, whether it belongs at the enchanting table, and a
 * factory that builds the {@link Enchantment} once a {@link HolderLookup.Provider} exists.
 *
 * <p>Vanilla's own {@link Enchantment.Builder} is already a complete declare-a-thing API — see
 * {@code Enchantments.bootstrap} — so this does not wrap it. The one thing a plain builder call
 * cannot supply ahead of time is an {@code exclusive_set} against another enchantment, since
 * {@code Registries.ENCHANTMENT} is itself the datapack registry this framework is populating and
 * does not exist yet at mod init — only at generation time, through the
 * {@code HolderLookup.Provider} a datagen provider is handed. A spec that needs one resolves it
 * inside its own factory, the same way {@code Enchantments.bootstrap} does via
 * {@code context.lookup(Registries.ENCHANTMENT)}.
 *
 * @param key the enchantment's registry key
 * @param inEnchantingTable whether the enchantment belongs in
 *        {@code #minecraft:in_enchanting_table} — the tag that gates enchanting-table candidacy
 *        regardless of weight or supported items. A loot-only or villager-trade-only enchantment
 *        sets this {@code false}.
 * @param factory builds the enchantment; a declaration with no exclusivity can ignore the argument
 */
public record EnchantSpec(ResourceKey<Enchantment> key, boolean inEnchantingTable,
        Function<HolderLookup.Provider, Enchantment> factory) {

    /** Declares an enchantment needing no registry lookups, reachable at the enchanting table. */
    public static EnchantSpec of(ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        return of(key, true, registries -> builder);
    }

    /**
     * As {@link #of(ResourceKey, Enchantment.Builder)}, with explicit enchanting-table membership.
     */
    public static EnchantSpec of(ResourceKey<Enchantment> key, boolean inEnchantingTable,
            Enchantment.Builder builder) {
        return of(key, inEnchantingTable, registries -> builder);
    }

    /**
     * Declares an enchantment whose builder needs a {@link HolderLookup.Provider} — an
     * {@code exclusiveWith} call against another enchantment, most often — reachable at the
     * enchanting table.
     */
    public static EnchantSpec of(ResourceKey<Enchantment> key,
            Function<HolderLookup.Provider, Enchantment.Builder> builder) {
        return of(key, true, builder);
    }

    /**
     * As {@link #of(ResourceKey, Function)}, with explicit enchanting-table membership.
     */
    public static EnchantSpec of(ResourceKey<Enchantment> key, boolean inEnchantingTable,
            Function<HolderLookup.Provider, Enchantment.Builder> builder) {
        return new EnchantSpec(key, inEnchantingTable,
                registries -> builder.apply(registries).build(key.identifier()));
    }
}
