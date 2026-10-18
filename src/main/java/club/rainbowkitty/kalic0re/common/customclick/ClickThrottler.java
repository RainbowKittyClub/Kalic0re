package club.rainbowkitty.kalic0re.common.customclick;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Rate limits each player's custom clicks by {@link ClickLimits}: a bucket of {@code burst} clicks
 * refilled at {@code perSecond}, with a click on an empty bucket dropped rather than punished. A
 * nonzero {@code kickPerSecond} drains a second bucket that every click fills; overflowing its two
 * seconds' worth disconnects as {@code disconnect.spam}, operators exempt as vanilla exempts them
 * from chat spam. Each player's peak clicks over any 20 ticks is kept too, to size those limits
 * from real clicking.
 *
 * <p>Clicks arrive on the server thread. The class hooks its own cleanup when first loaded, before
 * any bucket exists, and a leaving player's state goes once the server thread has seen their last
 * packet.
 */
public final class ClickThrottler {
    private static final int TICKS_PER_SECOND = 20;
    private static final int KICK_SECONDS = 2;
    private static final Component SPAM = Component.translatable("disconnect.spam");
    // Server thread only.
    private static final Map<UUID, Bucket> BUCKETS = new HashMap<>();
    private static volatile ClickLimits limits = ClickLimits.DEFAULT;

    static {
        // Fabric fires DISCONNECT on the netty thread while the server thread may still hold
        // clicks of the kicked player; a queued task runs after it has drained them.
        ServerPlayConnectionEvents.DISCONNECT.register((listener, server) ->
                server.execute(() -> BUCKETS.remove(listener.getPlayer().getUUID())));
    }

    private ClickThrottler() {
    }

    static void limits(ClickLimits newLimits) {
        limits = newLimits;
    }

    /**
     * Counts one click from {@code player}. Returns true when it is to be dropped: the bucket was
     * empty, or the click took them past the disconnect rate and they were disconnected.
     */
    static boolean drop(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        ClickLimits current = limits;
        Bucket bucket = BUCKETS.computeIfAbsent(player.getUUID(), id -> new Bucket(current));
        int now = server.getTickCount();
        bucket.count(now, current);
        if (current.kickPerSecond() > 0 && bucket.flooding(current)
                && !server.getPlayerList().isOp(player.nameAndId())
                && !server.isSingleplayerOwner(player.nameAndId())) {
            player.connection.disconnect(SPAM);
            return true;
        }
        return !bucket.take();
    }

    /** {@code player}'s peak clicks over any 20 ticks so far, and how many were dropped. */
    static CustomClicks.Stats stats(ServerPlayer player) {
        Bucket bucket = BUCKETS.get(player.getUUID());
        return bucket == null ? new CustomClicks.Stats(0, 0)
                : new CustomClicks.Stats(bucket.peak, bucket.dropped);
    }

    // One player's bucket, flood count and last-second window.
    private static final class Bucket {
        private final int[] window = new int[TICKS_PER_SECOND];
        private double tokens;
        private double flood;
        private int windowSum;
        private int tick;
        private int peak;
        private int dropped;

        private Bucket(ClickLimits limits) {
            tokens = limits.burst();
        }

        // Refills by the ticks since the last click and records this one in the window.
        private void count(int now, ClickLimits limits) {
            int elapsed = Math.max(0, now - tick);
            tokens = Math.min(limits.burst(),
                    tokens + elapsed * (double) limits.perSecond() / TICKS_PER_SECOND);
            flood = Math.max(0.0,
                    flood - elapsed * (double) limits.kickPerSecond() / TICKS_PER_SECOND) + 1;
            for (int t = 1; t <= Math.min(elapsed, TICKS_PER_SECOND); t++) {
                int slot = (tick + t) % TICKS_PER_SECOND;
                windowSum -= window[slot];
                window[slot] = 0;
            }
            tick = now;
            window[now % TICKS_PER_SECOND]++;
            windowSum++;
            peak = Math.max(peak, windowSum);
        }

        // Clicks beyond the kick rate pile up; two seconds' worth of excess disconnects.
        private boolean flooding(ClickLimits limits) {
            return flood > limits.kickPerSecond() * KICK_SECONDS;
        }

        private boolean take() {
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            dropped++;
            return false;
        }
    }
}
