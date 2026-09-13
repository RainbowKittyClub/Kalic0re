package club.rainbowkitty.kalic0re.common.item;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * Registers items on behalf of a mod. The namespace is a parameter rather than this mod's own id,
 * since callers are other mods that shade this class in.
 */
public final class ItemRegistration {
    private ItemRegistration() {
    }

    /**
     * Registers an item at {@code <namespace>:<path>}. The factory receives the item's own registry
     * key, which {@link Item.Properties#setId} requires before the item can be constructed.
     */
    public static <T extends Item> T register(String namespace, String path,
            Function<ResourceKey<Item>, T> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(key));
    }
}
