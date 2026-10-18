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
 * no-op that never learns the player, so a consumer routes clicks here from its own mixin. Every
 * click counts towards {@link ClickThrottler}'s limit, handled or not.
 *
 * <p>This holds the logic; each consumer owns the mixin, since mixins are never shared code. On
 * {@code ServerCommonPacketListenerImpl.handleCustomClickAction}, inject at the INVOKE of
 * {@code MinecraftServer.handleCustomClickAction} (past the thread check, so handlers run on the
 * server thread). When {@code this} is a {@code ServerGamePacketListenerImpl}, call
 * {@link #handle} with its player and the packet's id and payload, and cancel if it returns true.
 * Call {@link #register()} once from the mod initializer.
 *
 * <p>Several consumers installed together each keep a relocated copy with its own handlers and
 * counts. A click one copy handles is cancelled there and never reaches the next; a click none
 * handles goes on to vanilla. Each copy counts every click it sees against its own limit.
 */
public final class CustomClicks {
    private static final Map<Identifier, Handler> HANDLERS = new ConcurrentHashMap<>();

    private CustomClicks() {
    }

    /** Hooks the throttler's cleanup of leaving players; from the mod initializer. */
    public static void register() {
        ClickThrottler.register();
    }

    /** Handles every click with {@code id}; one handler per id. */
    public static void register(Identifier id, Handler handler) {
        if (HANDLERS.putIfAbsent(id, handler) != null) {
            throw new IllegalStateException("A custom click handler for " + id
                    + " is already registered");
        }
    }

    /**
     * Runs the handler for {@code id} on the server thread, unless the click floods.
     *
     * @return whether the click is handled: by a registered handler, or dropped as a flood
     */
    public static boolean handle(ServerPlayer player, Identifier id, Optional<Tag> payload) {
        if (ClickThrottler.flooded(player)) {
            return true;
        }
        Handler handler = HANDLERS.get(id);
        if (handler == null) {
            return false;
        }
        handler.handle(player, payload);
        return true;
    }

    /** What one click id does. */
    @FunctionalInterface
    public interface Handler {
        /** Handles one click from {@code player}, with whatever payload its client sent. */
        void handle(ServerPlayer player, Optional<Tag> payload);
    }
}
