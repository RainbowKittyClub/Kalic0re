package club.rainbowkitty.kalic0re.common.display;

import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Item display elements that draw a model of the mod's own rather than an item's usual art.
 *
 * <p>Creating the element and giving it a model are separate here because they are separate in use:
 * an element whose model changes — a chest becoming half of a double chest, say — is built once and
 * re-sent a stack on each change, while one with a fixed model gets both at construction.
 *
 * <p>Shared by relocation, not a mod dependency (see each consuming mod's {@code build.gradle}).
 */
public final class ItemDisplays {
    /**
     * Interpolation a fresh element starts with, in ticks. A caller that wants a pose to snap
     * instead sets zero on that pose — see {@code CargoDisplayHolder.PART_INTERPOLATION}.
     */
    public static final int DEFAULT_INTERPOLATION = 2;

    private ItemDisplays() {
    }

    /**
     * An item display element ready for a model-carrying stack, with no item set yet.
     *
     * <p>Drawn in {@link ItemDisplayContext#HEAD}: a model authored for this has no
     * {@code display} block for any context to read, so HEAD renders identically to FIXED or NONE,
     * and the choice only matters for models that do carry one.
     */
    public static ItemDisplayElement element() {
        ItemDisplayElement element = new ItemDisplayElement();
        element.setItemDisplayContext(ItemDisplayContext.HEAD);
        element.setInterpolationDuration(DEFAULT_INTERPOLATION);
        return element;
    }

    /**
     * A stack that draws as the given model whatever item carries it.
     *
     * @param carrier the item to hang the model on, which a client lacking the model falls back to
     *     drawing — so prefer the vanilla item the content stands in for
     * @param model the item-model id to draw instead, as written by datagen
     */
    public static ItemStack modelStack(Item carrier, Identifier model) {
        ItemStack stack = new ItemStack(carrier);
        stack.set(DataComponents.ITEM_MODEL, model);
        return stack;
    }
}
