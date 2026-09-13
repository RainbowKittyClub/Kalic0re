package club.rainbowkitty.kalic0re.common.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.resources.Identifier;

/**
 * {@link ChestRenderState} plus the one thing vanilla's fixed {@code ChestMaterialType} enum can't
 * express: an arbitrary chest sprite. {@link PolymerChestRenderer} fills {@link #sprite} from its
 * {@link ChestSpriteSelector} each frame.
 */
@Environment(EnvType.CLIENT)
public class PolymerChestRenderState extends ChestRenderState {
    /** Resolved chest sprite id, e.g. {@code plankedchests:birch_left}. */
    public Identifier sprite;
}
