package club.rainbowkitty.rkcore.common.vehicle;

import java.util.Arrays;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.GenericEntityElement;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

/**
 * The Polymer stand-in for a cargo the client cannot render: elements that ride the cart as
 * passengers, re-posed whenever the cart turns.
 *
 * <p>Riding rather than being positioned by the server is what keeps the cargo glued to a moving
 * cart. The client places a passenger itself, every frame, from the cart's own interpolated
 * position — a server-positioned element would arrive a tick late and visibly trail. Add elements
 * with {@link #addCargoElement}, never {@code addElement}, or they get the trailing behaviour.
 *
 * <p>The client gets no rotation for a passenger, only a position, so a subclass applies the cart's
 * facing itself through {@link #applyPose}.
 */
public abstract class CargoDisplayHolder extends ElementHolder {
    /**
     * Where a client puts a passenger of a container minecart, as a height above the cart's own
     * position: the {@code passengerAttachments(0.1875F)} every vanilla container cart is built
     * with. A subclass placing its cargo where vanilla draws a chest works from this point, not
     * from the cart's position, and {@link CargoMinecarts#containerCartBuilder} keeps the server's
     * entity type agreeing with it.
     */
    public static final float PASSENGER_ATTACHMENT_Y = 0.1875f;

    // A cart's movement reaches the client a few ticks late. The server accumulates one
    // MinecartStep per tick and flushes the batch on the tracker's updateInterval, 3 for a cart
    // (ServerEntity.java:125, EntityType.java:482); NewMinecartBehavior replays a batch over
    // exactly three client ticks (lerpDelay = 3, NewMinecartBehavior.java:78). Accumulation and
    // replay run at the same rate, so this is a fixed pipeline rather than a sawtooth: the client
    // begins a batch on the oldest step in it, already two ticks old, and a network hop is added
    // on top. The client therefore draws the cart at a rotation this thread stopped holding
    // several ticks ago, and posing the cargo from the cart's *current* rotation runs it ahead of
    // the cart it is sitting in — a visible lead through a curve, in the opposite direction from
    // the lag one would expect.
    //
    /**
     * Ticks to look back by default, tuned by eye on a curve at speed because there was no other
     * way to find it — 3.5, minecart-tweaks' {@code LinkChain} and the starting point, trailed
     * clearly; 2.5 led; 2.8 is where a scoreboard-driven search settled. A chain is placed
     * <em>between</em> two carts and waits out a hop of its own, where this rides one cart and is
     * drawn from it, so it wants a little less than LinkChain's.
     *
     * <p>No constant is exactly right on every tick, and the residue is not mainly the network.
     * Both the cart's move packet and this cargo's transform are produced in the same server tick
     * and leave in the same flush, so they cross the wire together and a player's ping shifts both
     * equally — it cancels. What does not cancel: a cart interpolates its rotation
     * <em>within</em> a tick and a display element with an interpolation duration of zero cannot,
     * which is half a tick of error oscillating at frame rate; and an off-schedule flush, which
     * {@code ServerEntity} will do for any dirty synched data, ships a short batch that the client
     * still stretches over three ticks, walking the delay by up to three until the schedule
     * resettles.
     */
    protected static final double DEFAULT_RENDER_LAG = 2.8;

    // The furthest back renderLag() may ask to look, and so how many samples the trail keeps —
    // enough for both of the ticks that figure falls between.
    private static final int MAX_RENDER_LAG = 8;
    private static final int SAMPLES = MAX_RENDER_LAG + 2;

    private final AbstractMinecart cart;

    // One sample of the cart's rotation per tick, oldest overwritten first, so the pose can be
    // taken from where the cart is being drawn rather than from where it is.
    private final float[] yaws = new float[SAMPLES];
    private final float[] pitches = new float[SAMPLES];
    private int next;

    // NaN so the first tick always counts as a change and pushes the real pose, whatever the cart's
    // initial rotation happens to be.
    private float shownYaw = Float.NaN;
    private float shownPitch = Float.NaN;

