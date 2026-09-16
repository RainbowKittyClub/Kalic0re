package club.rainbowkitty.kalic0re.common.display;

import java.util.Arrays;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * A short history of where an entity has been, read back at a delay in ticks.
 *
 * <p>A display posed by the server — a Polymer element riding an entity, or one drawn between two —
 * has to be built against the entity the <em>client</em> is drawing, not the one the server is
 * holding. For a minecart those are several ticks apart, and by a fixed amount rather than a
 * drifting one: the server accumulates one movement step per tick and flushes the batch on the
 * tracker's own interval, and the client replays that batch one step per tick, so accumulation and
 * replay run at the same rate. What that delay is not is a whole number of ticks — a network hop
 * sits on top of the replay — so a readback mixes the two samples either side of the figure asked
 * for.
 *
 * <p>The figure itself belongs to the delivery path and to where the display sits on it, not to
 * this class, and there is no way to derive it: each caller states its own, found by eye.
 */
public final class RenderTrail {

    // The two samples a fractional readback falls between, on top of the ones already passed.
    private static final int MARGIN = 2;

    // One sample of the entity's placement per tick, oldest overwritten first.
    private final Vec3[] positions;
    private final float[] yaws;
    private final float[] pitches;
    private final double maxLag;

    private int next;

    /**
     * A trail filled with {@code entity}'s current placement, so it reads as standing still rather
     * than as having just arrived from the origin.
     *
     * @param entity the entity to sample
     * @param maxLag the deepest readback this trail will be asked for, in ticks, which is what it
     *     keeps room for; anything deeper is clamped to it rather than reaching past the samples
     * @throws IllegalArgumentException if {@code maxLag} is negative
     */
    public RenderTrail(Entity entity, double maxLag) {
        // Negated so NaN is caught with the negatives. Either one survives construction only to
        // surface at the first readback — as inverted clamp bounds, or as a silent zero-tick
        // trail — a long way from whoever chose the figure.
        if (!(maxLag >= 0.0)) {
            throw new IllegalArgumentException("RenderTrail.maxLag must be >= 0, got " + maxLag);
        }

        int size = (int) Math.ceil(maxLag) + MARGIN;

        this.positions = new Vec3[size];
        this.yaws = new float[size];
        this.pitches = new float[size];
        this.maxLag = maxLag;

        Arrays.fill(positions, entity.position());
        Arrays.fill(yaws, entity.getYRot());
        Arrays.fill(pitches, entity.getXRot());
    }

    /** Records this tick's placement, overwriting the oldest sample. Call exactly once a tick. */
    public void push(Entity entity) {
        positions[next] = entity.position();
        yaws[next] = entity.getYRot();
        pitches[next] = entity.getXRot();
        next = (next + 1) % positions.length;
    }

    /** Where the entity was {@code lag} ticks ago. */
    public Vec3 position(double lag) {
        int whole = whole(lag);

        return Mth.lerp(fraction(lag), positions[index(whole)], positions[index(whole + 1)]);
    }

    /** The entity's yaw {@code lag} ticks ago, taken the short way round. */
    public float yaw(double lag) {
        return rotation(yaws, lag);
    }

    /** The entity's pitch {@code lag} ticks ago, taken the short way round. */
    public float pitch(double lag) {
        return rotation(pitches, lag);
    }

    // One delayed angle, mixed from the two samples the delay falls between. The cast is rotLerp's
    // own signature, so it stays here rather than in fraction, which position needs full width.
    private float rotation(float[] samples, double lag) {
        int whole = whole(lag);

        return Mth.rotLerp((float) fraction(lag), samples[index(whole)], samples[index(whole + 1)]);
    }

    // The newer of the two samples a readback mixes: how many whole ticks back it starts.
    private int whole(double lag) {
        return (int) clamp(lag);
    }

    // How far past that sample the readback lies, toward the older one.
    private double fraction(double lag) {
        double delay = clamp(lag);

        return delay - (int) delay;
    }

    // Where the sample from `back` ticks ago sits in the ring, counting this tick's as zero.
    private int index(int back) {
        return Math.floorMod(next - 1 - back, positions.length);
    }

    // Held inside what the trail actually keeps, so a caller tuning a lag by hand cannot index off
    // the end of it.
    private double clamp(double lag) {
        return Math.clamp(lag, 0.0, maxLag);
    }
}
