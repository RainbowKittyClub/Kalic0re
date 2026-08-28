package club.rainbowkitty.rkcore.books.hover;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

// Sizes a label to what it labels and works out where it sits relative to it.
final class LabelPlacement {
    // Text display metrics, all at scale 1: DisplayRenderer draws a font pixel at 0.025 blocks on
    // a 10px line, and the default font advances 6px for its widest common glyph.
    private static final double PIXEL = 0.025;
    private static final int LINE_SPACING = 10;
    private static final int CHAR_ADVANCE = 6;

    // Codepoints outside the BMP miss the default font's bitmaps and fall back to a full-width
    // glyph, which unifont advances at width / 2 + 1.
    private static final int WIDE_ADVANCE = 9;

    static final float SCALE = 0.33F;

    // Wrapping at just under the width of the block keeps a label from ever reaching past the
    // thing it labels. The margin absorbs glyphs a resource pack draws wider than it advances
    // them, which the client's own wrapping does not account for.
    private static final double LINE_SPAN = 0.9;
    static final int LINE_WIDTH = (int) (LINE_SPAN / (PIXEL * SCALE));

    private static final double LINE_HEIGHT = LINE_SPACING * PIXEL * SCALE;
    private static final double FACE_CLEARANCE = 0.65;
    private static final double ROW_OFFSET = 0.25;

    // Measured from the frame itself rather than a block centre, so the clearance only has to
    // cover the depth of the framed item.
    private static final double FRAME_CLEARANCE = 0.2;
    private static final double FRAME_RISE = 0.4;

    private LabelPlacement() {
    }

    static Vec3 shelf(Direction facing, int row, Component title) {
        double dy = (row == 0 ? ROW_OFFSET : -ROW_OFFSET) - centring(title);
        return Vec3.ZERO.relative(facing, FACE_CLEARANCE).add(0.0, dy, 0.0);
    }

    static Vec3 frame(Direction facing, Component title) {
        // A floor or ceiling frame has no room above it, so only a wall frame gets the rise that
        // keeps the label off the item.
        double rise = facing.getAxis().isHorizontal() ? FRAME_RISE : 0.0;
        return Vec3.ZERO.relative(facing, FRAME_CLEARANCE).add(0.0, rise - centring(title), 0.0);
    }

    // Text displays stack upward from their position, so extra lines are balanced around the
    // point a single line would occupy.
    private static double centring(Component title) {
        return (wrappedLines(title) - 1) * LINE_HEIGHT / 2.0;
    }

    // The client does the wrapping, so the resulting count has to be predicted here to keep a
    // wrapped label centred. Per-codepoint because Font is client-only.
    private static int wrappedLines(Component title) {
        int lines = 0;
        for (String line : title.getString().split("\n", -1)) {
            lines += Math.max(1, (int) Math.ceil(advance(line) / (double) LINE_WIDTH));
        }
        return lines;
    }

    // Codepoints, not chars: an emoji is two UTF-16 units, so measuring by length would count it
    // twice over at the wrong width.
    private static int advance(String line) {
        int advance = 0;
        for (int i = 0; i < line.length(); ) {
            int codepoint = line.codePointAt(i);
            advance += Character.isBmpCodePoint(codepoint) ? CHAR_ADVANCE : WIDE_ADVANCE;
            i += Character.charCount(codepoint);
        }
        return advance;
    }
}