    /**
     * @param cart the cart this cargo rides, whose pose {@link #applyPose} is driven from
     */
    protected CargoDisplayHolder(AbstractMinecart cart) {
        this.cart = cart;
        // Fill the trail so a cart that has not moved yet reads as standing still rather than as
        // having just swung round from zero.
        Arrays.fill(yaws, cart.getYRot());
        Arrays.fill(pitches, cart.getXRot());
    }

    /** The cart this cargo rides. */
    public AbstractMinecart cart() {
        return cart;
    }

    /**
     * Adds an element that rides the cart. Prefer this to {@code addElement}, which leaves the
     * element positioned by the server and lagging a tick behind a moving cart.
     *
     * <p>A ridden element is still sent a position update every tick by default, aimed at the
     * cart's own position rather than at the passenger point the client is drawing it at, and the
     * client honours it for the frame before it puts the rider back — a drop and snap on every tick
     * of movement. So position updates are switched off here rather than merely wasted.
     */
    public <T extends GenericEntityElement> T addCargoElement(T element) {
        element.ignorePositionUpdates();
        // Spawn it at the seat rather than at the cart's own position. A client moves a passenger
        // to the seat itself, but not until it has processed the passenger packet, and with
        // position updates off nothing will ever correct it in the meantime — so an element left at
        // the default offset appears inside the cart's floor and rises out of it.
        element.setOffset(new Vec3(0.0, PASSENGER_ATTACHMENT_Y, 0.0));
        return addPassengerElement(element);
    }

    /**
     * Applies the cart's rotation to the elements. Called on the first tick and then only when the
     * cart has turned, so it is the place for the whole transform, not a delta.
     *
     * @param yaw the cart's yaw in degrees, delayed to match the cart the client is drawing
     * @param pitch the cart's pitch in degrees, likewise delayed; non-zero on a sloped rail
     */
    protected abstract void applyPose(float yaw, float pitch);

    /**
     * Forces {@link #applyPose} on the next tick even if the cart has not turned. For a cargo that
     * changes shape on its own — a chest lid opening, say — where the pose is the only place the
     * new shape can be written.
     */
    protected void markPoseDirty() {
        shownYaw = Float.NaN;
        shownPitch = Float.NaN;
    }

    /**
     * Poses the cargo immediately. <strong>Call this at the end of a subclass constructor.</strong>
     * An element is spawned carrying no transform at all, so a holder that waits for its first tick
     * to pose one has already sent every watching client an unturned cargo, which then visibly
     * swings into place over the interpolation that follows.
     */
    protected final void poseNow() {
        pose();
    }

    @Override
    protected void onTick() {
        yaws[next] = cart.getYRot();
        pitches[next] = cart.getXRot();
        next = (next + 1) % SAMPLES;
        pose();
    }

    /**
     * How many ticks back to take the cart's rotation from, to match the cart the client is
     * drawing. Override only for a cart whose motion reaches the client differently from a rail
     * minecart's, or to make the figure tunable while it is being found.
     *
     * @return ticks, clamped into the trail this holder keeps; fractional values mix the two
     *     samples either side
     */
    protected double renderLag() {
        return DEFAULT_RENDER_LAG;
    }

    // Applies the cart's rotation as the client is currently drawing it, if that has moved since
    // the last time it was sent.
    private void pose() {
        double lag = Mth.clamp(renderLag(), 0.0, MAX_RENDER_LAG);
        int whole = (int) lag;
        float fraction = (float) (lag - whole);

        float yaw = Mth.rotLerp(fraction, yaws[index(whole)], yaws[index(whole + 1)]);
        float pitch = Mth.rotLerp(fraction, pitches[index(whole)], pitches[index(whole + 1)]);
        if (yaw != shownYaw || pitch != shownPitch) {
            shownYaw = yaw;
            shownPitch = pitch;
            applyPose(yaw, pitch);
        }
    }

    // The sample taken `back` ticks ago, counting this tick's as zero.
    private int index(int back) {
        return Math.floorMod(next - 1 - back, SAMPLES);
    }
}
