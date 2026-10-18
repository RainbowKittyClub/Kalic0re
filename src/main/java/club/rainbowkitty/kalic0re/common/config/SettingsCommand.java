package club.rainbowkitty.kalic0re.common.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import org.jspecify.annotations.Nullable;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

// /settings <mod> get|set|reset for one mod's player settings while the hub is absent, with the
// hub's grammar, nodes, page rules, replies and translation keys. Keys and pages are word
// arguments with suggestions filtered by access: a key literal hidden by requires would shadow
// any argument sibling (Brigadier tries arguments only when no literal matches), so it could
// not answer as unknown. Each mod is its own literal, so several mods' trees merge cleanly.
final class SettingsCommand {
    private static final String KEYS = "commands.rkcorecommon.settings.";

    private SettingsCommand() {
    }

    // Adds config's subtree under the shared settings literal, which carries no requires so
    // every mod's subtree merges into it.
    static void register(CommandDispatcher<CommandSourceStack> dispatcher, ModConfig config) {
        dispatcher.register(Commands.literal("settings").then(Commands.literal(config.modId())
                .requires(source -> any(config, source, Access::visible))
                .then(Commands.literal("get")
                        .requires(source -> any(config, source, Access::visible))
                        .then(Commands.argument("key", StringArgumentType.word())
                                .suggests(keys(config, Access::visible))
                                .executes(context -> get(context, config))))
                // Gated on visibility, not editability as the hub's is, so a read-only or forced
                // key answers its refusal line instead of a parse error when nothing is editable.
                .then(Commands.literal("set")
                        .requires(source -> any(config, source, Access::visible))
                        .then(Commands.argument("key", StringArgumentType.word())
                                .suggests(keys(config, Access::editable))
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .suggests(values(config))
                                        .executes(context -> set(context, config)))))
                .then(Commands.literal("reset")
                        .requires(source -> any(config, source, Access::visible))
                        .then(Commands.argument("page", StringArgumentType.word())
                                .suggests(pages(config))
                                .executes(context -> reset(context, config))))));
    }

    // get: the effective value, marked read-only or forced; the result is the value as a number.
    private static int get(CommandContext<CommandSourceStack> context, ModConfig config) {
        CommandSourceStack source = context.getSource();
        String key = StringArgumentType.getString(context, "key");
        Setting<?> setting = playerSetting(config, key);
        ServerPlayer player = source.getPlayer();
        if (setting != null && player == null) {
            return fail(source, "needs_player", key);
        }
        if (setting == null || !Access.of(setting, player).visible()) {
            return fail(source, "unknown_setting", key);
        }
        return reply(source, player, setting);
    }

    // The get reply for one setting.
    private static <T> int reply(CommandSourceStack source, ServerPlayer player,
            Setting<T> setting) {
        boolean forced = setting.forced(player).isPresent();
        T value = setting.get(player);
        String line = forced ? "get.forced" : Access.of(setting, player).editable() ? "get"
                : "get.read_only";
        Component message = text(line, setting.key(), setting.kind().show(value));
        source.sendSuccess(() -> message, false);
        return result(value);
    }

    // set: a one-entry page write. Checked in the hub's order: visible, of the kind, not forced, a
    // change at all, then permitted, so setting a value to what it already is needs no permission.
    private static int set(CommandContext<CommandSourceStack> context, ModConfig config) {
        CommandSourceStack source = context.getSource();
        String key = StringArgumentType.getString(context, "key");
        Setting<?> setting = playerSetting(config, key);
        ServerPlayer player = source.getPlayer();
        if (setting != null && player == null) {
            return fail(source, "needs_player", key);
        }
        if (setting == null || !Access.of(setting, player).visible()) {
            return fail(source, "unknown_setting", key);
        }
        return set(source, player, config, setting, StringArgumentType.getString(context,
                "value"));
    }

    // Parses text by setting's kind and writes setting's page with it in place.
    private static <T> int set(CommandSourceStack source, ServerPlayer player, ModConfig config,
            Setting<T> setting, String text) {
        String unquoted = unquote(text);
        DataResult<T> parsed = setting.kind().parse(unquoted);
        if (parsed.error().isPresent()) {
            source.sendFailure(invalid(setting, text, unquoted));
            return 0;
        }
        T value = parsed.getOrThrow();
        // Before the change check, which compares with the own value the force hides.
        if (setting.forced(player).isPresent()) {
            return fail(source, "forced", setting.key());
        }
        if (value.equals(setting.own(player))) {
            Component message = text("unchanged", setting.key(), setting.kind().show(value));
            source.sendSuccess(() -> message, false);
            return 0;
        }
        if (!Access.of(setting, player).editable()) {
            return fail(source, "read_only", setting.key());
        }
        List<Setting<?>> page = config.pages().get(setting.page());
        List<Object> before = effective(page, player);
        Map<String, Dynamic<?>> entries = new LinkedHashMap<>();
        for (Setting<?> other : page) {
            entries.put(other.key(), other == setting ? PlayerValues.encode(setting, value)
                    : ownEntry(other, player));
        }
        config.playerValues().put(player, entries);
        notifyChanges(player, page, before);
        Component message = text("set", setting.key(), setting.kind().show(value));
        source.sendSuccess(() -> message, false);
        return 1;
    }

    // reset: drops the page's entries the player may edit, as the hub deletes only those rows.
    // An entry they cannot change stays, so it is never named and the page stays touched.
    private static int reset(CommandContext<CommandSourceStack> context, ModConfig config) {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "page");
        List<Setting<?>> page = config.pages().get(name);
        if (page == null || page.getFirst().scope() != Scope.PLAYER) {
            return fail(source, "unknown_page", name);
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            return fail(source, "page_needs_player", name);
        }
        if (page.stream().noneMatch(setting -> Access.of(setting, player).visible())) {
            return fail(source, "unknown_page", name);
        }
        List<String> keys = page.stream()
                .filter(setting -> Access.of(setting, player).editable())
                .map(Setting::key).toList();
        if (!config.playerValues().hasAny(player, keys)) {
            Component message = text("reset_unchanged", name, config.modId());
            source.sendSuccess(() -> message, false);
            return 0;
        }
        List<Object> before = effective(page, player);
        config.playerValues().remove(player, keys);
        notifyChanges(player, page, before);
        Component message = text("reset", name, config.modId());
        source.sendSuccess(() -> message, false);
        return 1;
    }

    // The player setting under key, visible or not; null for a missing or server key.
    private static @Nullable Setting<?> playerSetting(ModConfig config, String key) {
        Setting<?> setting = config.setting(key);
        return setting != null && setting.scope() == Scope.PLAYER ? setting : null;
    }

    // Sends one failure line; the command result is 0.
    private static int fail(CommandSourceStack source, String line, Object... args) {
        source.sendFailure(text(line, args));
        return 0;
    }

    // A reply under commands.rkcorecommon.settings.*, the hub's keys, with its English as the
    // fallback, since the shaded library ships no lang file of its own.
    private static Component text(String line, Object... args) {
        String english = switch (line) {
            case "get" -> "%s is %s";
            case "get.read_only" -> "%s is %s (read-only)";
            case "get.forced" -> "%s is %s (set by the server)";
            case "set" -> "Set %s to %s";
            case "unchanged" -> "%s is already %s";
            case "reset" -> "Reset page %s of %s";
            case "reset_unchanged" -> "Page %s of %s has nothing to reset";
            case "unknown_setting" -> "Unknown setting: %s";
            case "unknown_page" -> "Unknown page: %s";
            case "needs_player" -> "%s is a player setting; only a player has one";
            case "page_needs_player" -> "Page %s holds player settings; only a player has those";
            case "invalid.toggle" -> "%s takes true or false, not %s";
            case "invalid.range" -> "%s takes a whole number from %s to %s, not %s";
            case "invalid.choice" -> "%s takes one of %s, not %s";
            case "invalid.text" -> "%s takes at most %s characters";
            case "read_only" -> "You cannot change %s";
            case "forced" -> "%s is set by the server";
            default -> throw new IllegalArgumentException("No reply " + line);
        };
        return Component.translatableWithFallback(KEYS + line, english, args);
    }

    // The kind's own wording of what it takes. given is the typed text, except that a choice
    // shows it lower-cased and an integer out of range shows it as parsed, as the hub does.
    private static <T> Component invalid(Setting<T> setting, String text, String unquoted) {
        String key = setting.key();
        return switch (setting.kind()) {
            case SettingKind.Toggle toggle -> text("invalid.toggle", key, text);
            case SettingKind.Range range -> text("invalid.range", key, range.min(), range.max(),
                    asInteger(unquoted, text));
            case SettingKind.Choice<?> choice -> text("invalid.choice", key,
                    String.join(", ", choice.ids()), unquoted.toLowerCase(Locale.ROOT));
            case SettingKind.Text textKind -> text("invalid.text", key, textKind.maxLength());
        };
    }

    // unquoted as a parsed integer's digits, or text when it is not one.
    private static String asInteger(String unquoted, String text) {
        try {
            return Integer.toString(Integer.parseInt(unquoted));
        } catch (NumberFormatException e) {
            return text;
        }
    }

    // A whole value in double quotes, unescaped as Brigadier reads a quoted string; how an
    // empty or space-edged text is typed.
    private static String unquote(String text) {
        if (text.length() < 2 || text.charAt(0) != '"' || text.charAt(text.length() - 1) != '"') {
            return text;
        }
        StringReader reader = new StringReader(text);
        try {
            String unquoted = reader.readQuotedString();
            return reader.canRead() ? text : unquoted;
        } catch (CommandSyntaxException e) {
            return text;
        }
    }

    // The command result of a value: 1 or 0, the integer, the option's position, or the length.
    private static int result(Object value) {
        return switch (value) {
            case Boolean toggle -> toggle ? 1 : 0;
            case Integer number -> number;
            case Enum<?> option -> option.ordinal();
            case String string -> string.length();
            default -> 0;
        };
    }

    // Whether any player setting of config passes test for source.
    private static boolean any(ModConfig config, CommandSourceStack source,
            Predicate<Access> test) {
        return config.settings().stream().anyMatch(setting -> setting.scope() == Scope.PLAYER
                && test.test(Access.of(setting, source)));
    }

    // Suggests the player-setting keys whose access passes test.
    private static SuggestionProvider<CommandSourceStack> keys(ModConfig config,
            Predicate<Access> test) {
        return (context, builder) -> SharedSuggestionProvider.suggest(config.settings().stream()
                .filter(setting -> setting.scope() == Scope.PLAYER
                        && test.test(Access.of(setting, context.getSource())))
                .map(Setting::key), builder);
    }

    // Suggests what the typed key takes: true and false, its bounds, its option ids, or "". Only
    // for a key the source may change, so a hidden one answers as an unknown one does.
    private static SuggestionProvider<CommandSourceStack> values(ModConfig config) {
        return (context, builder) -> {
            Setting<?> setting = playerSetting(config,
                    StringArgumentType.getString(context, "key"));
            List<String> values = setting == null
                    || !Access.of(setting, context.getSource()).editable()
                    ? List.of() : setting.kind().suggestions();
            return SharedSuggestionProvider.suggest(values, builder);
        };
    }

    // Suggests the player pages with anything visible.
    private static SuggestionProvider<CommandSourceStack> pages(ModConfig config) {
        return (context, builder) -> {
            List<String> visible = new ArrayList<>();
            config.pages().forEach((name, page) -> {
                if (page.stream().anyMatch(setting -> setting.scope() == Scope.PLAYER
                        && Access.of(setting, context.getSource()).visible())) {
                    visible.add(name);
                }
            });
            return SharedSuggestionProvider.suggest(visible, builder);
        };
    }

    // A setting's stored-or-default value as its page write stores it; never the forced one.
    private static <T> Dynamic<?> ownEntry(Setting<T> setting, ServerPlayer player) {
        return PlayerValues.encode(setting, setting.own(player));
    }

    // Each setting's effective value, in page order.
    private static List<Object> effective(List<Setting<?>> page, ServerPlayer player) {
        return page.stream().map(setting -> (Object) setting.get(player)).toList();
    }

    // Fires onChange for each setting whose effective value moved from before.
    private static void notifyChanges(ServerPlayer player, List<Setting<?>> page,
            List<Object> before) {
        MinecraftServer server = player.level().getServer();
        for (int i = 0; i < page.size(); i++) {
            notifyIfChanged(server, player, page.get(i), before.get(i));
        }
    }

    // Fires setting's listeners when its effective value differs from was.
    private static <T> void notifyIfChanged(MinecraftServer server, ServerPlayer player,
            Setting<T> setting, Object was) {
        T now = setting.get(player);
        if (!now.equals(was)) {
            setting.fire(server, player, now);
        }
    }
}
