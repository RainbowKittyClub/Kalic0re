package club.rainbowkitty.kalic0re.common.config;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import club.rainbowkitty.kalic0re.common.rule.FallbackRules;

// Which backend answers settings, decided once every initializer has run, and the hub's bridge
// functions when it is present. Static per shaded copy, so per consumer mod.
final class ConfigRuntime {
    static final String DECLARE_KEY = "rkcorecommon:config/v1/declare";
    static final String PLAYER_KEY = "rkcorecommon:config/v1/player";
    static final String SERVER_KEY = "rkcorecommon:config/v1/server";
    static final String LISTEN_KEY = "rkcorecommon:config/v1/listen";

    private static final Logger LOGGER = LoggerFactory.getLogger("kalic0re/config");
    private static final List<ModConfig> CONFIGS = new CopyOnWriteArrayList<>();
    // After every default-phase listener, so the hub has loaded its values whatever the mod order
    // and a value a consumer read before that is dropped from the cache.
    private static final Identifier STARTING_PHASE =
            Identifier.fromNamespaceAndPath("kalic0re", "config");

    private static volatile @Nullable Mode mode;
    private static volatile @Nullable Function<Identifier, JsonElement> serverValues;
    private static volatile @Nullable BiFunction<UUID, Identifier, JsonElement> playerValues;
    private static volatile @Nullable BiConsumer<String, BiConsumer<UUID, Identifier>> listen;
    private static boolean hooked;

    private ConfigRuntime() {
    }

    // Who answers: the hub, this mod's own rules and attachment, or nobody (defaults only).
    enum Mode {
        HUB, FALLBACK, BROKEN
    }

    // Tracks config for server start and stop, hooking the lifecycle events on first use.
    static synchronized void add(ModConfig config) {
        CONFIGS.add(config);
        if (!hooked) {
            hooked = true;
            ServerLifecycleEvents.SERVER_STARTING.addPhaseOrdering(Event.DEFAULT_PHASE,
                    STARTING_PHASE);
            ServerLifecycleEvents.SERVER_STARTING.register(STARTING_PHASE,
                    ConfigRuntime::starting);
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> CONFIGS.forEach(
                    ModConfig::serverStopped));
        }
    }

    // The mode, resolved on first ask. Loader runs every initializer before any server exists,
    // so command registration and SERVER_STARTING both see the same answer.
    static Mode mode() {
        Mode settled = mode;
        return settled != null ? settled : resolve();
    }

    // Settles the mode once: fallback without rkcore-common, the hub when it published all
    // three read keys, and defaults only when it is loaded but did not.
    @SuppressWarnings("unchecked")
    private static synchronized Mode resolve() {
        if (mode != null) {
            return mode;
        }
        if (!FallbackRules.hubLoaded()) {
            mode = Mode.FALLBACK;
            return mode;
        }
        Object server = FabricLoader.getInstance().getObjectShare().get(SERVER_KEY);
        Object player = FabricLoader.getInstance().getObjectShare().get(PLAYER_KEY);
        Object notices = FabricLoader.getInstance().getObjectShare().get(LISTEN_KEY);
        if (server instanceof Function<?, ?> s && player instanceof BiFunction<?, ?, ?> p
                && notices instanceof BiConsumer<?, ?> l) {
            serverValues = (Function<Identifier, JsonElement>) s;
            playerValues = (BiFunction<UUID, Identifier, JsonElement>) p;
            listen = (BiConsumer<String, BiConsumer<UUID, Identifier>>) l;
            mode = Mode.HUB;
        } else {
            mode = Mode.BROKEN;
        }
        return mode;
    }

    // The hub's effective server value for id, or null for a setting it does not know.
    static @Nullable JsonElement hubServer(Identifier id) {
        return serverValues == null ? null : serverValues.apply(id);
    }

    // The hub's effective value of id for player, or null for a setting it does not know.
    static @Nullable JsonElement hubPlayer(UUID player, Identifier id) {
        return playerValues == null ? null : playerValues.apply(player, id);
    }

    // Subscribes handler to the hub's change notices for modId.
    static void listen(String modId, BiConsumer<UUID, Identifier> handler) {
        if (listen != null) {
            listen.accept(modId, handler);
        }
    }

    // Hands every config the starting server and the settled mode, once the hub has started.
    private static void starting(MinecraftServer server) {
        Mode settled = mode();
        if (settled == Mode.BROKEN) {
            LOGGER.error("rkcorecommon is loaded but published no settings bridge; every"
                    + " setting of {} answers its default",
                    CONFIGS.stream().map(ModConfig::modId).toList());
        }
        CONFIGS.forEach(config -> config.serverStarting(server, settled));
    }
}
