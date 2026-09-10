package club.rainbowkitty.rkcore.armorstands;

import net.minecraft.world.level.gamerules.GameRule;

import club.rainbowkitty.rkcore.common.rule.ComponentGameRules;

/** Game rules for the armor-stand feature. */
public final class ArmorStandRules {
    public static final GameRule<Boolean> SHOW_ARMS_BY_DEFAULT =
            ComponentGameRules.registerBoolean("armorstands", "show_arms_by_default", true);

    private ArmorStandRules() {
    }

    /** Forces class initialization so the rules land in the registry before it freezes. */
    public static void init() {
    }
}
