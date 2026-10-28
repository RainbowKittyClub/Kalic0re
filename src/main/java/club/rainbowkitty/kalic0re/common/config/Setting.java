package club.rainbowkitty.kalic0re.common.config;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

import club.rainbowkitty.kalic0re.common.rule.FallbackRules;

/**
 * One declared setting of a {@link ModConfig}, and the way its mod reads it.
 *
 * <p>Declare it with {@code player()} or {@code server()} and any of the optional calls before
 * {@link ModConfig#register()}; they refuse once the declaration is closed. Reads answer the
 * effective value whoever can see the setting: the hub's when rkcore-common runs, else the
 * fallback's (the game rule for a server value, the player's attachment for a player value,
 * under any value forced through permission meta), and the default whenever neither has one.
 *
 * @param <T> the value type: {@code Boolean}, {@code Integer}, the choice's enum, or
 *        {@code String}
 */
public final class Setting<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger("kalic0re/config");
    private static final Object UNSET = new Object();

    private final ModConfig config;
    private final String key;
    private final Identifier id;
    private final SettingKind<T> kind;
    private final Codec<T> codec;
    private final T defaultValue;
    private final List<Listener<T>> listeners = new CopyOnWriteArrayList<>();
    private final Set<String> reported = ConcurrentHashMap.newKeySet();
    private @Nullable Scope scope;
    private @Nullable String page;
    private @Nullable String tab;
    private boolean inMenu = true;
    private FeatureFlagSet requiredFeatures = FeatureFlagSet.of();
    private @Nullable PermissionLevel permission;
    private @Nullable Boolean hiddenWhenDenied;
    private @Nullable GameRuleCategory fallbackCategory;
    private @Nullable GameRule<T> rule;
    private @Nullable Validator<T> validator;
    // The hub's answer for a server value, decoded; UNSET until read and after its notice.
    private volatile Object hubCache = UNSET;

    Setting(ModConfig config, String key, SettingKind<T> kind, T defaultValue) {
        this.config = config;
        this.key = key;
        this.id = Identifier.fromNamespaceAndPath(config.modId(), key);
        this.kind = kind;
        this.codec = kind.codec();
        this.defaultValue = defaultValue;
    }

    /** Makes this a value each player holds for themselves. */
    public Setting<T> player() {
        return scope(Scope.PLAYER);
    }

    /** Makes this one value for the whole server. */
    public Setting<T> server() {
        return scope(Scope.SERVER);
    }

    /**
     * Puts this on page {@code name} ({@code [a-z0-9_]+}) instead of its scope's default page.
     * A page holds settings of one scope only.
     */
    public Setting<T> page(String name) {
        config.checkOpen();
        this.page = name;
        return this;
    }

    /**
     * Shows this under the hub's Settings tab {@code key} ({@code [a-z0-9_]+}) instead of its
     * page's tab ({@link ModConfig#pageTab}); mods naming the same key share that tab.
     */
    public Setting<T> tab(String key) {
        config.checkOpen();
        this.tab = key;
        return this;
    }

    /**
     * Keeps this out of the menu's Settings section; {@code /settings} still lists and changes
     * it.
     */
    public Setting<T> hideFromMenus() {
        config.checkOpen();
        this.inMenu = false;
        return this;
    }

    /**
     * Makes this unavailable on a world without every one of {@code features}: it shows nowhere,
     * answers its default, and its fallback rule carries the same requirement.
     */
    public Setting<T> requires(FeatureFlagSet features) {
        config.checkOpen();
        this.requiredFeatures = features;
        return this;
    }

    /**
     * The operator level that edits this when no permission provider defines its node,
     * {@code <mod>.config.<scope>.<key>}. Defaults to {@code ALL} for a player setting and
     * {@code GAMEMASTERS} for a server one.
     */
    public Setting<T> permission(PermissionLevel fallback) {
        config.checkOpen();
        this.permission = fallback;
        return this;
    }

    /** Hides this from whoever may not edit it; the default for a server setting. */
    public Setting<T> hiddenWhenDenied() {
        config.checkOpen();
        this.hiddenWhenDenied = true;
        return this;
    }

    /** Shows this read-only to whoever may not edit it; the default for a player setting. */
    public Setting<T> readOnlyWhenDenied() {
        config.checkOpen();
        this.hiddenWhenDenied = false;
        return this;
    }

    /**
     * Refuses a value as it is set, with the reason {@code validator} gives: through the hub's
     * {@code /settings} and its menu, and the fallback's {@code /settings}. A fallback rule
     * cannot refuse, since vanilla's {@code /gamerule} sets it without asking; a value it refuses
     * there is logged once. Reads never ask it, so a value stored before, or set past it, still
     * reaches the mod.
     */
    public Setting<T> validator(Validator<T> validator) {
        config.checkOpen();
        this.validator = validator;
        return this;
    }

    /** Gives this server setting a game rule in {@code MISC} for servers without the hub. */
    public Setting<T> fallbackRule() {
        return fallbackRule(GameRuleCategory.MISC);
    }

    /**
     * Gives this server setting a game rule {@code <mod>:<key>} in {@code category}, registered
     * only when rkcore-common is absent and read there in place of the hub.
     */
    public Setting<T> fallbackRule(GameRuleCategory category) {
        config.checkOpen();
        this.fallbackCategory = category;
        return this;
    }

    /**
     * The effective value for {@code player}: forced, else stored, else the default. A server
     * setting answers its server value.
     */
    public T get(ServerPlayer player) {
        config.checkRegistered();
        MinecraftServer server = player.level().getServer();
        if (scope == Scope.SERVER) {
            return get(server);
        }
        return switch (ConfigRuntime.mode()) {
            case HUB -> fromHub(ConfigRuntime.hubPlayer(player.getUUID(), id));
            case FALLBACK -> available(server) ? effective(player) : defaultValue;
            case BROKEN -> defaultValue;
        };
    }

    /**
     * The effective server value.
     *
     * @throws IllegalStateException for a player setting, which has no server value
     */
    public T get(MinecraftServer server) {
        config.checkRegistered();
        if (scope != Scope.SERVER) {
            throw new IllegalStateException(id + " is a player setting; read it per player");
        }
        return switch (ConfigRuntime.mode()) {
            case HUB -> hubServerValue();
            case FALLBACK -> fromRule(server);
            case BROKEN -> defaultValue;
        };
    }

    /** The declared default, for code that runs where no server holds a value. */
    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Calls {@code listener} on the server thread after this setting's value changes: a server
     * value through the hub or {@code /gamerule}, a player value through the hub or the
     * fallback's {@code /settings}. A player value notifies only while that player is online,
     * and a change of forced value without the hub notifies nothing.
     */
    public void onChange(Listener<T> listener) {
        listeners.add(listener);
    }

    /** The key, unique in its mod. */
    public String key() {
        return key;
    }

    /** {@code <mod>:<key>}, which is also the fallback rule's id. */
    public Identifier id() {
        return id;
    }

    /**
     * Called after a setting's value changes.
     *
     * @param <T> the setting's value type
     */
    @FunctionalInterface
    public interface Listener<T> {
        /**
         * The change.
         *
         * @param server the server the value changed on
         * @param player the player whose value changed, or null for a server value
         * @param value the new effective value
         */
        public void changed(MinecraftServer server, @Nullable ServerPlayer player, T value);
    }

    /**
     * Checks a value as it is set.
     *
     * @param <T> the setting's value type
     */
    @FunctionalInterface
    public interface Validator<T> {
        /**
         * Why {@code value} is refused, or null when it is taken. Called on the server thread
         * with a value of the setting's kind, within its bounds, and never for a reset to the
         * default. The reason is one line for whoever set it, translatable, its English in the
         * mod's own {@code lang/en_us.json} ({@code FallbackText}); house refusals start "Must".
         */
        public @Nullable Component refusal(T value);
    }

    // The scope, or null before player() or server() was called.
    @Nullable Scope scope() {
        return scope;
    }

    // The page name, the scope's own when none was declared.
    String page() {
        return page != null ? page : scope().serializedName();
    }

    // Whether the menu's Settings section shows this.
    boolean inMenu() {
        return inMenu;
    }

    // The tab this names itself, or null for its page's.
    @Nullable String tab() {
        return tab;
    }

    // Whether page(name) was called.
    boolean pageDeclared() {
        return page != null;
    }

    // How the value is checked and carried.
    SettingKind<T> kind() {
        return kind;
    }

    // The kind's attachment codec, built once.
    Codec<T> codec() {
        return codec;
    }

    // The declared required features, empty for none.
    FeatureFlagSet requiredFeatures() {
        return requiredFeatures;
    }

    // The permission node, <mod>.config.<scope>.<key>.
    String node() {
        return config.modId() + ".config." + scope.serializedName() + "." + key;
    }

    // The declared fallback level, or null for the scope's.
    @Nullable PermissionLevel declaredPermission() {
        return permission;
    }

    // The declared denial, or null for the scope's.
    @Nullable Boolean declaredHidden() {
        return hiddenWhenDenied;
    }

    // The level that stands in for the node when no permission provider defines it.
    PermissionLevel fallbackLevel() {
        return permission != null ? permission : scope.defaultFallback();
    }

    // Whether a denied viewer sees nothing of this rather than a read-only value.
    boolean hiddenWhenDeniedResolved() {
        return hiddenWhenDenied != null ? hiddenWhenDenied : scope.hiddenByDefault();
    }

    // Whether fallbackRule was declared, which also picks the gamerule label key.
    boolean hasFallbackRule() {
        return fallbackCategory != null;
    }

    // Whether the world's features cover the declared requirement.
    boolean available(MinecraftServer server) {
        return requiredFeatures.isSubsetOf(server.getWorldData().enabledFeatures());
    }

    // The player's own value without the hub: stored, else the default. Never the forced one.
    T own(ServerPlayer player) {
        return storedOwn(player).orElse(defaultValue);
    }

    // The player's stored value without the hub, if a valid one is stored.
    Optional<T> storedOwn(ServerPlayer player) {
        return config.playerValues().stored(player, this);
    }

    // The forced value without the hub, if any.
    Optional<T> forced(ServerPlayer player) {
        return Forcing.forced(player, this);
    }

    // Whether validator(...) was called.
    boolean validated() {
        return validator != null;
    }

    // The validator's reason to refuse value, or null when it takes it or none is declared.
    // Throws whatever the validator throws.
    @Nullable Component refusal(T value) {
        return validator == null ? null : validator.refusal(value);
    }

    // Logs a validator that threw on value, once per setting.
    void validatorFailed(T value, Throwable error) {
        if (reported.add("validator")) {
            LOGGER.error("The validator of {} failed on '{}'", id, kind.show(value), error);
        }
    }

    // The validator in the bridge's shape: the value as JSON in, the reason or null out. A value
    // not of the kind is the hub's to refuse, so this takes it.
    Function<JsonElement, @Nullable Component> bridgedValidator() {
        return json -> kind.fromJson(json).result().map(this::refusal).orElse(null);
    }

    // Logs message once per distinct what, as a warning.
    void reportOnce(String what, String message) {
        if (reported.add(what)) {
            LOGGER.warn(message);
        }
    }

    // Registers the fallback rule where declared and the hub is absent, wiring its notices.
    void registerFallbackRule() {
        if (fallbackCategory == null) {
            return;
        }
        rule = FallbackRule.register(this, fallbackCategory, defaultValue);
        if (rule != null) {
            GameRuleEvents.changeCallback(rule).register((value, server) -> {
                T now = checked(value);
                reportRefusedRule(now);
                fire(server, null, now);
            });
        }
    }

    // Drops the hub cache, so the next read asks the hub.
    void clearHubCache() {
        hubCache = UNSET;
    }

    // A hub notice for this setting: player null for the server value.
    void hubNotice(MinecraftServer server, @Nullable UUID player) {
        if (player == null) {
            hubCache = UNSET;
            if (scope == Scope.SERVER) {
                fire(server, null, get(server));
            }
            return;
        }
        ServerPlayer online = server.getPlayerList().getPlayer(player);
        if (online != null && scope == Scope.PLAYER) {
            fire(server, online, get(online));
        }
    }

    // Tells every listener, one failing listener stopping none of the others.
    void fire(MinecraftServer server, @Nullable ServerPlayer player, T value) {
        for (Listener<T> listener : listeners) {
            try {
                listener.changed(server, player, value);
            } catch (RuntimeException e) {
                LOGGER.error("A change listener of {} failed", id, e);
            }
        }
    }

    // Sets the scope once.
    private Setting<T> scope(Scope declared) {
        config.checkOpen();
        if (scope != null && scope != declared) {
            throw new IllegalStateException(id + " is already a " + scope.serializedName()
                    + " setting");
        }
        this.scope = declared;
        return this;
    }

    // Forced, else own.
    private T effective(ServerPlayer player) {
        return forced(player).orElseGet(() -> own(player));
    }

    // The rule's value without the hub; the default with no rule or on a world that lacks it.
    private T fromRule(MinecraftServer server) {
        if (rule == null || !rule.isEnabled(server.getWorldData().enabledFeatures())) {
            return defaultValue;
        }
        return checked(server.getGameRules().get(rule));
    }

    // value when the kind accepts it; else the default, logged once per value. A text rule is
    // where this bites, since /gamerule cannot refuse a long string.
    private T checked(T value) {
        if (kind.accepts(value)) {
            return value;
        }
        kind.check(value).error().ifPresent(error -> reportOnce("rule:" + kind.show(value),
                "Rule " + id + " holds '" + kind.show(value) + "', " + error.message()
                        + "; answering its default"));
        return defaultValue;
    }

    // Logs once per value a rule value the validator refuses: /gamerule has already set it, and
    // the mod reads it as set.
    private void reportRefusedRule(T value) {
        Component reason;
        try {
            reason = refusal(value);
        } catch (RuntimeException | LinkageError e) {
            validatorFailed(value, e);
            return;
        }
        if (reason != null) {
            reportOnce("refused:" + kind.show(value), "Rule " + id + " holds '" + kind.show(value)
                    + "', which " + config.modId() + " refuses: " + reason.getString());
        }
    }

    // The hub's server value, decoded once until its next notice.
    @SuppressWarnings("unchecked")
    private T hubServerValue() {
        Object cached = hubCache;
        if (cached != UNSET) {
            return (T) cached;
        }
        T value = fromHub(ConfigRuntime.hubServer(id));
        hubCache = value;
        return value;
    }

    // A bridge answer decoded; the default, logged once, for null or a value of another kind.
    private T fromHub(@Nullable JsonElement json) {
        if (json == null) {
            reportOnce("hub:unknown", "The settings hub does not know " + id
                    + "; answering its default");
            return defaultValue;
        }
        DataResult<T> decoded = kind.fromJson(json);
        decoded.error().ifPresent(error -> reportOnce("hub:" + json,
                "The settings hub answered " + json + " for " + id + ", " + error.message()
                        + "; answering its default"));
        return decoded.result().orElse(defaultValue);
    }

    // The rule matching a setting's kind, from common/rule.
    private static final class FallbackRule {
        private FallbackRule() {
        }

        // The registered rule, or null with the hub present.
        @SuppressWarnings("unchecked")
        private static <T> @Nullable GameRule<T> register(Setting<T> setting,
                GameRuleCategory category, T defaultValue) {
            Identifier id = setting.id;
            FeatureFlagSet features = setting.requiredFeatures;
            GameRule<?> rule = switch (setting.kind) {
                case SettingKind.Toggle toggle -> FallbackRules.bool(id, category,
                        (Boolean) defaultValue, features);
                case SettingKind.Range range -> FallbackRules.integer(id, category,
                        (Integer) defaultValue, range.min(), range.max(), features);
                case SettingKind.Choice<?> choice -> choice(id, category,
                        (Enum<?>) defaultValue, features);
                case SettingKind.Text text -> FallbackRules.text(id, category,
                        (String) defaultValue, features);
            };
            return (GameRule<T>) rule;
        }

        // The enum rule, with the constant's type recovered from the constant.
        @SuppressWarnings("unchecked")
        private static <E extends Enum<E>> @Nullable GameRule<E> choice(Identifier id,
                GameRuleCategory category, Enum<?> defaultValue, FeatureFlagSet features) {
            return FallbackRules.choice(id, category, (E) defaultValue, features);
        }
    }
}
