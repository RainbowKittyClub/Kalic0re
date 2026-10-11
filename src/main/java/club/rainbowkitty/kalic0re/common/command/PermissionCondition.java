package club.rainbowkitty.kalic0re.common.command;

import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * A datapack predicate condition that passes when the context's entity is a player who holds a
 * permission node, so a function or command block can ask {@code execute as <player> if predicate
 * <id>} or select with {@code @a[predicate=<id>]}. The JSON is
 * {@code {"condition": "<id>", "node": "<node>", "fallback": "<level>"}}; {@code fallback}
 * defaults to {@code gamemasters} and answers when no permission provider has an opinion, as
 * {@link CommandPermissions#holds} does.
 *
 * <p>Fails closed: no entity in the context (a console or a bare command block), or an entity
 * that is not a player, is a fail. A command block checks a player by running {@code as} them.
 *
 * @param node the full permission node, taken as given
 * @param fallback the operator level that decides when the node is undefined
 */
public record PermissionCondition(String node, PermissionLevel fallback)
        implements LootItemCondition {
    /** The condition's fields: {@code node} required, {@code fallback} defaulting to level 2. */
    public static final MapCodec<PermissionCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Codec.STRING.fieldOf("node").forGetter(PermissionCondition::node),
                    PermissionLevel.CODEC.optionalFieldOf("fallback", PermissionLevel.GAMEMASTERS)
                            .forGetter(PermissionCondition::fallback))
                    .apply(i, PermissionCondition::new));

    /**
     * Registers the condition type under {@code id}, the string datapacks name in "condition". An
     * id in the default namespace lets them write the bare name, at the price of failing boot the
     * day vanilla ships a condition called the same, which is a loud enough tripwire to rename on.
     */
    public static void register(Identifier id) {
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, id, MAP_CODEC);
    }

    @Override
    public MapCodec<PermissionCondition> codec() {
        return MAP_CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public boolean test(LootContext context) {
        return context.getOptionalParameter(LootContextParams.THIS_ENTITY)
                instanceof ServerPlayer player && CommandPermissions.holds(player, node, fallback);
    }
}
