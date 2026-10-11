package club.rainbowkitty.kalic0re.common.command;

import java.util.function.Predicate;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionLevel;

/**
 * Gates one mod's commands on permission nodes, with a vanilla operator level as fallback.
 *
 * <p>Every node is {@code <modId>.command.<path>}, the path being the command's literals from the
 * root down joined by dots ({@code rkcore.command.spawn.set}), so a permission plugin grants per
 * subcommand with one spelling across every mod. The node is asked through fabric-permissions-api
 * when some mod provides it at runtime (LuckPerms nests it); a defined answer wins, an undefined
 * one falls through to the level check. Without the API the gate is the level check alone. The
 * API is a compile-time dependency only, named by nothing but {@link ApiGate}.
 *
 * <p>{@link #holds} asks the same question of one player for any node, which is what
 * {@link PermissionCondition} uses to answer a datapack predicate.
 */
public final class CommandPermissions {
    private static final String API_MOD_ID = "fabric-permissions-api-v0";
    private static final boolean API_LOADED = FabricLoader.getInstance().isModLoaded(API_MOD_ID);

    private final String modId;

    private CommandPermissions(String modId) {
        this.modId = modId;
    }

    /** The gate for {@code modId}'s commands; hold one per mod. */
    public static CommandPermissions forMod(String modId) {
        return new CommandPermissions(modId);
    }

    /** The permission node for a command path: {@code <modId>.command.<path>}. */
    public String node(String commandPath) {
        return modId + ".command." + commandPath;
    }

    /**
     * A {@code requires} predicate: passes when the source holds {@link #node} of
     * {@code commandPath}, or, when no permission provider answers, when it has {@code fallback}
     * operator level.
     */
    public Predicate<CommandSourceStack> require(String commandPath, PermissionLevel fallback) {
        if (API_LOADED) {
            return ApiGate.require(node(commandPath), fallback);
        }
        return levelCheck(fallback);
    }

    /** The same answer as {@link #require}, asked of one source inside an executor. */
    public boolean check(CommandSourceStack source, String commandPath, PermissionLevel fallback) {
        if (API_LOADED) {
            return ApiGate.check(source, node(commandPath), fallback);
        }
        return levelCheck(fallback).test(source);
    }

    /**
     * Whether {@code player} holds the full node {@code node}, or, when no permission provider
     * answers, has {@code fallback} operator level. The node is taken as given, for checks that
     * do not follow the {@code <modId>.command.<path>} shape.
     */
    public static boolean holds(ServerPlayer player, String node, PermissionLevel fallback) {
        if (API_LOADED) {
            return ApiGate.check(player, node, fallback);
        }
        return player.permissions().hasPermission(new Permission.HasCommandLevel(fallback));
    }

    // Vanilla's own "has command level" check, the one Commands.LEVEL_GAMEMASTERS and kin wrap.
    private static Predicate<CommandSourceStack> levelCheck(PermissionLevel level) {
        return Commands.<CommandSourceStack>hasPermission(
                new PermissionCheck.Require(new Permission.HasCommandLevel(level)));
    }

    // The only code naming fabric-permissions-api, so the JVM resolves it only once the API is
    // known present; Permissions itself does the node-then-level fallback.
    private static final class ApiGate {
        private ApiGate() {
        }

        // Permissions.require: the node's value when defined, else the vanilla level check.
        private static Predicate<CommandSourceStack> require(String node,
                PermissionLevel fallback) {
            return Permissions.require(node, fallback);
        }

        // Permissions.check, with the same fallback.
        private static boolean check(CommandSourceStack source, String node,
                PermissionLevel fallback) {
            return Permissions.check(source, node, fallback);
        }

        // The entity overload of the same check.
        private static boolean check(ServerPlayer player, String node, PermissionLevel fallback) {
            return Permissions.check(player, node, fallback);
        }
    }
}
