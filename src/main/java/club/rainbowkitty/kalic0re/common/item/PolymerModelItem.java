package club.rainbowkitty.kalic0re.common.item;

import java.util.function.Predicate;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * An item that clients without this server's resource pack see as an ordinary vanilla item, and
 * clients with the pack see using a custom model.
 *
 * <p>Instantiate this directly when the fallback and model are fixed for every stack; subclass it
 * when either has to vary, or when the item needs behaviour of its own.
 */
public class PolymerModelItem extends Item implements PolymerItem {
    private final Item fallback;
    private final Identifier model;
    private final Predicate<PacketContext> useRealItem;

    /**
     * @param fallback the vanilla item shown to clients that do not have the pack; pick the closest
     *     match, since its shape and tooltip are what those clients get
     * @param model the item model identifier, which resolves to the item <em>definition</em> at
     *     {@code assets/<namespace>/items/<path>.json} rather than to {@code models/item/}
     * @param properties standard item properties, which must already carry the item's registry id
     */
    public PolymerModelItem(Item fallback, Identifier model, Properties properties) {
        this(fallback, model, context -> false, properties);
    }

    /**
     * @param useRealItem clients for which this returns true are sent the real item id (and so its
     *     real name and model) rather than {@code fallback}; e.g. a client also running this mod
     */
    public PolymerModelItem(Item fallback, Identifier model, Predicate<PacketContext> useRealItem,
            Properties properties) {
        super(properties);
        this.fallback = fallback;
        this.model = model;
        this.useRealItem = useRealItem;
    }

    /** The vanilla item this is presented as, or itself for a client that also runs this mod. */
    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return useRealItem.test(context) ? this : fallback;
    }

    /**
     * The custom model for a client that can resolve it - one with this server's resource pack, or
     * one running this mod and so carrying the assets itself - and none for any other, which leaves
     * Polymer drawing the fallback item's own rather than a missing model.
     */
    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context,
            HolderLookup.Provider lookup) {
        return useRealItem.test(context) || PolymerResourcePackUtils.hasMainPack(context)
                ? model : null;
    }
}
