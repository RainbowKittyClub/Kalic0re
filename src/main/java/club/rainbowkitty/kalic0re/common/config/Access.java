package club.rainbowkitty.kalic0re.common.config;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;

import club.rainbowkitty.kalic0re.common.command.CommandPermissions;

// What one viewer may do with one setting without the hub, by the contract's model: holding the
// node edits it, else its declared denial applies; a forced value counts as denied, and a world
// lacking its features hides it.
enum Access {
    EDITABLE, READONLY, HIDDEN;

    // Whether the viewer sees the setting at all.
    public boolean visible() {
        return this != HIDDEN;
    }

    // Whether the viewer may change it.
    public boolean editable() {
        return this == EDITABLE;
    }

    // player's access to their own value of setting.
    static Access of(Setting<?> setting, ServerPlayer player) {
        if (!setting.available(player.level().getServer())) {
            return HIDDEN;
        }
        if (CommandPermissions.holds(player, setting.node(), setting.fallbackLevel())
                && setting.forced(player).isEmpty()) {
            return EDITABLE;
        }
        return denied(setting);
    }

    // A source's access: a player's own, anything else by its permission level alone. The
    // command tree tests every requires on a compilation source with no server
    // (Commands.java:203), and a throw there fails the player's join.
    static Access of(Setting<?> setting, CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player != null) {
            return of(setting, player);
        }
        MinecraftServer server = source.getServer();
        if (server != null && !setting.available(server)) {
            return HIDDEN;
        }
        return source.permissions().hasPermission(
                new Permission.HasCommandLevel(setting.fallbackLevel())) ? EDITABLE
                        : denied(setting);
    }

    // The declared denial.
    private static Access denied(Setting<?> setting) {
        return setting.hiddenWhenDeniedResolved() ? HIDDEN : READONLY;
    }
}
