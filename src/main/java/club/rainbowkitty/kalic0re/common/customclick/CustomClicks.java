package club.rainbowkitty.kalic0re.common.customclick;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Handlers for the custom clicks a player's client sends from a dialog or a text click event, by
 * click id. Vanilla hands every such click to {@code MinecraftServer.handleCustomClickAction}, a
 * no-op that never learns the player, so a consumer routes clicks here from its own mixin. Clicks
 * on an id from {@link #register} count towards {@link ClickThrottler}'s limits; a consumer may
 * replace the package's {@link ClickLimits#DEFAULT} with {@link #limits}. Ids from
 * {@link #registerUnthrottled} skip the limits entirely.
 *
 * <p>This holds the logic; each consumer owns the mixin, since mixins are never shared code. On
 * {@code ServerCommonPacketListenerImpl.handleCustomClickAction}, inject at the INVOKE of
 * {@code MinecraftServer.handleCustomClickAction} (past the thread check, so handlers run on the
 * server thread). When {@code this} is a {@code ServerGamePacketListenerImpl}, call
 * {@link #handle} with its player and the packet's id and payload, and cancel if it returns true.
 * Beyond that, register handlers from the mod initializer; the throttler cleans up after itself.
 *
 * <p>Several consumers installed together each keep a relocated copy with its own handlers and
 * counts. A copy counts and throttles only clicks on its own ids, and cancels them, so they never
 * reach the next copy; a click on an id no copy handles goes on to vanilla uncounted.
 */
public final class CustomClicks {
    private static final Map<Identifier, Entry> HANDLERS = new ConcurrentHashMap<>();

    private CustomClicks() {
    }

    /** Handles every click with {@code id}, within the rate limits; one handler per id. */
    public static void register(Identifier id, Handler handler) {
        put(id, new Entry(handler, true));
    }

    /**
     * Handles every click with {@code id}, never dropped nor counted by the rate limits; one
     * handler per id. For ids that do the same however often they come, such as a close, where a
     * dropped click leaves the server out of step with the client.
     */
    public static void registerUnthrottled(Identifier id, Handler handler) {
        put(id, new Entry(handler, false));
    }

    private static void put(Identifier id, Entry entry) {
        if (HANDLERS.putIfAbsent(id, entry) != null) {
            throw new IllegalStateException("A custom click handler for " + id
                    + " is already registered");
        }
    }

    /** Replaces this copy's rate limits; call from the mod initializer. */
    public static void limits(ClickLimits limits) {
        ClickThrottler.limits(limits);
    }

    /** {@code player}'s peak clicks on this copy's ids over any second, and the dropped ones. */
    public static Stats stats(ServerPlayer player) {
        return ClickThrottler.stats(player);
    }

    /**
     * Runs the handler for {@code id} on the server thread, unless the rate limit drops the click.
     * A click on an id with no handler here, or an unthrottled one, is not counted.
     *
     * @return whether the click is this copy's: handled, or dropped by the limit
     */
    public static boolean handle(ServerPlayer player, Identifier id, Optional<Tag> payload) {
        Entry entry = HANDLERS.get(id);
        if (entry == null) {
            return false;
        }
        if (!entry.throttled() || !ClickThrottler.drop(player)) {
            entry.handler().handle(player, payload);
        }
        return true;
    }

    /** A player's peak clicks in any 20 ticks, and the clicks the rate limit dropped. */
    public record Stats(int peakPerSecond, int dropped) {
    }

    /** What one click id does. */
    @FunctionalInterface
    public interface Handler {
        /** Handles one click from {@code player}, with whatever payload its client sent. */
        void handle(ServerPlayer player, Optional<Tag> payload);
    }

    // A registered handler, and whether its clicks go through the rate limits.
    private record Entry(Handler handler, boolean throttled) {
    }
}
