package club.rainbowkitty.rkcore.common.armor;

import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;

import net.minecraft.core.component.DataComponents;

/** Polymer wiring every mod registering armour sets needs, so none of them writes it again. */
public final class ArmorPolymerSupport {
    private ArmorPolymerSupport() {
    }

    /**
     * Puts a mod's assets into Polymer's generated pack and keeps unbreakable pieces from drawing
     * the fallback item's durability bar. Call once from the mod's initializer, before registering
     * any set.
     *
     * <p>Deliberately does not call {@code PolymerResourcePackUtils.markAsRequired()}: whether a
     * server's players must accept the pack is that server's decision, not a consequence of
     * wearing custom armour. A mod that wants it calls it itself.
     */
    public static void init(String modId) {
        PolymerResourcePackUtils.addModAssets(modId);
        PolymerItemUtils.addCopiedComponent(DataComponents.UNBREAKABLE);
    }
}
