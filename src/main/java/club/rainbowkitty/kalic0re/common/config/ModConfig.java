package club.rainbowkitty.kalic0re.common.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ObjectShare;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import club.rainbowkitty.kalic0re.common.rule.FallbackRules;
import club.rainbowkitty.kalic0re.common.text.FallbackText;

/**
 * One mod's settings, declared into the rkcore-common settings hub when it runs and kept by this
 * mod itself when it does not.
 *
 * <p>Create one in the mod's initializer, declare each setting with {@link #toggle},
 * {@link #range}, {@link #choice} or {@link #text} and its scope, then call {@link #register()}
 * before the initializer returns. Labels come from the mod's own {@code lang/en_us.json}:
 * {@code gamerule.<mod>.<key>} for a setting with a fallback rule, {@code config.<mod>.<key>}
 * otherwise, each with an optional {@code .description}; a choice's options are
 * {@code config.<mod>.<key>.<option>} and a page's title {@code config.<mod>.page.<page>}. A
 * Settings tab's title {@code config.<mod>.tab.<tab>} is optional: the hub titles the tabs it
 * defines itself.
 *
 * <p>Without the hub, server values live in their fallback rules and player values in a
 * persistent player attachment {@code <mod>:settings}, edited through
 * {@code /settings <mod> get|set|reset}.
 */
