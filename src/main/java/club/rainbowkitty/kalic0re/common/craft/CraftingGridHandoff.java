package club.rainbowkitty.kalic0re.common.craft;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.CraftingContainer;

/**
 * Hands a shared crafting grid to {@code AbstractCraftingMenu}'s constructor, which otherwise
 * builds a fresh {@code TransientCraftingContainer} and assigns it to its {@code final}
 * {@code craftSlots} before {@code CraftingMenu} builds any slot on it. A subclass cannot pass the
 * grid in, its own fields being unset until that constructor returns, so the grid travels through
 * a thread-local instead.
 *
 * <p>Each consumer owns the mixin that reads it, since mixins are never shared code: an inject at
 * the tail of {@code AbstractCraftingMenu}'s constructor, with {@code craftSlots} shadowed
 * {@code @Mutable}, that assigns {@link #take()} when it returns a grid.
 */
public final class CraftingGridHandoff {
    private static final ThreadLocal<CraftingContainer> PENDING = new ThreadLocal<>();

    private CraftingGridHandoff() {
    }

    /**
     * Builds a menu whose {@code craftSlots} is {@code grid}.
     *
     * @param grid the grid the menu's slots are built on
     * @param factory constructs the menu; must construct exactly one crafting menu
     * @return the menu {@code factory} built
     */
    public static <M extends AbstractCraftingMenu> M construct(CraftingContainer grid,
            Supplier<M> factory) {
        PENDING.set(grid);
        try {
            return factory.get();
        } finally {
            PENDING.remove();
        }
    }

    /** The grid for the crafting menu now being constructed, once, or null for any other menu. */
    public static @Nullable CraftingContainer take() {
        CraftingContainer grid = PENDING.get();
        PENDING.remove();
        return grid;
    }
}
