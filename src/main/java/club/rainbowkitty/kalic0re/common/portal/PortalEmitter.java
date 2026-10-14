package club.rainbowkitty.kalic0re.common.portal;

import java.util.List;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * One source of ambient particles on a lit portal, placed in the portal's own frame: {@code x}
 * runs with the frame's width, {@code y} with its height, {@code z} through the plane. The
 * particles of a series play in turn, one kind per burst; a burst scatters its particles about
 * its centre by a gaussian deviation per axis, as the particle command does.
 *
 * @param particles the particle, or the series played in turn
 * @param offset the burst's centre from the portal's centre
 * @param spread the deviation on each axis
 * @param scaled whether {@code offset} and {@code spread} on {@code x} and {@code y} are fractions
 *        of the frame's half-width and half-height rather than blocks, so one emitter fits a
 *        portal of any size; {@code z} is always blocks
 * @param count particles per burst
 * @param speed the particles' speed, as the particle command takes it
 * @param period ticks between bursts, at least 1
 */
public record PortalEmitter(List<ParticleOptions> particles, Vec3 offset, Vec3 spread,
        boolean scaled, int count, float speed, int period) {
    public PortalEmitter {
        if (particles.isEmpty()) {
            throw new IllegalArgumentException("PortalEmitter needs at least one particle");
        }
        if (count < 1 || period < 1) {
            throw new IllegalArgumentException("PortalEmitter count and period must be at least 1,"
                    + " got count=" + count + " period=" + period);
        }
        particles = List.copyOf(particles);
    }

    /**
     * A sheet over the whole portal: {@code count} particles every {@code period} ticks, spread
     * across the plane and {@code thickness} blocks through it.
     */
    public static PortalEmitter sheet(List<ParticleOptions> particles, double thickness, int count,
            float speed, int period) {
        return new PortalEmitter(particles, Vec3.ZERO, new Vec3(0.5, 0.5, thickness), true, count,
                speed, period);
    }

    /** Whether this emitter bursts on game tick {@code time}. */
    public boolean due(long time) {
        return time % period == 0;
    }

    /** Sends one burst at {@code frame} in {@code level}, the {@code time}th tick of the game. */
    public void burst(ServerLevel level, PortalFrame frame, long time) {
        Vec3 at = offset;
        Vec3 deviation = spread;
        if (scaled) {
            double halfWidth = frame.width() / 2.0;
            double halfHeight = frame.height() / 2.0;
            at = new Vec3(at.x * halfWidth, at.y * halfHeight, at.z);
            deviation = new Vec3(deviation.x * halfWidth, deviation.y * halfHeight, deviation.z);
        }
        Vec3 centre = frame.centre().add(frame.plane().toWorld(at));
        Vec3 worldDeviation = frame.plane().toWorld(deviation);
        ParticleOptions particle = particles.get((int) ((time / period) % particles.size()));
        level.sendParticles(particle, centre.x, centre.y, centre.z, count, worldDeviation.x,
                worldDeviation.y, worldDeviation.z, speed);
    }
}
