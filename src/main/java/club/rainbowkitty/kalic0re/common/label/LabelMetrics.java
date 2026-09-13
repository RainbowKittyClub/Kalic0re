package club.rainbowkitty.kalic0re.common.label;

import net.minecraft.network.chat.Component;

/** Predicts how the client will lay a label out, since {@code Font} is client-only. */
public final class LabelMetrics {
    // Text display metrics, all at scale 1: DisplayRenderer draws a font pixel at 0.025 blocks on
    // a 10px line, and the default font advances 6px for its widest common glyph.
    private static final double PIXEL = 0.025;
    private static final int LINE_SPACING = 10;
    private static final int CHAR_ADVANCE = 6;

    // Codepoints outside the BMP miss the default font's bitmaps and fall back to a full-width
    // glyph, which unifont advances at width / 2 + 1.
    private static final int WIDE_ADVANCE = 9;

    // Clear space between the two plates, as a fraction of a line. Taken off the line rather than
    // fixed so the separation reads the same whatever the label is scaled to.
    private static final double PLATE_GAP = 0.5;

    private LabelMetrics() {
    }

    /** The wrap width to hand the client, converting a span in blocks at the style's own scale. */
    public static int lineWidth(LabelStyle style) {
        return (int) (style.lineSpan() / (PIXEL * style.scale()));
    }

    /** How tall the text will end up once the client has wrapped it. */
    public static double height(LabelStyle style, Component text) {
        return wrappedLines(style, text) * lineHeight(style);
    }

    /** How tall the whole label will end up, both plates and the space between them. */
    public static double height(LabelStyle style, LabelText text) {
        Component subheading = text.subheading();
        double heading = height(style, text.heading());
        return subheading == null ? heading : heading + gap(style) + height(style, subheading);
    }

    /**
     * How far to drop a label so that a tall one stays centred on the point a single line would
     * occupy. Text displays stack upward from their position.
     */
    public static double centring(LabelStyle style, LabelText text) {
        return (height(style, text) - lineHeight(style)) / 2.0;
    }

    /** The clear space between the heading plate and the one under it. */
    public static double gap(LabelStyle style) {
        return lineHeight(style) * PLATE_GAP;
    }

    private static double lineHeight(LabelStyle style) {
        return LINE_SPACING * PIXEL * style.scale();
    }

    private static int wrappedLines(LabelStyle style, Component text) {
        int width = lineWidth(style);
        int lines = 0;
        for (String line : text.getString().split("\n", -1)) {
            lines += Math.max(1, (int) Math.ceil(advance(line) / (double) width));
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
