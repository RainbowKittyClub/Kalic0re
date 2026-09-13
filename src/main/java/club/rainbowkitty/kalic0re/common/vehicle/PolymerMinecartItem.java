package club.rainbowkitty.kalic0re.common.vehicle;

import java.util.function.Predicate;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;

/**
 * A minecart item that places a mod's own cart while presenting as an ordinary vanilla one, with a
 * custom model for clients that have this server's resource pack.
 *
 * <p>Subclassing {@code MinecartItem} rather than reimplementing it is the point: the entity type
 * it places is a constructor argument, so all of vanilla's placement behaviour — the rail check,
 * the slope offset, the sound, the stat, creative-mode consumption — is inherited exactly, and only
 * the type placed differs.
 *
 * <p>Instantiate this directly when the fallback and model are fixed for every stack; subclass it
 * when either has to vary per stack, which it does whenever the cart's cargo lives in a component
 * rather than in the item id.
 */
public class PolymerMinecartItem extends MinecartItem implements PolymerItem {
    private final Item fallback;
    private final @Nullable Identifier model;
    private final Predicate<PacketContext> useRealItem;

    /**
     * @param type the cart entity type this item places
     * @param fallback the vanilla item shown to clients that do not have the pack; pick the closest
     *     match, since its name and tooltip are what those clients get
     * @param model the item model identifier, which resolves to the item <em>definition</em> at
     *     {@code assets/<namespace>/items/<path>.json} rather than to {@code models/item/}; null
     *     keeps whatever model {@code fallback} already has
     * @param properties standard item properties, which must already carry the item's registry id
     */
    public PolymerMinecartItem(EntityType<? extends AbstractMinecart> type, Item fallback,
            @Nullable Identifier model, Properties properties) {
        this(type, fallback, model, context -> false, properties);
    }

    /**
     * @param useRealItem clients for which this returns true are sent the real item id (and so its
     *     real name and model) rather than {@code fallback}; e.g. a client also running this mod
     */
    public PolymerMinecartItem(EntityType<? extends AbstractMinecart> type, Item fallback,
            @Nullable Identifier model, Predicate<PacketContext> useRealItem,
            Properties properties) {
        super(type, properties);
        this.fallback = fallback;
        this.model = model;
        this.useRealItem = useRealItem;
    }

    /** The vanilla item this is presented as, or itself for a client that also runs this mod. */
    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return useRealItem.test(context) ? this : fallback;
    }

    /** Returns the custom model, or null to leave the fallback item's own model in place. */
    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context,
            HolderLookup.Provider lookup) {
        return model;
    }
}
