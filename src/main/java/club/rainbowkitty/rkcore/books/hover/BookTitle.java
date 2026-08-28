package club.rainbowkitty.rkcore.books.hover;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

// Builds the label text for a shelved book, styled so its type reads at a glance.
final class BookTitle {
    private static final ChatFormatting UNNAMED = ChatFormatting.GRAY;
    private static final ChatFormatting ENCHANTMENT = ChatFormatting.YELLOW;
    private static final ChatFormatting CURSE = ChatFormatting.RED;

    private BookTitle() {
    }

    static Component of(ItemStack book) {
        if (book.getItem() == Items.ENCHANTED_BOOK) {
            Component enchantments = enchantments(book);
            if (enchantments != null) {
                return enchantments;
            }
        }

        boolean named = book.getCustomName() != null;

        // A draft stays dim whether or not it has been titled; italics mark the title as the
        // author's working one rather than a published one.
        if (book.getItem() == Items.WRITABLE_BOOK) {
            MutableComponent draft = Component.empty().withStyle(UNNAMED);
            return (named ? draft.withStyle(ChatFormatting.ITALIC) : draft)
                    .append(book.getHoverName());
        }

        // Rarity is the item's own signal about itself — it is what makes a knowledge book
        // magenta — so it outranks the grey that only means "has no title of its own".
        ChatFormatting rarity = book.getRarity().color();
        if (rarity != ChatFormatting.WHITE) {
            return Component.empty().withStyle(rarity).append(book.getHoverName());
        }

        if (!named) {
            return Component.empty().withStyle(UNNAMED).append(book.getHoverName());
        }

        return book.getHoverName();
    }

    // Null rather than a name when there is nothing stored, so the caller falls back to the item.
    private static Component enchantments(ItemStack book) {
        ItemEnchantments stored = book.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.isEmpty()) {
            return null;
        }

        MutableComponent lines = Component.empty().withStyle(ChatFormatting.ITALIC);
        boolean first = true;
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            if (!first) {
                lines.append("\n");
            }
            first = false;
            lines.append(name(entry.getKey(), entry.getIntValue()));
        }

        return lines;
    }

    // Enchantment.getFullname cannot be reused: it merges its colour into the component itself,
    // which then beats anything the label sets. The parts it builds the name from are public.
    private static Component name(Holder<Enchantment> enchantment, int level) {
        MutableComponent name = enchantment.value().description().copy();
        if (level != 1 || enchantment.value().getMaxLevel() != 1) {
            name.append(" ").append(Component.translatable("enchantment.level." + level));
        }

        ChatFormatting colour = enchantment.is(EnchantmentTags.CURSE) ? CURSE : ENCHANTMENT;
        return Component.empty().withStyle(colour).append(name);
    }
}
