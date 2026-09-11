package club.rainbowkitty.rkcore.common.armor.datagen;

import java.util.Map;

import net.minecraft.world.item.equipment.ArmorType;

import club.rainbowkitty.rkcore.common.armor.ArmorLayerTarget;
import club.rainbowkitty.rkcore.common.armor.ArmorSet;

/**
 * Base art for a set whose textures are derived rather than drawn: one image per item icon and per
 * layer target, tinted into the set's own textures by {@link ArmorTextureProvider}.
 *
 * <p>A set with hand-drawn textures needs none of this — it ships them under its own names and
 * skips the provider entirely. Deriving is for a family of sets that differ only in colour, where
 * one piece of art and a colour list beat sixteen copies of the same image.
 *
 * @param set the set these textures belong to
 * @param tint an opaque ARGB colour every image is multiplied by, e.g.
 *        {@code DyeColor.getTextureDiffuseColor()}
 * @param itemArt classpath path to each piece's icon art, by slot, e.g.
 *        {@code assets/<mod>/textures/item/base.png}
 * @param layerArt classpath path to each target's layer art, by target
 */
public record ArmorArt(ArmorSet set, int tint, Map<ArmorType, String> itemArt,
        Map<ArmorLayerTarget, String> layerArt) {
    public ArmorArt {
        itemArt = Map.copyOf(itemArt);
        layerArt = Map.copyOf(layerArt);
        if (itemArt.isEmpty() && layerArt.isEmpty()) {
            throw new IllegalArgumentException("ArmorArt for set '" + set.name()
                    + "' names no art to derive from");
        }
    }
}
