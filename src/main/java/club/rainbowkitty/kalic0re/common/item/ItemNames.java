package club.rainbowkitty.kalic0re.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * Sets an item's {@code ITEM_NAME} component to a given component, so a client without the
 * server's language file reads that (a literal, or a translatable with fallback) instead of the
 * item's translation key.
 *
 * <p>Done by a component initializer registered after the item's own, not through
 * {@code Item.Properties.component}: {@code Item.Properties.finalizeInitializer} sets
 * {@code ITEM_NAME} to the translatable name after every component the properties hold, and an
 * item's initializers run in the order they were added, so only a later one sticks.
 */
public final class ItemNames {
    private ItemNames() {
    }

    /**
     * Names {@code item}, registered under {@code key}, and returns it; call with the item just
     * constructed, so this initializer runs after the one its constructor added.
     */
    public static <T extends Item> T named(ResourceKey<Item> key, Component name, T item) {
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add(key,
                (components, context, id) -> components.set(DataComponents.ITEM_NAME, name));
        return item;
    }
}
