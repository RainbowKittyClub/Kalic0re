package club.rainbowkitty.rkcore.books;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import club.rainbowkitty.rkcore.common.label.ItemText;
import club.rainbowkitty.rkcore.common.label.LabelText;

/** Labels a book with its title, styled so its type reads at a glance, over what it holds. */
public final class BookText {
    private static final ChatFormatting UNNAMED = ChatFormatting.GRAY;
    private static final ChatFormatting ENCHANTMENT = ChatFormatting.YELLOW;
    private static final ChatFormatting CURSE = ChatFormatting.RED;

    private BookText() {
    }

    /** Claims books away from the generic item text. */
    public static void register() {
        ItemText.register(BookText::of);
    }

    /** The label for a book, or null for anything else so the generic form takes over. */
    public static LabelText of(ItemStack stack) {
        if (!stack.typeHolder().is(ItemTags.BOOKSHELF_BOOKS)) {
            return null;
        }
        return new LabelText(heading(stack), subheadings(stack));
    }

    private static Component heading(ItemStack book) {
        // A draft stays dim whether or not it has been titled; italics mark the title as the
        // author's working one rather than a published one.
        if (book.getItem() == Items.WRITABLE_BOOK) {
            MutableComponent draft = Component.empty().withStyle(UNNAMED);
            return (book.getCustomName() != null ? draft.withStyle(ChatFormatting.ITALIC) : draft)
                    .append(book.getHoverName());
        }

        return ItemText.heading(book);
    }

    private static List<Component> subheadings(ItemStack book) {
        List<Component> enchantments = enchantments(book);
        List<Component> lore = ItemText.subheadings(book);
        if (enchantments.isEmpty()) {
            return lore;
        }

        List<Component> lines = new ArrayList<>(enchantments);
        lines.addAll(lore);
        return lines;
    }

    private static List<Component> enchantments(ItemStack book) {
        ItemEnchantments stored = book.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.isEmpty()) {
            return List.of();
        }

        List<Component> lines = new ArrayList<>();
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            lines.add(name(entry.getKey(), entry.getIntValue()));
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
        return Component.empty().withStyle(colour, ChatFormatting.ITALIC).append(name);
    }
}
