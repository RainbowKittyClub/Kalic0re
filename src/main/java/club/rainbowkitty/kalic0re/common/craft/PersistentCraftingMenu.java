package club.rainbowkitty.kalic0re.common.craft;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Vanilla's crafting menu over a {@link CraftingGridBlockEntity}, which keeps its contents when the
 * menu closes. Every menu open on one grid shares its slots, so each viewer sees the others' moves,
 * and each refreshes its own output when the grid tells it of a change.
 *
 * <p>Built with {@link ContainerLevelAccess#NULL}: {@code CraftingMenu} uses its access only to
 * recompute the output, to hand the grid back on close and to check validity, and this class
 * replaces the first and last, so the grid is never handed back.
 */
public final class PersistentCraftingMenu extends CraftingMenu {
    private final CraftingGridBlockEntity grid;
    private final Player player;
    private boolean placingRecipe;

    // Only through create(), which hands the grid to AbstractCraftingMenu's constructor.
    private PersistentCraftingMenu(int containerId, Inventory inventory,
            CraftingGridBlockEntity grid) {
        super(containerId, inventory, ContainerLevelAccess.NULL);
        if (craftSlots != grid) {
            throw new IllegalStateException("Crafting grid not handed to AbstractCraftingMenu:"
                    + " the consumer's CraftingGridHandoff mixin did not apply");
        }
        this.grid = grid;
        this.player = inventory.player;
        grid.startOpen(player);
        grid.attach(this);
        refreshResult();
    }

    /**
     * Opens a menu on a grid.
     *
     * @param containerId the menu's container id
     * @param inventory the opening player's inventory
     * @param grid the grid the menu's slots are built on
     * @return the menu
     */
    public static PersistentCraftingMenu create(int containerId, Inventory inventory,
            CraftingGridBlockEntity grid) {
        return CraftingGridHandoff.construct(grid,
                () -> new PersistentCraftingMenu(containerId, inventory, grid));
    }

    /** The player this menu belongs to. */
    public ContainerUser user() {
        return player;
    }

    @Override
    public boolean stillValid(Player player) {
        return grid.stillValid(player);
    }

    // Names the clicking player to the grid, which takes their facing if the click empties it.
    @Override
    public void clicked(int slotIndex, int buttonNum, ContainerInput containerInput,
            Player player) {
        grid.beginAction(player);
        try {
            super.clicked(slotIndex, buttonNum, containerInput, player);
        } finally {
            grid.endAction();
        }
    }

    // As clicked(): the recipe book empties the grid before refilling it.
    @Override
    public RecipeBookMenu.PostPlaceAction handlePlacement(boolean useMaxItems,
            boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level,
            Inventory inventory) {
        grid.beginAction(inventory.player);
        try {
            return super.handlePlacement(useMaxItems, allowDroppingItemsToClear, recipe, level,
                    inventory);
        } finally {
            grid.endAction();
        }
    }

    // Vanilla's slotsChanged goes through the access this menu does not have.
    @Override
    public void slotsChanged(Container container) {
        if (!placingRecipe) {
            refreshResult();
        }
    }

    @Override
    public void beginPlacingRecipe() {
        placingRecipe = true;
        super.beginPlacingRecipe();
    }

    @Override
    public void finishPlacingRecipe(ServerLevel level, RecipeHolder<CraftingRecipe> recipe) {
        placingRecipe = false;
        super.finishPlacingRecipe(level, recipe);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        grid.detach(this);
        grid.stopOpen(player);
    }

    // Recomputes the output from the grid and sends it to this menu's player.
    private void refreshResult() {
        if (player instanceof ServerPlayer serverPlayer) {
            slotChangedCraftingGrid(this, serverPlayer.level(), serverPlayer, craftSlots,
                    resultSlots, null);
        }
    }
}
