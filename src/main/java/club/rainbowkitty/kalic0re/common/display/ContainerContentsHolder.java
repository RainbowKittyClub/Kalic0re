package club.rainbowkitty.kalic0re.common.display;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

/**
 * Shows a container's contents as one item display per filled slot, posed by a
 * {@link ContainerContentsVisual}: the items on a crafting table's top, say, or inside a glass
 * chest. An empty slot has no element, so an empty container sends no entities at all.
 *
 * <p>It pulls what to show from a source rather than being handed it, so a holder created before
 * its container exists — during chunk load, before the chunk's block entities — fills itself the
 * first time a player starts watching. After that its owner calls {@link #refreshAtTickEnd()} on
 * every change, or {@link #refresh()} to show one at once. Nothing ticks it: {@code refresh}
 * flushes its own changes.
 *
 * <p>The source is handed the holder, so it can find the container through the holder's current
 * attachment. It must not remember a position: Polymer carries a block's holder along when a
 * piston moves the block ({@code BlockWithElementHolder#createStaticElementHolder}).
 */
public class ContainerContentsHolder extends ElementHolder {
    // Holders asked to refresh as the current tick ends.
    private static final Set<ContainerContentsHolder> PENDING = ConcurrentHashMap.newKeySet();

    private final ContainerContentsVisual visual;
    private final float viewRange;
    private final Source source;

    private ItemDisplayElement[] elements = new ItemDisplayElement[0];
    private ItemStack[] shown = new ItemStack[0];
    private @Nullable Direction shownFacing;
    private boolean filled;

    /**
     * @param visual poses each slot's item
     * @param viewRange each element's view range, a multiple of the client's entity distance
     * @param source the contents to show now
     */
    public ContainerContentsHolder(ContainerContentsVisual visual, float viewRange,
            Source source) {
        this.visual = visual;
        this.viewRange = viewRange;
        this.source = source;
    }

    /** Has {@link #refreshAtTickEnd()} take effect. Call once during mod initialization. */
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> refreshPending());
    }

    /** Shows the source's contents now, sending only what changed. */
    public void refresh() {
        if (show()) {
            tick();
        }
    }

    /**
     * Shows the source's contents as the server tick ends, once however often it is asked within
     * the tick, so a change undone within it sends nothing. A hopper with no room does that: it
     * takes an item out, finds nowhere to put it, and puts it back, which refreshed at once would
     * remove the item's element and add it again. Needs {@link #init()}.
     */
    public void refreshAtTickEnd() {
        PENDING.add(this);
    }

    // Refreshes every holder asked to this tick.
    private static void refreshPending() {
        if (PENDING.isEmpty()) {
            return;
        }
        List<ContainerContentsHolder> holders = List.copyOf(PENDING);
        PENDING.removeAll(holders);
        holders.forEach(ContainerContentsHolder::refresh);
    }

    // The first watcher finds the elements already built, rather than an empty holder.
    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        if (!filled) {
            show();
        }
        return super.startWatching(player);
    }

    // Brings each slot's element in line with the source; false if nothing changed.
    private boolean show() {
        Contents contents = source.contents(this);
        if (contents == null) {
            return false;
        }
        filled = true;
        List<ItemStack> items = contents.items();
        if (elements.length != items.size()) {
            clear();
            elements = new ItemDisplayElement[items.size()];
            shown = new ItemStack[items.size()];
            Arrays.fill(shown, ItemStack.EMPTY);
        }
        boolean turned = contents.facing() != shownFacing;
        shownFacing = contents.facing();
        boolean changed = false;
        for (int slot = 0; slot < items.size(); slot++) {
            changed |= showSlot(slot, items.get(slot), contents.facing(), turned);
        }
        return changed;
    }

    // Adds, removes or re-poses one slot's element; false if it already showed this.
    private boolean showSlot(int slot, ItemStack stack, Direction facing, boolean turned) {
        ItemDisplayElement element = elements[slot];
        if (stack.isEmpty()) {
            shown[slot] = ItemStack.EMPTY;
            if (element == null) {
                return false;
            }
            removeElement(element);
            elements[slot] = null;
            return true;
        }
        if (element != null && !turned && ItemStack.isSameItemSameComponents(shown[slot], stack)) {
            return false;
        }
        shown[slot] = stack.copyWithCount(1);
        boolean added = element == null;
        if (added) {
            element = ItemDisplays.element();
            element.setInterpolationDuration(0);
            element.setViewRange(viewRange);
            elements[slot] = element;
        }
        ContainerContentsVisual.Pose pose = visual.pose(slot, facing, stack);
        element.setItemDisplayContext(pose.context());
        element.setBillboardMode(pose.billboard());
        element.setOffset(pose.offset());
        element.setLeftRotation(pose.leftRotation());
        element.setScale(new Vector3f(pose.scale()));
        element.setTranslation(pose.translation());
        element.setItem(shown[slot]);
        if (added) {
            addElement(element);
        }
        return true;
    }

    // Removes every element, for a source whose slot count changed.
    private void clear() {
        for (ItemDisplayElement element : elements) {
            if (element != null) {
                removeElement(element);
            }
        }
    }

    /** Where a {@link ContainerContentsHolder} finds what to show. */
    @FunctionalInterface
    public interface Source {
        /** The contents to show now, or null while the container is unavailable. */
        public @Nullable Contents contents(ContainerContentsHolder holder);
    }

    /**
     * What a {@link ContainerContentsHolder} shows.
     *
     * @param items the container's slots, in order; an empty stack is an empty slot
     * @param facing the horizontal direction the layout is turned to
     */
    public record Contents(List<ItemStack> items, Direction facing) {
    }
}
