package club.rainbowkitty.kalic0re.common.aim;

import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * What a player's crosshair is on, resolved server-side against their own reach.
 *
 * <p>The server never hears what a client has highlighted, so anything that answers to where a
 * player is pointing has to work this out for itself. Doing it once here keeps the two answers
 * consistent: an entity only counts when it stands in front of whatever block was hit, so one
 * behind a wall is not what the player is pointing at.
 */
public final class Crosshair {

    // Static-only; blocks instantiation.
    private Crosshair() {}

    /**
     * What {@code player} is pointing at: the block their reach clips to, and the nearest entity
     * in front of it that {@code filter} accepts.
     *
     * @param player the player whose crosshair to resolve
     * @param filter which entities are worth hitting, asked before the ray is traced
     */
    public static Hit of(ServerPlayer player, Predicate<Entity> filter) {
        // pick() clips to exactly the reach distance, so no further range filtering is needed.
        double range = player.blockInteractionRange();
        HitResult block = player.pick(range, 1.0F, false);
        Vec3 eye = player.getEyePosition();

        // An entity past the block the ray stopped at is behind it from here.
        double limit = block.getType() == HitResult.Type.MISS
                ? range * range
                : eye.distanceToSqr(block.getLocation());

        Vec3 look = player.getViewVector(1.0F).scale(range);
        AABB search = player.getBoundingBox().expandTowards(look).inflate(1.0);

        return new Hit(block, ProjectileUtil.getEntityHitResult(
                player, eye, eye.add(look), search, filter, limit));
    }

    /**
     * The nearest entity of {@code type} under {@code player}'s crosshair, or null where they are
     * pointing at anything else — a block standing in front of one counts as anything else.
     *
     * @param player the player whose crosshair to resolve
     * @param type the only kind of entity worth hitting
     */
    public static <T extends Entity> @Nullable T entity(ServerPlayer player, Class<T> type) {
        EntityHitResult hit = of(player, type::isInstance).entity();

        return hit == null ? null : type.cast(hit.getEntity());
    }

    /**
     * Whether {@code player} is pointing at nothing at all: no block inside their reach, and no
     * entity in front of where that reach runs out.
     *
     * <p>What "use the item on empty air" means, which the game itself never asks on the server.
     */
    public static boolean onNothing(ServerPlayer player) {
        Hit hit = of(player, entity -> true);

        return hit.entity() == null && hit.block().getType() == HitResult.Type.MISS;
    }

    /**
     * One resolved crosshair.
     *
     * @param block the block hit, clipped to the player's reach; a miss rather than null where
     *     they are pointing at open air
     * @param entity the nearest accepted entity in front of that block, or null for none
     */
    public record Hit(HitResult block, @Nullable EntityHitResult entity) {}
}
