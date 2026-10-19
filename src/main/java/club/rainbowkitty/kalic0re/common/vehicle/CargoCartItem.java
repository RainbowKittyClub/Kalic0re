package club.rainbowkitty.kalic0re.common.vehicle;

import java.util.function.Predicate;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * One cart item for every cargo, carrying the cargo block in a component rather than in the item
 * id.
 *
 * <p>One item beats one per cargo: a mod with 27 cargoes registers one item and writes one recipe
 * result per cargo instead of 27 of each, and the name and model are derived from the component so
 * that changing either never means regenerating anything.
 *
 * <p>Also the single place the cart entity and its item agree on the three things they must agree
 * on — the component, the default cargo and the name pattern. {@link CargoMinecartChest} reads all
 * three off the item rather than being handed them again, so there is no way for them to drift.
 */
public class CargoCartItem extends PolymerMinecartItem {
    private final DataComponentType<Holder<Block>> cargoComponent;
    private final Holder<Block> defaultCargo;
    private final String namePatternKey;

    /**
     * @param type the cart entity type this item places
     * @param cargoComponent from {@link CargoMinecarts#registerCargoComponent}
     * @param defaultCargo the cargo a stack with no component carries; for a container cart this is
     *     the vanilla block that cart already draws, so an uncomponented stack is the vanilla cart
     * @param namePatternKey lang key for {@link CargoMinecarts#cartName}
     * @param fallback the vanilla item shown to clients without this server's resource pack
     * @param model item model identifier, or null to keep the fallback's; per-cargo models come
     *     from {@link #cargoModel} instead
     * @param useRealItem clients for which this returns true are sent the real item id
     * @param properties standard item properties, which must already carry the item's registry id
     */
    public CargoCartItem(EntityType<? extends AbstractMinecart> type,
            DataComponentType<Holder<Block>> cargoComponent, Holder<Block> defaultCargo,
            String namePatternKey, Item fallback, @Nullable Identifier model,
            Predicate<PacketContext> useRealItem, Item.Properties properties) {
        super(type, fallback, model, useRealItem, properties);
        this.cargoComponent = cargoComponent;
        this.defaultCargo = defaultCargo;
        this.namePatternKey = namePatternKey;
    }

    /** The component this item's cargo rides in. */
    public DataComponentType<Holder<Block>> cargoComponent() {
        return cargoComponent;
    }

    /** The cargo a stack carries when it has no component of its own. */
    public Holder<Block> defaultCargo() {
        return defaultCargo;
    }

    /**
     * The cargo on a stack, or null if it carries none — or one {@link #acceptsCargo} refuses.
     *
     * <p>Filtering at the read rather than at the write is what keeps a refused cargo from being
     * half-honoured. Every question a stack answers about its cargo — its name, its model, the cart
     * it places — comes through here, so a component naming a block this cart will not carry is
     * simply not there as far as any of them can tell.
     */
    public @Nullable Holder<Block> cargoOf(ItemStack stack) {
        Holder<Block> cargo = stack.get(cargoComponent);
        return cargo != null && acceptsCargo(cargo) ? cargo : null;
    }

    /**
     * Whether this cart will carry a cargo, which decides both what a stack reads as and what a
     * placed cart honours.
     *
     * <p>Everything, unless a consumer says otherwise. {@code kalic0re} has no way to know which
     * blocks belong in a given cart any more than it knows what a wood is — a chest cart wants
     * chest-like blocks, and a furnace cart's answer would be a different list entirely — so the
     * judgement is the consumer's to make and a block tag is the usual shape for it.
     *
     * <p>Never asked about {@link #defaultCargo()}: a refused cargo falls back to it rather than
     * being tested itself, so a consumer cannot lock its own cart out by answering carelessly.
     */
    public boolean acceptsCargo(Holder<Block> cargo) {
        return true;
    }

