package club.rainbowkitty.rkcore.common.label;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

/** Builds the label text for an item: a heading naming it, then any lore beneath it. */
public final class ItemText {
    private static final ChatFormatting UNNAMED = ChatFormatting.GRAY;
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

    /** The item's lore, styled the way a tooltip would style it. */
    public static List<Component> subheadings(ItemStack stack) {
        ItemLore lore = stack.get(DataComponents.LORE);
        return lore == null ? List.of() : lore.styledLines();
    }
}
