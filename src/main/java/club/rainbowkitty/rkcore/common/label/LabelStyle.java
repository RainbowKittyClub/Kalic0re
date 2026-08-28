package club.rainbowkitty.rkcore.common.label;

import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;

/**
 * How a hover label is drawn.
 *
 * @param scale      size of the text relative to a vanilla nametag
 * @param billboard  how the text turns to face the viewer
 * @param seeThrough whether the text draws through solid blocks
 * @param lineSpan   width in blocks to wrap at, independent of {@code scale}
 * @param alignment  how wrapped lines sit relative to each other
 * @param shadow     whether the text is drawn with a drop shadow
 * @param brightness fixed light level, or null to take the light at the label's position
 */
public record LabelStyle(float scale, Display.BillboardConstraints billboard, boolean seeThrough,
        double lineSpan, Display.TextDisplay.Align alignment, boolean shadow,
        Brightness brightness) {
}