    /**
     * Whether a cart carrying this cargo drops its contents <em>on the item</em> rather than
     * spilling them, so that breaking and replacing it keeps what was inside.
     *
     * <p>No, unless a consumer says otherwise — a container cart spills, as every vanilla one does,
     * and that is the behaviour a cart presented as a vanilla cart should have.
     *
     * <p>The consumer's answer is worth making per cargo rather than per cart. A cart is drawn as
     * its cargo and named after it, so the least surprising rule is that it is <em>broken</em> like
     * it too: a cargo whose own block keeps its contents when mined gives a cart that does, and a
     * cargo whose block spills gives one that spills. That rule is inherited from vanilla rather
     * than invented, and it costs nothing in reach — a cart can only keep what its cargo could
     * already have kept as a block.
     */
    public boolean cargoKeepsContents(Holder<Block> cargo) {
        return false;
    }

    /** The cargo on a stack, falling back to {@link #defaultCargo()}. */
    public Holder<Block> cargoOrDefault(ItemStack stack) {
        Holder<Block> cargo = cargoOf(stack);
        return cargo == null ? defaultCargo : cargo;
    }

    /**
     * A stack carrying the given cargo, or the default cargo for {@code null}.
     *
     * <p>Null writes no component at all rather than writing the default explicitly, so the
     * ordinary stack stays componentless and keeps whatever naming the fallback item already has.
     */
    public ItemStack stack(@Nullable Holder<Block> cargo) {
        ItemStack stack = new ItemStack(this);
        if (cargo != null) {
            stack.set(cargoComponent, cargo);
        }
        return stack;
    }

    /**
     * This cart's name for a cargo, e.g. "Minecart with Birch Chest".
     *
     * <p>The hook to override for a cargo that should not be named by composition; {@link #getName}
     * and {@link CargoMinecartChest#getTypeName} both come through here, so overriding it keeps a
     * cart and its item saying the same thing.
     */
    public Component cartName(@Nullable Holder<Block> cargo) {
        return CargoMinecarts.cartName(namePatternKey, cargo == null ? defaultCargo : cargo);
    }

    /**
     * Names a stack for its cargo, instead of the one generic name the single registered item would
     * otherwise have.
     *
     * <p>Naming here rather than on the outgoing client stack is what makes it true everywhere:
     * Polymer copies the server stack's name onto its client copy itself, and it does so
     * <em>after</em> the hooks that modify that copy, so a name written there would be overwritten.
     *
     * <p>An {@code item_name} written onto the stack still wins, which is not a nicety. In 26.2
     * {@code Item#getName} <em>is</em> the {@link DataComponents#ITEM_NAME} lookup — every item
     * carries one as a default component and vanilla's implementation returns it — so an override
     * that ignores the component makes {@code item_name} silently dead on this item. That breaks
     * two things beyond an operator's {@code /give}: Polymer writes the composed name onto a client
     * copy as exactly this component, so a client running the mod reads the real item, runs this
     * method, and throws away the name the server sent it; and a Polymer creative tab sends its
     * stacks raw, so a name baked onto one is the only name it will ever have.
     */
    @Override
    public Component getName(ItemStack stack) {
        Component explicit = explicitName(stack);
        return explicit != null ? explicit : cartName(cargoOf(stack));
    }

    // The patch rather than the resolved component, because every item is registered with a default
    // ITEM_NAME (Item.Properties#finalizeInitializer) and reading through to that would shadow the
    // cargo with the one generic name for every stack.
    private static @Nullable Component explicitName(ItemStack stack) {
        return stack.getComponentsPatch().split().added().get(DataComponents.ITEM_NAME);
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context,
            HolderLookup.Provider lookup) {
        Identifier model = cargoModel(cargoOrDefault(stack));
        return model == null ? super.getPolymerItemModel(stack, context, lookup) : model;
    }

    /**
     * The item model for one cargo, or null to use whatever model this item was built with.
     *
     * <p>The hook a mod generating a per-cargo icon overrides; the base class has no way to guess
     * what such a model would be called.
     */
    protected @Nullable Identifier cargoModel(Holder<Block> cargo) {
        return null;
    }
}
