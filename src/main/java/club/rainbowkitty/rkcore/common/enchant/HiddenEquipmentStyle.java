package club.rainbowkitty.rkcore.common.enchant;

/**
 * How a {@link HideEquipmentModelEnchantments}-flagged item's equipped model is suppressed.
 *
 * <p>Two independent choices, named as their four combinations because a caller picks exactly one:
 * whether the now-invisible mesh still writes depth, and whether the enchantment glint still draws.
 * Depth is the interesting half — armor is modelled fractionally outside the skin it covers, so an
 * invisible mesh that still writes depth occludes the wearer's own body and cuts a piece-shaped
 * hole clean through them, showing the world behind.
 */
public enum HiddenEquipmentStyle {
    /** Nothing drawn at all; the piece may as well not be equipped. */
    HIDDEN(false, false),

    /** Mesh gone, glint still drawn, wearer's body intact underneath. The usual "shadowed" look. */
    HIDDEN_GLINT(false, true),

    /** Mesh gone but still occluding, so the wearer is cut away where the piece would have been. */
    PHANTOM(true, false),

    /** As {@link #PHANTOM}, with the glint drawn over the hole. */
    PHANTOM_GLINT(true, true);

    private final boolean occluding;
    private final boolean glinting;

    HiddenEquipmentStyle(boolean occluding, boolean glinting) {
        this.occluding = occluding;
        this.glinting = glinting;
    }

    /** Whether the invisible mesh still writes depth, hiding whatever sits behind it. */
    public boolean occluding() {
        return occluding;
    }

    /** Whether the enchantment glint still draws. */
    public boolean glinting() {
        return glinting;
    }
}
