package club.rainbowkitty.kalic0re.common.label;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/** Builds the label text for an item: a heading naming it, then its enchantments and lore. */
public final class ItemText {
    private static final ChatFormatting UNNAMED = ChatFormatting.GRAY;
    private static final ChatFormatting ENCHANTMENT = ChatFormatting.YELLOW;
    private static final ChatFormatting CURSE = ChatFormatting.RED;
    private static final List<Function<ItemStack, LabelText>> BUILDERS = new ArrayList<>();

    private ItemText() {
    }

    /**
     * Adds a builder that may claim the stacks it recognises and return null for the rest.
     * Builders are tried in registration order, ahead of the generic form.
     */
    public static void register(Function<ItemStack, LabelText> builder) {
        BUILDERS.add(builder);
    }

    /** The whole label for a stack. */
    public static LabelText of(ItemStack stack) {
        for (Function<ItemStack, LabelText> builder : BUILDERS) {
            LabelText text = builder.apply(stack);
            if (text != null) {
                return text;
            }
        }
        return new LabelText(heading(stack), subheadings(stack));
    }

    /** The item's name, coloured by what the item says about itself. */
    public static Component heading(ItemStack stack) {
        // Rarity is the item's own signal about itself — it is what makes a knowledge book
        // magenta — so it outranks the grey that only means "has no name of its own".
        ChatFormatting rarity = stack.getRarity().color();
        if (rarity != ChatFormatting.WHITE) {
            return Component.empty().withStyle(rarity).append(stack.getHoverName());
        }

        if (stack.getCustomName() == null) {
            return Component.empty().withStyle(UNNAMED).append(stack.getHoverName());
        }

        return stack.getHoverName();
    }

    /** The item's enchantments, then its lore styled the way a tooltip would style it. */
    public static List<Component> subheadings(ItemStack stack) {
        return Stream.concat(enchantmentLines(stack.get(DataComponents.ENCHANTMENTS)).stream(),
                lore(stack).stream()).toList();
    }

    /** One line per enchantment, for whichever component holds them, or none for null or empty. */
    public static List<Component> enchantmentLines(ItemEnchantments enchantments) {
        if (enchantments == null || enchantments.isEmpty()) {
            return List.of();
        }

        List<Component> lines = new ArrayList<>();
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            lines.add(enchantmentName(entry.getKey(), entry.getIntValue()));
        }
        return lines;
    }

    private static List<Component> lore(ItemStack stack) {
        ItemLore lore = stack.get(DataComponents.LORE);
        return lore == null ? List.of() : lore.styledLines();
    }

    // Enchantment.getFullname cannot be reused: it merges its colour into the component itself,
    // which then beats anything the label sets. The parts it builds the name from are public.
    private static Component enchantmentName(Holder<Enchantment> enchantment, int level) {
        MutableComponent name = enchantment.value().description().copy();
        if (level != 1 || enchantment.value().getMaxLevel() != 1) {
            name.append(" ").append(Component.translatable("enchantment.level." + level));
        }

        ChatFormatting colour = enchantment.is(EnchantmentTags.CURSE) ? CURSE : ENCHANTMENT;
        return Component.empty().withStyle(colour, ChatFormatting.ITALIC).append(name);
    }
}
