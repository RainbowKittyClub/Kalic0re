package club.rainbowkitty.kalic0re.common.craft;

import java.util.List;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Vanilla's crafting menu, with a grid of its own that goes back to the player on close, valid at
 * any given block. {@code CraftingMenu#stillValid} names {@code Blocks.CRAFTING_TABLE} itself, so
 * at any other block it would close the tick after opening.
 */
public class BlockCraftingMenu extends CraftingMenu {
    private final ContainerLevelAccess access;
    private final Block block;
    private boolean filling;

    /**
     * @param containerId the menu's container id
     * @param inventory the opening player's inventory
     * @param access the level and position of the block the menu belongs to
     * @param block the block that must stay at that position for the menu to stay open
     */
    public BlockCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access,
            Block block) {
        super(containerId, inventory, access);
        this.access = access;
        this.block = block;
    }

    /**
     * Puts stacks in the grid, slot by slot, as the menu opens, and works out the output once they
     * are all in. The grid hands them to the player on close like anything else in it.
     *
     * @param stacks the stacks, in slot order; the menu takes them rather than copies
     */
    public void fillGrid(List<ItemStack> stacks) {
        filling = true;
        try {
            for (int slot = 0; slot < Math.min(stacks.size(), craftSlots.getContainerSize());
                    slot++) {
                craftSlots.setItem(slot, stacks.get(slot));
            }
        } finally {
            filling = false;
        }
        slotsChanged(craftSlots);
    }

    // The output is worked out once fillGrid is done, not after every slot.
    @Override
    public void slotsChanged(Container container) {
        if (!filling) {
            super.slotsChanged(container);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, block);
    }
}
