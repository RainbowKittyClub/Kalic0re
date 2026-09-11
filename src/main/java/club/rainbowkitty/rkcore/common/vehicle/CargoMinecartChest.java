package club.rainbowkitty.rkcore.common.vehicle;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * A chest minecart carrying an arbitrary block instead of the vanilla chest.
 *
 * <p>Server-side it is an ordinary vanilla chest minecart — same container, same hoppers, same
 * rails — and the cargo only decides how it looks, what it is called and what it drops. A cargo the
 * client can already render needs no subclass at all: the default {@link #createCargoDisplay()}
 * returns null and vanilla draws the block itself, which is the case that keeps this class honest
 * about being general.
 *
 * <p>The cargo is a plain field rather than synched entity data. A vanilla client is told this cart
 * is one of the vanilla minecarts, and its data tracker is sized for that class, so an extra
 * synched field would index past the end of the client's array and disconnect it.
 */
public class CargoMinecartChest extends MinecartChest implements CartCargo {
    // Saved-data key for the cargo. Capitalised to match the vanilla keys it sits beside.
    private static final String CARGO_TAG = "Cargo";

    private final CartCargoSupport support;
    private final Supplier<CargoCartItem> item;

    // The id is the source of truth and the holder is derived, rather than the other way round,
    // because a cart can outlive its cargo block: a world may be loaded without the mod that
    // registers it, and a block id that resolves to nothing today may resolve again tomorrow.
    // Keeping the id means such a cart reverts to the default cargo for as long as the block is
    // missing and comes back when it returns, instead of being quietly rewritten to the default on
    // the first save. Null in both is the default cargo, which is the ordinary case.
    private @Nullable Identifier cargoId;
    private @Nullable Holder<Block> cargo;

    /**
     * @param type the cart type, which every client is told is a vanilla minecart
     * @param level the level the cart is being created in
     * @param item the cart's item, supplied rather than passed because the entity factory is handed
     *     to {@link CargoMinecarts#register} before the item exists; nothing constructs a cart
     *     until long after registration, so the supplier is always resolvable when it is called
     */
    protected CargoMinecartChest(EntityType<? extends CargoMinecartChest> type, Level level,
            Supplier<CargoCartItem> item) {
        super(type, level);
        this.item = item;
        this.support = CartCargoSupport.of(this);
    }

    /**
     * Answers only the main hand, where a vanilla container cart answers both.
     *
     * <p>Not a preference — it undoes a duplicate. A client decides whether a click did anything by
     * running the vanilla {@code interact} of the type it believes it is holding, and on a result
     * of pass it tries the other hand and sends a second interaction. {@code Minecart#interact}
     * passes whenever the player sneaks or the cart has passengers, and a cart drawing its cargo
     * through a {@link CargoDisplayHolder} always has passengers — so every click arrives twice.
     * The second arrives with whatever is in the off hand, usually nothing, and a container would
     * answer it by opening: that is enough to open this cart out of a sneak-click another mod had
     * already consumed for its own gesture. The main-hand packet is always sent and always first,
     * so refusing the rest costs nothing.
     */
    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        return hand == InteractionHand.MAIN_HAND
                ? super.interact(player, hand, location)
                : InteractionResult.PASS;
    }

    /** This cart's item, which is also where its component, default cargo and naming live. */
    protected CargoCartItem item() {
        return item.get();
    }

    /** The cargo this cart carries, or null for the default. */
    public @Nullable Holder<Block> cargo() {
        return cargo;
    }

    /** The cargo this cart carries, falling back to the item's default. */
    public Holder<Block> cargoOrDefault() {
        return cargo == null ? item().defaultCargo() : cargo;
    }

    /** Sets the cargo and rebuilds how it is drawn. */
    public void setCargo(@Nullable Holder<Block> cargo) {
        apply(cargo == null ? null : BuiltInRegistries.BLOCK.getKey(cargo.value()), cargo);
    }

    /**
     * Sets the cargo by block id, keeping the id even when no block is registered under it. Used
     * when reading saved data, where the id may name a block from a mod this build is without.
     */
    public void setCargoId(@Nullable Identifier cargoId) {
        apply(cargoId, cargoId == null ? null : BuiltInRegistries.BLOCK.get(cargoId).orElse(null));
    }

    // The one gate both ways in go through, so there is no way for a cart to hold a cargo its own
    // item would not read off a stack.
    //
    // A refused cargo is dropped but its id is kept, exactly as an unregistered one is. The two are
    // the same situation from the cart's side - this build will not draw that block today - and
    // keeping the id means neither is destructive: a cart summoned with nonsense reverts to the
    // default cargo rather than being rewritten to it, and one whose cargo a datapack later adds
    // back to the accepted list carries it again. The cart's *item* has no such second chance and
    // needs none; a stack is rewritten only by the player holding it.
    private void apply(@Nullable Identifier id, @Nullable Holder<Block> cargo) {
        this.cargoId = id;
        this.cargo = cargo != null && item().acceptsCargo(cargo) ? cargo : null;
        support.refresh();
    }

    @Override
    public BlockState cargoState() {
        return cargoOrDefault().value().defaultBlockState();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Null here, so a cargo the client can render is drawn by vanilla. Override for a cargo it
     * cannot — anything this mod's consumer registers itself.
     */
    @Override
    public @Nullable CargoDisplayHolder createCargoDisplay() {
        return null;
    }

    /** The cart item this cart drops and picks as, carrying its cargo. */
    @Override
    public ItemStack getPickResult() {
        return item().stack(cargo);
    }

    /**
     * Reads the cargo, and any contents, off the item stack the cart was placed from.
     *
     * <p>The contents are vanilla's rule rather than an invention: a block item carrying a
     * {@link DataComponents#CONTAINER} fills the block entity it places, and a cart placed from a
     * stack that carries one is the same act. Without it a cargo that keeps its contents on the
     * item — a shulker box is the one that does — would have them quietly dropped by whatever
     * recipe turned it into a cart.
     *
     * <p>{@code copyInto} walks this cart's slots rather than the component's, so a container
     * larger than 27 slots would lose the overflow. Every cargo a cart accepts today is a 27-slot
     * container, and the alternative is vanilla's own behaviour for a block entity.
     */
    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        Holder<Block> placed = components.get(item().cargoComponent());
        if (placed != null) {
            setCargo(placed);
        }
        ItemContainerContents contents = components.get(DataComponents.CONTAINER);
        if (contents != null) {
            contents.copyInto(getItemStacks());
        }
    }

    /**
     * Drops the cart carrying its cargo, and — for a cargo that keeps its own contents when
     * broken — carrying what was inside it too.
     *
     * <p>Emptying the cart before {@link #kill} is what moves the contents rather than copying
     * them. Two places spill a container cart's inventory, and both read it after this point:
     * {@code AbstractMinecartContainer#remove} whenever the removal reason destroys, and
     * {@code chestVehicleDestroyed} afterwards. Clearing here is upstream of both, so neither finds
     * anything to drop and the only copy left is the one on the stack.
     *
     * <p>Gated on {@code ENTITY_DROPS} as well as on the cargo, and deliberately: {@code remove}
     * spills without consulting that rule, so clearing the inventory while no cart item is going to
     * be dropped would be the one way to actually destroy the contents.
     */
    @Override
    public void destroy(ServerLevel level, Item dropItem) {
        // Reproduces VehicleEntity#destroy(ServerLevel, Item), which builds a bare stack from the
        // item alone and would drop a cart with no cargo on it.
        boolean dropsItems = level.getGameRules().get(GameRules.ENTITY_DROPS);
        ItemContainerContents kept = null;
        if (dropsItems && item().cargoKeepsContents(cargoOrDefault())) {
            kept = ItemContainerContents.fromItems(getItemStacks());
            clearItemStacks();
        }

        kill(level);
        if (dropsItems) {
            ItemStack dropped = item().stack(cargo);
            dropped.set(DataComponents.CUSTOM_NAME, getCustomName());
            if (kept != null) {
                dropped.set(DataComponents.CONTAINER, kept);
            }
            spawnAtLocation(level, dropped);
        }
    }

    @Override
    public void tick() {
        super.tick();
        support.tick();
    }

    @Override
    public void remove(RemovalReason reason) {
        support.destroy();
        super.remove(reason);
    }

    // No key at all is the default cargo, so the two forms of "no cargo" - never had one, and had a
    // malformed one - both land on the default rather than on a guess.
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (cargoId != null) {
            output.putString(CARGO_TAG, cargoId.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setCargoId(input.getString(CARGO_TAG).map(Identifier::tryParse).orElse(null));
    }

    // The container menu's title and the cart's hover name, per cargo rather than per entity type.
    @Override
    protected Component getTypeName() {
        return item().cartName(cargo);
    }
}
