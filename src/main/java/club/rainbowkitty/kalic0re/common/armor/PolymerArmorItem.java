package club.rainbowkitty.kalic0re.common.armor;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import club.rainbowkitty.kalic0re.common.item.PolymerModelItem;

/**
 * An armour piece that degrades to its fallback item for a connection without this server's
 * resource pack, rather than to nothing.
 *
 * <p>Neither half of that happens on its own. A layer piece's custom asset is copied to the client
 * verbatim and resolves to an empty layer map on a client that cannot find it, which draws nothing
 * at all; and a custom item model is just as unresolvable. So both are withdrawn per connection:
 * the model by returning {@code null}, which leaves Polymer using the fallback item's own, and the
 * {@code equippable} component by replacing it with the fallback's after Polymer has copied ours
 * over.
 */
public class PolymerArmorItem extends PolymerModelItem {
    private final Item fallback;

    /**
     * @param fallback the vanilla item this is presented as, and whose armour a pack-less client
     *     ends up wearing; its own {@code equippable} component is what gets substituted
     * @param model the item model for clients that do have the pack
     * @param properties standard item properties, which must already carry the item's registry id
     */
    public PolymerArmorItem(Item fallback, Identifier model, Properties properties) {
        super(fallback, model, properties);
        this.fallback = fallback;
    }

    /** The custom model, or none for a connection that could not resolve it anyway. */
    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context,
            HolderLookup.Provider lookup) {
        return PolymerResourcePackUtils.hasMainPack(context)
                ? super.getPolymerItemModel(stack, context, lookup) : null;
    }

    /**
     * Substitutes the fallback item's own {@code equippable} for a pack-less connection, so it
     * wears vanilla armour instead of an asset it cannot resolve.
     */
    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context,
            HolderLookup.Provider lookup) {
        if (!PolymerResourcePackUtils.hasMainPack(context)) {
            out.set(DataComponents.EQUIPPABLE,
                    fallback.components().get(DataComponents.EQUIPPABLE));
        }
    }
}
