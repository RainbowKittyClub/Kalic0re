package club.rainbowkitty.kalic0re.common.config;

import java.util.Optional;

import com.mojang.serialization.DataResult;
import me.lucko.fabric.api.permissions.v0.Options;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.server.level.ServerPlayer;

// Forced player values without the hub: an option (LuckPerms meta) named like the setting's
// node, asked through fabric-permissions-api on every read, so nothing is cached and a change of
// force fires no notice.
final class Forcing {
    private static final boolean API_LOADED =
            FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");

    private Forcing() {
    }

    // The value the server forces on player for setting, if any parses; one that does not is
    // ignored and logged once per value.
    static <T> Optional<T> forced(ServerPlayer player, Setting<T> setting) {
        if (!API_LOADED) {
            return Optional.empty();
        }
        Optional<String> option = ApiGate.option(player, setting.node());
        if (option.isEmpty()) {
            return Optional.empty();
        }
        String raw = option.get();
        DataResult<T> parsed = setting.kind().parse(raw);
        parsed.error().ifPresent(error -> setting.reportOnce("forced:" + raw,
                "Ignoring forced value '" + raw + "' of " + setting.node() + ": "
                        + error.message()));
        return parsed.result();
    }

    // The only code naming fabric-permissions-api, loaded only once the API is known present.
    private static final class ApiGate {
        private ApiGate() {
        }

        // The option's value in the player's current contexts.
        private static Optional<String> option(ServerPlayer player, String key) {
            return Options.get(player, key);
        }
    }
}