public final class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("kalic0re/config");
    private static final Pattern KEY = Pattern.compile("[a-z0-9_.]+");
    private static final Pattern PAGE = Pattern.compile("[a-z0-9_]+");
    private static final Pattern CONSTANT = Pattern.compile("[A-Z0-9_]+");
    private static final int MAX_TEXT_LENGTH = 256;

    private final String modId;
    private final Map<String, Setting<?>> settings = new LinkedHashMap<>();
    private final Map<String, String> pageTabs = new HashMap<>();
    private boolean registered;
    private @Nullable PlayerValues playerValues;
    private boolean listening;
    private @Nullable MinecraftServer server;

    private ModConfig(String modId) {
        this.modId = modId;
    }

    /** A fresh, open declaration for {@code modId}, which must be a loaded mod's id. */
    public static ModConfig create(String modId) {
        return new ModConfig(modId);
    }

    /** Declares a true/false setting. */
    public Setting<Boolean> toggle(String key, boolean defaultValue) {
        return add(new Setting<>(this, key, new SettingKind.Toggle(), defaultValue));
    }

    /** Declares a whole-number setting in {@code [min, max]}. */
    public Setting<Integer> range(String key, int defaultValue, int min, int max) {
        if (min > max || defaultValue < min || defaultValue > max) {
            throw new IllegalArgumentException(modId + ":" + key + " needs min <= default <= max,"
                    + " got " + min + ", " + defaultValue + ", " + max);
        }
        return add(new Setting<>(this, key, new SettingKind.Range(min, max), defaultValue));
    }

    /**
     * Declares a choice among {@code defaultValue}'s enum constants. Each constant is an option
     * whose id is its name in lower case, so constants must match {@code [A-Z0-9_]+}.
     */
    public <E extends Enum<E>> Setting<E> choice(String key, E defaultValue) {
        Class<E> type = defaultValue.getDeclaringClass();
        Arrays.stream(type.getEnumConstants())
                .filter(constant -> !CONSTANT.matcher(constant.name()).matches())
                .findFirst().ifPresent(constant -> {
                    throw new IllegalArgumentException(modId + ":" + key + " option "
                            + constant.name() + " is not [A-Z0-9_]+");
                });
        return add(new Setting<>(this, key, new SettingKind.Choice<>(type), defaultValue));
    }

    /** Declares a text setting of at most {@code maxLength} characters, 1 to 256. */
    public Setting<String> text(String key, String defaultValue, int maxLength) {
        if (maxLength < 1 || maxLength > MAX_TEXT_LENGTH || defaultValue.length() > maxLength) {
            throw new IllegalArgumentException(modId + ":" + key + " needs maxLength in 1.."
                    + MAX_TEXT_LENGTH + " and a default within it, got " + maxLength);
        }
        return add(new Setting<>(this, key, new SettingKind.Text(maxLength), defaultValue));
    }

    /**
     * Shows page {@code page}'s settings under the hub's Settings tab {@code tab}
     * ({@code [a-z0-9_]+}), but for any that names its own ({@link Setting#tab}). Mods naming the
     * same tab share it; settings that name none sit in the hub's Other tab.
     */
    public ModConfig pageTab(String page, String tab) {
        checkOpen();
        pageTabs.put(page, tab);
        return this;
    }

    /**
     * Closes the declaration: registers the fallback rules (only without rkcore-common), the
     * player attachment, and the fallback {@code /settings}, and hands the declaration to the hub
     * whenever it publishes, with each setting's validator, so mod load order does not matter.
     *
     * @throws IllegalStateException when a setting lacks a scope, mixes scopes on a page, or
     *         lacks a label in the mod's lang file
     */
    public void register() {
        checkOpen();
        validate();
        JsonObject declaration = Declaration.build(this, FallbackText.load(modId));
        registered = true;
        settings.values().forEach(Setting::registerFallbackRule);
        if (settings.values().stream().anyMatch(setting -> setting.scope() == Scope.PLAYER)) {
            playerValues = new PlayerValues(modId);
            CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> {
                if (ConfigRuntime.mode() == ConfigRuntime.Mode.FALLBACK) {
                    SettingsCommand.register(dispatcher, this);
                }
            });
        }
        ConfigRuntime.add(this);
        if (FallbackRules.hubLoaded()) {
            ObjectShare share = FabricLoader.getInstance().getObjectShare();
            share.whenAvailable(ConfigRuntime.DECLARE_KEY,
                    (key, declare) -> declareTo(declare, declaration));
            settings.values().stream().filter(Setting::validated).forEach(setting ->
                    share.whenAvailable(ConfigRuntime.VALIDATE_KEY,
                            (key, validate) -> validatorTo(validate, setting)));
        }
    }

    /** The mod these settings belong to. */
    public String modId() {
        return modId;
    }

    // Every setting, in declaration order.
    Collection<Setting<?>> settings() {
        return settings.values();
    }

    // The setting under key, or null.
    @Nullable Setting<?> setting(String key) {
        return settings.get(key);
    }

    // The tab setting shows under: its own, else its page's, else null for the hub's Other.
    @Nullable String tabOf(Setting<?> setting) {
        return setting.tab() != null ? setting.tab() : pageTabs.get(setting.page());
    }

    // The pages in declaration order, each with its settings.
    Map<String, List<Setting<?>>> pages() {
        Map<String, List<Setting<?>>> pages = new LinkedHashMap<>();
        for (Setting<?> setting : settings.values()) {
            pages.computeIfAbsent(setting.page(), page -> new ArrayList<>()).add(setting);
        }
        return pages;
    }

    // The attachment store; present when any player setting was declared.
    PlayerValues playerValues() {
        if (playerValues == null) {
            throw new IllegalStateException(modId + " declared no player settings");
        }
        return playerValues;
    }

    // Refuses a declaration call after register().
    void checkOpen() {
        if (registered) {
            throw new IllegalStateException(modId + "'s settings are already registered");
        }
    }

    // Refuses a read before register().
    void checkRegistered() {
        if (!registered) {
            throw new IllegalStateException(modId + "'s settings are not registered yet");
        }
    }

    // Holds the server, drops any hub answer cached before the hub loaded its values (a read from
    // another SERVER_STARTING listener), and subscribes to the hub's notices once per JVM.
    void serverStarting(MinecraftServer starting, ConfigRuntime.Mode mode) {
        this.server = starting;
        settings.values().forEach(Setting::clearHubCache);
        if (mode == ConfigRuntime.Mode.HUB && !listening) {
            listening = true;
            ConfigRuntime.listen(modId, this::notice);
        }
    }

    // Forgets the server and every cached hub answer.
    void serverStopped() {
        this.server = null;
        settings.values().forEach(Setting::clearHubCache);
    }

    // Adds a setting under a fresh, well-formed key.
    private <T> Setting<T> add(Setting<T> setting) {
        checkOpen();
        if (!KEY.matcher(setting.key()).matches()) {
            throw new IllegalArgumentException(setting.id() + ": a key is [a-z0-9_.]+");
        }
        if (settings.putIfAbsent(setting.key(), setting) != null) {
            throw new IllegalArgumentException(setting.id() + " is declared twice");
        }
        return setting;
    }

    // Every setting has a scope, a well-formed page of that scope only, a well-formed tab, and a
    // rule only if it is a server setting; every page given a tab holds a setting.
    private void validate() {
        Map<String, Scope> pageScopes = new HashMap<>();
        for (Setting<?> setting : settings.values()) {
            Scope scope = setting.scope();
            if (scope == null) {
                throw new IllegalStateException(setting.id() + " needs player() or server()");
            }
            if (setting.hasFallbackRule() && scope != Scope.SERVER) {
                throw new IllegalStateException(setting.id() + " is a player setting;"
                        + " only a server setting takes a fallback rule");
            }
            String page = setting.page();
            if (!PAGE.matcher(page).matches()) {
                throw new IllegalStateException(setting.id() + ": a page is [a-z0-9_]+");
            }
            Scope held = pageScopes.putIfAbsent(page, scope);
            if (held != null && held != scope) {
                throw new IllegalStateException(modId + " page " + page + " mixes player and"
                        + " server settings");
            }
            if (setting.tab() != null && !PAGE.matcher(setting.tab()).matches()) {
                throw new IllegalStateException(setting.id() + ": a tab is [a-z0-9_]+");
            }
        }
        pageTabs.forEach((page, tab) -> {
            if (!pageScopes.containsKey(page)) {
                throw new IllegalStateException(modId + " gives page " + page + " a tab, but no"
                        + " setting is on it");
            }
            if (!PAGE.matcher(tab).matches()) {
                throw new IllegalStateException(modId + " page " + page + ": a tab is"
                        + " [a-z0-9_]+");
            }
        });
    }

    // Hands the declaration to the hub's declare function; a refusal is the hub's to log, but a
    // throw is logged here so it never stops another mod's handover.
    @SuppressWarnings("unchecked")
    private void declareTo(Object declare, JsonObject declaration) {
        try {
            ((Consumer<JsonObject>) declare).accept(declaration.deepCopy());
        } catch (RuntimeException e) {
            LOGGER.error("The settings hub refused {}'s declaration", modId, e);
        }
    }

    // Hands setting's validator to the hub's validate function, logging a throw as declareTo does.
    @SuppressWarnings("unchecked")
    private void validatorTo(Object validate, Setting<?> setting) {
        try {
            ((BiConsumer<Identifier, Function<JsonElement, @Nullable Component>>) validate)
                    .accept(setting.id(), setting.bridgedValidator());
        } catch (RuntimeException e) {
            LOGGER.error("The settings hub refused the validator of {}", setting.id(), e);
        }
    }

    // A hub notice for this mod, on the server thread.
    private void notice(@Nullable UUID player, Identifier id) {
        Setting<?> setting = id.getNamespace().equals(modId) ? settings.get(id.getPath()) : null;
        MinecraftServer current = server;
        if (setting != null && current != null) {
            setting.hubNotice(current, player);
        }
    }
}
