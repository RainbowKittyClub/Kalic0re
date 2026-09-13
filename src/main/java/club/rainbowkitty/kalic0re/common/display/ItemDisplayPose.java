package club.rainbowkitty.kalic0re.common.display;

import com.mojang.math.Axis;
import org.joml.Quaternionf;

/**
 * The turn every rendered item display has baked into it, and the orientation that accounts for it.
 *
 * <p>Anything posing an {@code ItemDisplayElement} to face a particular way has to cancel this, and
 * the cost of not knowing about it is a model facing backwards — which looks like a sign error
 * anywhere else in the maths, so it gets one name here instead of being re-derived per caller.
 *
 * <p>Shared by relocation, not a mod dependency (see each consuming mod's {@code build.gradle}).
 */
public final class ItemDisplayPose {
    /**
     * The extra 180° Y turn {@code DisplayRenderer$ItemDisplayRenderer#submitInner} applies to
     * every {@code Display$ItemDisplay} carrying a non-empty stack, before the stack is drawn.
     *
     * <p>Unconditional, so it is part of the pose whether or not a caller meant it to be. A vector
     * expressed in the display's own frame — a rotation pivot, say — turns with the geometry and so
     * carries this turn too, which for a 180° Y turn means the opposite sign on x and z.
     */
    public static final float RENDERER_YAW_OFFSET = 180.0f;

    private ItemDisplayPose() {
    }

    /**
     * The rotation that poses an item display turned {@code yaw} degrees about Y, corrected for
     * {@link #RENDERER_YAW_OFFSET}.
     *
     * @param yaw the facing wanted, as an {@link Axis#YP} rotation — which is the opposite sign
     *     from a Minecraft yaw, so a block's facing arrives here as {@code -facing.toYRot()}
     */
    public static Quaternionf orientation(float yaw) {
        return Axis.YP.rotationDegrees(RENDERER_YAW_OFFSET + yaw);
    }
}
