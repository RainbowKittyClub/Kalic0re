package club.rainbowkitty.kalic0re.common.portal;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The look of one interior block: an item for a client with the server's resource pack, a block
 * state for any other. Both are drawn as the X plane's slab, its faces toward north and south,
 * and turned for the other planes.
 *
 * @param packed the item whose model a client with the pack draws, one block wide and high
 * @param plain the block state every other client draws, such as {@code nether_portal[axis=x]},
 *        which is also what every client sees shatter when a portal block breaks
 */
public record PortalMaterial(ItemStack packed, BlockState plain) {
}
