package club.rainbowkitty.kalic0re.common.text;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;

/**
 * Shows one player a title and subtitle, sending every part of the client's title state each time.
 *
 * <p>The client keeps that state between packets ({@code Hud.setTimes}, {@code setSubtitle},
 * {@code setTitle}). Timing set once applies to every later title until a clear packet resets it.
 * A new title keeps the subtitle already stored, and a subtitle shows only once a title packet
 * starts the display, so a subtitle sent on its own waits for whatever title comes next. A bare
 * title packet therefore inherits an earlier sender's subtitle and timing. {@link #show} sends
 * timing, subtitle and title as one bundle, applied by the client in a single pass, so none of
 * that leaks in.
 *
 * <p>The timing stays on the client afterwards and applies to the next title from any source,
 * vanilla {@code /title} included.
 */
public final class Titles {
    /** Vanilla's timing, the one {@code /title <player> reset} restores: 10, 70 and 20 ticks. */
    public static final Timing DEFAULT_TIMING = new Timing(10, 70, 20);

    private Titles() {
    }

    /** Shows {@code title} over {@code subtitle}; either may be {@link Component#empty()}. */
    public static void show(ServerPlayer player, Component title, Component subtitle,
            Timing timing) {
        player.connection.send(new ClientboundBundlePacket(List.of(
                new ClientboundSetTitlesAnimationPacket(
                        timing.fadeIn(), timing.stay(), timing.fadeOut()),
                new ClientboundSetSubtitleTextPacket(subtitle),
                new ClientboundSetTitleTextPacket(title))));
    }

    /** Shows {@code subtitle} under an empty title, which the client needs to show it at all. */
    public static void showSubtitle(ServerPlayer player, Component subtitle, Timing timing) {
        show(player, Component.empty(), subtitle, timing);
    }

    /** Removes the title and subtitle on screen and sets the timing back to vanilla's. */
    public static void clear(ServerPlayer player) {
        player.connection.send(new ClientboundClearTitlesPacket(true));
    }

    /**
     * How long a title takes to fade in, stays, and takes to fade out, in ticks.
     *
     * <p>None may be negative: the client reads a negative value as "keep the previous one", which
     * is the leftover state {@link Titles} exists to rule out.
     *
     * @param fadeIn ticks spent fading in
     * @param stay ticks at full opacity
     * @param fadeOut ticks spent fading out
     */
    public record Timing(int fadeIn, int stay, int fadeOut) {
        public Timing {
            if (fadeIn < 0 || stay < 0 || fadeOut < 0) {
                throw new IllegalArgumentException("Titles.Timing must not be negative, got fadeIn="
                        + fadeIn + " stay=" + stay + " fadeOut=" + fadeOut);
            }
        }
    }
}
