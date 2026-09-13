package club.rainbowkitty.kalic0re.common.label;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * Something a player is looking at that wants a label, and everything needed to draw one over it.
 *
 * <p>The offset is in the label's own frame rather than the world's, because it is applied after
 * whatever rotation the style asks for: +Y runs up the face of the text and +Z out towards the
 * reader. For a {@code FIXED} label those come out as world up and {@code yaw}; for a billboard
 * they follow the viewer, which keeps the text sat over its target on screen from any angle.
 *
 * @param level  world the anchor is in
 * @param anchor world position the label is attached to
 * @param yaw    direction the text faces; ignored when the style billboards
 * @param text   the label itself
 * @param offset displacement from the anchor to the bottom of the label
 * @param style  how the text is drawn
 */
public record LabelTarget(ServerLevel level, Vec3 anchor, float yaw, LabelText text, Vec3 offset,
        LabelStyle style) {
}
