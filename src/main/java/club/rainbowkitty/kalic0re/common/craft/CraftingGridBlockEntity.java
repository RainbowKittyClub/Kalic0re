package club.rainbowkitty.kalic0re.common.craft;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * A 3×3 crafting grid that keeps its contents between uses, as a chest does. Being a
 * {@code BaseContainerBlockEntity} gives it what vanilla gives a chest: a lock, a custom name as
 * the menu title, contents carried by the {@code container} component, contents dropped when
 * broken, and the container checks claim and logging mods key on.
 *
 * <p>Every {@link PersistentCraftingMenu} open on it shares its slots. It tells each one of every
 * change, from {@link #setChanged}: a stack merged into a slot grows in place and reports only
 * through {@code Slot#setChanged}, which is what reaches it. Vanilla's click path calls
 * {@code setChanged} several times per click, so it tells them only when the contents differ from
 * what they were last told: each telling recomputes and resends a menu's output.
 *
 * <p>It also keeps a horizontal facing, for a display of its contents to face the way the grid
 * read in the menu: taken from whoever opens it while it is empty, and again whenever a change
 * leaves it empty, from the player who made the change or else anyone with it open.
 *
 * <p>The subclass decides when it keeps items and when hoppers reach it, and may give a player a
 * grid of their own instead of this one.
 */
public abstract class CraftingGridBlockEntity extends BaseContainerBlockEntity
        implements CraftingContainer, WorldlyContainer {
    /** Grid width, in slots. */
    public static final int WIDTH = 3;
    /** Grid height, in slots. */
    public static final int HEIGHT = 3;
    /** Slot count. */
    public static final int SIZE = WIDTH * HEIGHT;

    private static final Component DEFAULT_NAME = Component.translatable("container.crafting");
    private static final int[] EVERY_SLOT = IntStream.range(0, SIZE).toArray();
    private static final int[] NO_SLOTS = new int[0];

    private final List<PersistentCraftingMenu> menus = new ArrayList<>();
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private Direction facing = Direction.NORTH;
    // The player whose click or recipe placement is changing the grid now, if any.
    private @Nullable ContainerUser actor;
    // Copies of the contents as the menus and display were last told them, and the facing then.
    private final NonNullList<ItemStack> toldItems = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private @Nullable Direction toldFacing;

    /**
     * @param type the subclass's block entity type
     * @param pos the grid's position
     * @param state the grid's block state
     */
    protected CraftingGridBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * The horizontal direction the grid's top row lies toward: the facing of whoever last opened it
     * empty or emptied it, north until someone has.
     */
    public Direction facing() {
        return facing;
    }

    /** The number of slots holding anything, 0 to 9: the crafter's comparator signal. */
    public int filledSlotCount() {
        int count = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /** Registers a menu to be told of changes. For {@link PersistentCraftingMenu}'s use. */
    public void attach(PersistentCraftingMenu menu) {
        menus.add(menu);
    }

    /** Stops telling a menu of changes. For {@link PersistentCraftingMenu}'s use. */
    public void detach(PersistentCraftingMenu menu) {
        menus.remove(menu);
    }

    /**
     * Names the player about to change the grid, whose facing it takes if they empty it. For
     * {@link PersistentCraftingMenu}'s use, paired with {@link #endAction()}.
     */
    public void beginAction(ContainerUser user) {
        actor = user;
    }

    /** Ends {@link #beginAction}. */
    public void endAction() {
        actor = null;
    }

    // An empty grid takes its opener's facing, through the same path as emptying it; a spectator's
    // is not taken.
    @Override
    public void startOpen(ContainerUser user) {
        if (isEmpty() && !user.getLivingEntity().isSpectator()
                && user.getLivingEntity().getDirection() != facing) {
            beginAction(user);
            try {
                setChanged();
            } finally {
                endAction();
            }
        }
    }

    @Override
    public void setChanged() {
        if (isEmpty()) {
            ContainerUser emptier = actor != null ? actor : firstViewer();
            if (emptier != null) {
                facing = emptier.getLivingEntity().getDirection();
            }
        }
        super.setChanged();
        boolean turned = facing != toldFacing;
        toldFacing = facing;
        boolean refilled = takeContentChanges();
        if (refilled) {
            for (PersistentCraftingMenu menu : List.copyOf(menus)) {
                menu.slotsChanged(this);
            }
        }
        if (refilled || turned) {
            onGridChanged();
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        setChanged();
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void fillStackedContents(StackedItemContents contents) {
        for (ItemStack stack : items) {
            contents.accountSimpleStack(stack);
        }
    }

    // Every slot to a hopper, dropper or crafter when the subclass allows it, none otherwise.
    @Override
    public int[] getSlotsForFace(Direction direction) {
        return holdsItems() && hoppersCanAccess() ? EVERY_SLOT : NO_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack,
            @Nullable Direction direction) {
        return holdsItems() && hoppersCanAccess();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return holdsItems() && hoppersCanAccess();
    }

    /**
     * Whether the grid keeps its contents between uses. When it does not, opening it gives the
     * player a grid of their own, handed back on close, and anything this one still holds moves
     * into theirs first: it goes back to whoever opens it next, as if they had left it there.
     */
    protected abstract boolean holdsItems();

    /** Whether hoppers, droppers and crafters may fill and drain the grid while it keeps items. */
    protected abstract boolean hoppersCanAccess();

    /**
     * Whether this player must get a grid of their own rather than this one, which they then never
     * see or touch. False unless a subclass says otherwise.
     */
    protected boolean needsOwnGrid(Player player) {
        return false;
    }

    /** Called after the grid's contents or facing change, the level possibly still unset. */
    protected void onGridChanged() {
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        if (needsOwnGrid(inventory.player)) {
            return ownGridMenu(containerId, inventory);
        }
        if (!holdsItems()) {
            BlockCraftingMenu menu = ownGridMenu(containerId, inventory);
            // A spectator looks without taking what is left.
            if (!inventory.player.isSpectator() && !isEmpty()) {
                menu.fillGrid(items);
                clearContent();
            }
            return menu;
        }
        return PersistentCraftingMenu.create(containerId, inventory, this);
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        facing = input.read("facing", Direction.CODEC)
                .filter(direction -> direction.getAxis().isHorizontal())
                .orElse(Direction.NORTH);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.store("facing", Direction.CODEC, facing);
    }

    // Vanilla's crafting menu at this block, with a grid of the player's own.
    private BlockCraftingMenu ownGridMenu(int containerId, Inventory inventory) {
        return new BlockCraftingMenu(containerId, inventory,
                level != null ? ContainerLevelAccess.create(level, worldPosition)
                        : ContainerLevelAccess.NULL,
                getBlockState().getBlock());
    }

    // Whether any slot differs from what the menus were last told, recording the contents as told.
    // Compared by count too: a stack merged into a slot changes only in count.
    private boolean takeContentChanges() {
        boolean changed = false;
        for (int slot = 0; slot < SIZE; slot++) {
            ItemStack stack = items.get(slot);
            if (!ItemStack.matches(toldItems.get(slot), stack)) {
                toldItems.set(slot, stack.copy());
                changed = true;
            }
        }
        return changed;
    }

    // The first player with the grid open who is not a spectator, if any.
    private @Nullable ContainerUser firstViewer() {
        for (PersistentCraftingMenu menu : menus) {
            if (!menu.user().getLivingEntity().isSpectator()) {
                return menu.user();
            }
        }
        return null;
    }
}
