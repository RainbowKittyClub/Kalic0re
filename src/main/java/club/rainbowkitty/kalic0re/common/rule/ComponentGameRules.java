package club.rainbowkitty.kalic0re.common.rule;

import java.util.function.ToIntFunction;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRules;

/**
 * Registers game rules under a namespace scoped to one feature rather than the whole mod, so
 * {@code /gamerule} groups rules by the component they tune (e.g. {@code mymod.armorstands:...})
 * instead of dumping every feature's rules under one flat {@code mymod:} prefix.
 *
 * <p>Worth it only for a mod with several unrelated feature areas; one with a handful of rules is
 * better served by the flat namespace its lang file and command completion already imply.
 */
public final class ComponentGameRules {
    private ComponentGameRules() {
    }

    /** Registers a boolean rule as {@code <modId>.<component>:<path>}. */
    public static GameRule<Boolean> registerBoolean(String modId, String component, String path,
            boolean defaultValue) {
        return register(modId, component, path, GameRuleType.BOOL, BoolArgumentType.bool(),
                (visitor, rule) -> visitor.visitBoolean(rule), Codec.BOOL,
                value -> value ? 1 : 0, defaultValue);
    }

    private static <T> GameRule<T> register(String modId, String component, String path,
            GameRuleType type, ArgumentType<T> argument, GameRules.VisitorCaller<T> visitorCaller,
            Codec<T> valueCodec, ToIntFunction<T> commandResultFunction, T defaultValue) {
        GameRule<T> rule = new GameRule<>(GameRuleCategory.MISC, type, argument, visitorCaller,
                valueCodec, commandResultFunction, defaultValue, FeatureFlags.VANILLA_SET);
        return Registry.register(BuiltInRegistries.GAME_RULE,
                Identifier.fromNamespaceAndPath(modId + "." + component, path), rule);
    }
}
