package club.rainbowkitty.kalic0re.common.rule;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;

/**
 * Game rules that stand in for server settings where the rkcore-common settings hub is absent.
 *
 * <p>Each helper registers its rule only when {@code rkcorecommon} is not loaded, and returns it,
 * or null when the hub owns the value instead. Loader settles the mod list before any
 * initializer runs, so the answer is the same however mods load. Call them from an initializer,
 * before the game-rule registry freezes.
 */
public final class FallbackRules {
    /** The mod id of the settings hub whose presence suppresses every fallback rule. */
    public static final String HUB_MOD_ID = "rkcorecommon";

    private static final boolean HUB_LOADED = FabricLoader.getInstance().isModLoaded(HUB_MOD_ID);

    private FallbackRules() {
    }

    /** Whether the settings hub's mod is loaded, which is when no fallback rule registers. */
    public static boolean hubLoaded() {
        return HUB_LOADED;
    }

    /** A boolean rule in {@code MISC}, or null with the hub present. */
    public static @Nullable GameRule<Boolean> bool(Identifier id, boolean defaultValue) {
        return bool(id, GameRuleCategory.MISC, defaultValue, FeatureFlagSet.of());
    }

    /** A boolean rule, or null with the hub present. */
    public static @Nullable GameRule<Boolean> bool(Identifier id, GameRuleCategory category,
            boolean defaultValue, FeatureFlagSet requiredFeatures) {
        if (HUB_LOADED) {
            return null;
        }
        return GameRuleBuilder.forBoolean(defaultValue).category(category)
                .requiredFeatures(requiredFeatures).buildAndRegister(id);
    }

    /** An integer rule in {@code [min, max]} and {@code MISC}, or null with the hub present. */
    public static @Nullable GameRule<Integer> integer(Identifier id, int defaultValue, int min,
            int max) {
        return integer(id, GameRuleCategory.MISC, defaultValue, min, max, FeatureFlagSet.of());
    }

    /** An integer rule bounded to {@code [min, max]}, or null with the hub present. */
    public static @Nullable GameRule<Integer> integer(Identifier id, GameRuleCategory category,
            int defaultValue, int min, int max, FeatureFlagSet requiredFeatures) {
        if (HUB_LOADED) {
            return null;
        }
        return GameRuleBuilder.forInteger(defaultValue).range(min, max).category(category)
                .requiredFeatures(requiredFeatures).buildAndRegister(id);
    }

    /**
     * Fabric's enum rule over {@code defaultValue}'s constants in {@code MISC}, or null with the
     * hub present. {@code /gamerule} offers one literal per constant, spelled by
     * {@code toString()}, and the save holds the constant's {@code name()}.
     */
    public static <E extends Enum<E>> @Nullable GameRule<E> choice(Identifier id, E defaultValue) {
        return choice(id, GameRuleCategory.MISC, defaultValue, FeatureFlagSet.of());
    }

    /** Fabric's enum rule over {@code defaultValue}'s constants, or null with the hub present. */
    public static <E extends Enum<E>> @Nullable GameRule<E> choice(Identifier id,
            GameRuleCategory category, E defaultValue, FeatureFlagSet requiredFeatures) {
        if (HUB_LOADED) {
            return null;
        }
        return GameRuleBuilder.forEnum(defaultValue).category(category)
                .requiredFeatures(requiredFeatures).buildAndRegister(id);
    }

    /** A string rule in {@code MISC}, or null with the hub present; see the full overload. */
    public static @Nullable GameRule<String> text(Identifier id, String defaultValue) {
        return text(id, GameRuleCategory.MISC, defaultValue, FeatureFlagSet.of());
    }

    /**
     * A string rule, or null with the hub present. {@code /gamerule} takes it quoted or as one
     * unquoted word, {@code ""} included, and answers with its length. The rule cannot refuse
     * any string, so a length limit is the reader's to apply.
     */
    public static @Nullable GameRule<String> text(Identifier id, GameRuleCategory category,
            String defaultValue, FeatureFlagSet requiredFeatures) {
        if (HUB_LOADED) {
            return null;
        }
        // INT is only a label: vanilla reads the type nowhere but in its own registration and
        // the management API. The visitor caller visits nothing, as Fabric's double rule does
        // for a visitor that is not its own, so the client's rule screen skips it.
        GameRule<String> rule = new GameRule<>(category, GameRuleType.INT,
                StringArgumentType.string(), (visitor, self) -> { }, Codec.STRING,
                String::length, defaultValue, requiredFeatures);
        return Registry.register(BuiltInRegistries.GAME_RULE, id, rule);
    }
}
