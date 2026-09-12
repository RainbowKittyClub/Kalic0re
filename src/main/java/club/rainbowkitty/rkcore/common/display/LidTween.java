package club.rainbowkitty.rkcore.common.display;

import net.minecraft.util.Mth;

/**
 * A container lid's open/closed progress, ticked server-side.
 *
 * <p>Vanilla animates a lid on the client alone — {@code ChestLidController} for a chest,
 * {@code ShulkerBoxBlockEntity#updateAnimation} for a shulker box — so a display entity standing in
 * for one has to run the ramp itself; a server's {@code ChestBlockEntity.getOpenNess} reads 0
 * forever. Both vanilla controllers step by the same {@link #STEP} a tick and clamp at either end,
 * which is exactly {@link Mth#approach}.
 *
 * <p>Whether an eased curve goes on top is the caller's and differs by block: a chest's renderer
 * applies {@link #easeOut}, a shulker box's draws the raw progress.
 *
 * <p>Shared by relocation, not a mod dependency (see each consuming mod's {@code build.gradle}).
 */
public final class LidTween {
    /** Progress a lid gains or loses per tick, the step both vanilla lid controllers use. */
    public static final float STEP = 0.1f;

    private float progress;

    /**
     * Vanilla's ease-out cubic for a chest lid, from {@code ChestRenderer#submit} — the lid leaves
     * its closed position quickly and settles slowly.
     *
     * @param progress raw progress, 0 closed to 1 fully open
     * @return the openness to draw the lid at
     */
    public static float easeOut(float progress) {
        float closed = 1.0f - progress;
        return 1.0f - closed * closed * closed;
    }

    /** Progress as last advanced, 0 closed to 1 fully open, before any easing. */
    public float progress() {
        return progress;
    }

    /**
     * Steps progress one tick toward open or closed.
     *
     * @param open whether anything currently holds the lid open
     * @return whether this tick moved it, so a caller can skip re-sending an unchanged pose
     */
    public boolean advance(boolean open) {
        float next = Mth.approach(progress, open ? 1.0f : 0.0f, STEP);
        if (next == progress) {
            return false;
        }
        progress = next;
        return true;
    }
}
