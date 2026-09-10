package club.rainbowkitty.rkcore.common.item;

import java.util.function.Predicate;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * A block item that clients without the server's resource pack see as an ordinary vanilla item and
 * clients with the pack see with a fixed custom model. The {@link BlockItem} twin of
 * {@link PolymerModelItem}; they cannot share a superclass ({@code Item} vs {@code BlockItem}), so
 * the small body (fields, constructors, {@code getPolymer*} overrides) is duplicated between them.
 */
public class PolymerModelBlockItem extends BlockItem implements PolymerItem {
    private final Item fallback;
    private final Identifier model;
    private final Predicate<PacketContext> useRealItem;

    /**
     * @param block the block placed
     * @param fallback the vanilla item shown to clients without the pack
     * @param model the item-model definition id shown to clients with the pack
     * @param properties standard item properties, already carrying this item's id
     */
    public PolymerModelBlockItem(Block block, Item fallback, Identifier model,
            Properties properties) {
        this(block, fallback, model, context -> false, properties);
    }

    /**
     * @param useRealItem clients for which this returns true are sent the real item id (and so its
     *     real name and model) rather than {@code fallback}; e.g. a client also running this mod
     */
    public PolymerModelBlockItem(Block block, Item fallback, Identifier model,
            Predicate<PacketContext> useRealItem, Properties properties) {
        super(block, properties);
        this.fallback = fallback;
        this.model = model;
        this.useRealItem = useRealItem;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return useRealItem.test(context) ? this : fallback;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context,
            HolderLookup.Provider lookup) {
        return model;
    }
}
