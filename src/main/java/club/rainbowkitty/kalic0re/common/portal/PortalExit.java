package club.rainbowkitty.kalic0re.common.portal;

import java.util.UUID;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

/**
 * Where an entity comes out: the same place in the far frame's box it had in the near one's, or on
 * a shaped frame the nearest spot there it fits, with its velocity and facing kept and turned a
 * quarter between the two upright planes, as a nether portal turns them. The arrival stands
 * inside the far portal, which vanilla's portal cooldown
 * keeps from sending it straight back; riders get that cooldown here, since vanilla gives it only
 * to the entity that crossed.
 */
public final class PortalExit {
    private static final float QUARTER_TURN = 90.0F;

    private PortalExit() {
    }

    /**
     * The transition carrying {@code entity} from {@code from} into {@code to}, which stands in
     * {@code toLevel}, with {@code after} run once it has arrived. Riders come along on copies
     * of the transition; {@code after} runs once, for {@code entity} alone. No sound plays but
     * what {@code after} plays.
     */
    public static TeleportTransition transition(Entity entity, PortalFrame from,
            ServerLevel toLevel, PortalFrame to, TeleportTransition.PostTeleportTransition after) {
        UUID crossing = entity.getUUID();
        EntityDimensions size = entity.getDimensions(entity.getPose());
        Vec3 pos = entity.position();
        PortalPlane near = from.plane();
        PortalPlane far = to.plane();
        double along = fraction(pos, from, near.along(), from.width(), size);
        double up = fraction(pos, from, near.up(), from.height(), size);
        // The offset across the plane only carries between planes of a kind: feet across a flat
        // one and a centre across an upright one are different measures.
        double across = near.isFlat() == far.isFlat()
                ? pos.get(near.normal()) - (from.origin().get(near.normal()) + 0.5) : 0.0;
        double extentAlong = extent(far.along(), size);
        double extentUp = extent(far.up(), size);
        double[] low = settle(to, (to.width() - extentAlong) * along,
                (to.height() - extentUp) * up, extentAlong, extentUp);
        Vec3 target = Vec3.ZERO
                .with(far.along(), place(to, far.along(), low[0], size))
                .with(far.up(), place(to, far.up(), low[1], size))
                .with(far.normal(), to.origin().get(far.normal()) + 0.5 + across);
        Vec3 free = PortalShape.findCollisionFreePosition(target, toLevel, entity, size);
        float turn = near != far && !near.isFlat() && !far.isFlat() ? QUARTER_TURN : 0.0F;
        return new TeleportTransition(toLevel, free, Vec3.ZERO, turn, 0.0F,
                Relative.union(Relative.DELTA, Relative.ROTATION),
                TeleportTransition.PLACE_PORTAL_TICKET.then(PortalExit::coolPassengers)
                        .then(arrived -> {
                            if (arrived.getUUID().equals(crossing)) {
                                after.onTransition(arrived);
                            }
                        }));
    }

    /**
     * Whether an entity of {@code size} has room inside {@code frame}: on a shaped frame, a
     * window of whole interior blocks its box fits.
     */
    public static boolean fits(EntityDimensions size, PortalFrame frame) {
        double extentAlong = extent(frame.plane().along(), size);
        double extentUp = extent(frame.plane().up(), size);
        if (extentAlong > frame.width() || extentUp > frame.height()) {
            return false;
        }
        return frame.isRectangle()
                || nearestWindow(frame, 0.0, 0.0, extentAlong, extentUp) != null;
    }

    // Riders could not use the portal while riding, so vanilla gave them no cooldown; one who
    // dismounts inside the far portal would otherwise go straight back alone.
    private static void coolPassengers(Entity entity) {
        for (Entity passenger : entity.getIndirectPassengers()) {
            passenger.setPortalCooldown();
        }
    }

    // Where on `axis` the entity sits within a frame `cells` long, as a fraction of the room it
    // has there: the middle (or the floor, going up) when it has none.
    private static double fraction(Vec3 pos, PortalFrame frame, Direction.Axis axis, int cells,
            EntityDimensions size) {
        double extent = extent(axis, size);
        double room = cells - extent;
        if (room <= 0.0) {
            return axis == Direction.Axis.Y ? 0.0 : 0.5;
        }
        double lowEdge = pos.get(axis) - (axis == Direction.Axis.Y ? 0.0 : extent / 2.0);
        return Mth.clamp((lowEdge - frame.origin().get(axis)) / room, 0.0, 1.0);
    }

    // The coordinate on `axis` that puts the entity's low edge `low` blocks past the origin: its
    // centre on a horizontal axis, its feet on the vertical one.
    private static double place(PortalFrame frame, Direction.Axis axis, double low,
            EntityDimensions size) {
        double lowEdge = frame.origin().get(axis) + low;
        return lowEdge + (axis == Direction.Axis.Y ? 0.0 : extent(axis, size) / 2.0);
    }

    // The low corner, in blocks from the origin along and up, nearest (u, v) where a box
    // extentAlong by extentUp lies on whole interior blocks; (u, v) itself on a rectangle, or
    // when no such place exists.
    private static double[] settle(PortalFrame frame, double u, double v, double extentAlong,
            double extentUp) {
        if (frame.isRectangle()) {
            return new double[] {u, v};
        }
        double[] nearest = nearestWindow(frame, u, v, extentAlong, extentUp);
        return nearest == null ? new double[] {u, v} : nearest;
    }

    // Over every window of whole interior blocks the box covers, the point in it nearest (u, v)
    // for the box's low corner; null when there is none.
    private static double @Nullable [] nearestWindow(PortalFrame frame, double u, double v,
            double extentAlong, double extentUp) {
        int across = Math.max(1, Mth.ceil(extentAlong));
        int tall = Math.max(1, Mth.ceil(extentUp));
        double[] best = null;
        double bestDistance = Double.MAX_VALUE;
        for (int v0 = 0; v0 + tall <= frame.height(); v0++) {
            for (int u0 = 0; u0 + across <= frame.width(); u0++) {
                if (!filled(frame, u0, v0, across, tall)) {
                    continue;
                }
                double atU = Mth.clamp(u, u0, u0 + across - extentAlong);
                double atV = Mth.clamp(v, v0, v0 + tall - extentUp);
                double distance = (atU - u) * (atU - u) + (atV - v) * (atV - v);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = new double[] {atU, atV};
                }
            }
        }
        return best;
    }

    private static boolean filled(PortalFrame frame, int u0, int v0, int across, int tall) {
        for (int v = v0; v < v0 + tall; v++) {
            for (int u = u0; u < u0 + across; u++) {
                if (!frame.hasCell(u, v)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static double extent(Direction.Axis axis, EntityDimensions size) {
        return axis == Direction.Axis.Y ? size.height() : size.width();
    }
}
