package club.rainbowkitty.kalic0re.common.customclick;

/**
 * How many custom clicks a player may send. Up to {@code burst} clicks pass at once, refilled at
 * {@code perSecond}; clicks beyond that are dropped. A nonzero {@code kickPerSecond} disconnects a
 * player once their clicks, dropped ones included, overflow a bucket of two seconds' worth draining
 * at that rate; a burst delivered at once after lag counts too. 0 never disconnects.
 */
public record ClickLimits(int perSecond, int burst, int kickPerSecond) {
    /** The package's own limits: ten a second, ten at once, and no disconnect. */
    public static final ClickLimits DEFAULT = new ClickLimits(10, 10, 0);

    public ClickLimits {
        if (perSecond < 1 || burst < 1 || kickPerSecond < 0) {
            throw new IllegalArgumentException("Click limits must be positive: " + perSecond
                    + "/s, burst " + burst + ", kick " + kickPerSecond + "/s");
        }
    }
}
