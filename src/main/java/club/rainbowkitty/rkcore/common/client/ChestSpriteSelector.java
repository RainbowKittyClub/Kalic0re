package club.rainbowkitty.rkcore.common.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * Resolves the chest sprite id for a block entity and chest half, {@code _left}/{@code _right}
 * suffix included. Passed to {@link PolymerChestRenderer}'s constructor so a mod maps its own block
 * entities to its own sprites; the renderer only consumes the result, resolving it against the
 * blocks atlas.
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface ChestSpriteSelector<T extends BlockEntity> {
    /**
     * Sprite id for this block entity and half, as it stitches into the blocks atlas, e.g.
     * {@code plankedchests:block/chest/birch_left}.
     */
    Identifier sprite(T blockEntity, ChestType type);
}
