package club.rainbowkitty.kalic0re.common.customclick;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Vanilla's chat spam rule for custom clicks: each click adds {@value #STEP} to its player's
 * count, each server tick takes one off, and a count at {@value #LIMIT} disconnects the player
 * as {@code disconnect.spam}, unless they are an operator, whom vanilla exempts. Ten clicks in
 * one burst trip it; a click a second apart never does. Clicks arrive on the server thread, and a
 * leaving player's count goes once the server thread has seen their last packet.
 */
public final class ClickThrottler {
    // Vanilla's chat throttler at its default threshold (TickThrottler(20, 20 * 10)).
    private static final int STEP = 20;
    private static final int LIMIT = 200;
    private static final Component SPAM = Component.translatable("disconnect.spam");
    // Server thread only.
    private static final Map<UUID, Count> COUNTS = new HashMap<>();

    private ClickThrottler() {
    }

    /** Drops a leaving player's count; from {@link CustomClicks#register()}. */
    static void register() {
        // Fabric fires DISCONNECT on the netty thread while the server thread may still hold
        // clicks of the kicked player; a queued task runs after it has drained them.
        ServerPlayConnectionEvents.DISCONNECT.register((listener, server) ->
                server.execute(() -> COUNTS.remove(listener.getPlayer().getUUID())));
    }

    /**
     * Counts one click from {@code player}. Returns true, having disconnected them, when it took
     * them to the limit and they are not exempt; the click is then dropped.
     */
    public static boolean flooded(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        Count count = COUNTS.computeIfAbsent(player.getUUID(), id -> new Count());
        if (count.add(server.getTickCount()) < LIMIT) {
            return false;
        }
        if (server.getPlayerList().isOp(player.nameAndId())
                || server.isSingleplayerOwner(player.nameAndId())) {
            return false;
        }
        player.connection.disconnect(SPAM);
        return true;
    }

    // One player's count, taken down by the ticks since it last grew.
    private static final class Count {
        private int count;
        private int tick;

        // Counts one click at server tick `now` and returns the count.
        private int add(int now) {
            count = Math.max(0, count - (now - tick)) + STEP;
            tick = now;
            return count;
        }
    }
}
