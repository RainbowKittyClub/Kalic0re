package club.rainbowkitty.kalic0re.common.armor;

import net.minecraft.resources.Identifier;

/**
 * One texture layer of an armour piece, drawn on every target the piece declares.
 *
 * <p>A target's layers are drawn in declaration order, so a trim or overlay is declared after the
 * base it sits on.
 *
 * @param texture the texture id, which resolves per target to
 *        {@code textures/entity/equipment/<target>/<path>.png}
 * @param dye how the client tints this layer from the stack's {@code dyed_color} component
 */
public record ArmorLayer(Identifier texture, ArmorLayer.Dye dye) {
    /** How a layer responds to the wearer's {@code dyed_color} component. */
    public enum Dye {
        /** Always drawn, never tinted — the layer whose colour is baked into its texture. */
        NONE,
        /** Always drawn, tinted once dyed and drawn in leather's default brown until then. */
        TINTED,
        /** Drawn only once the piece is dyed, tinted — a trim that appears with the dye. */
        ONLY_IF_DYED
    }

    /** A plain, untinted layer. */
    public static ArmorLayer plain(Identifier texture) {
        return new ArmorLayer(texture, Dye.NONE);
    }

    /** A layer tinted by the wearer's dye, falling back to leather's brown while undyed. */
    public static ArmorLayer tinted(Identifier texture) {
        return new ArmorLayer(texture, Dye.TINTED);
    }

    /** A layer that stays hidden until the piece is dyed, then draws in the dye's colour. */
    public static ArmorLayer onlyIfDyed(Identifier texture) {
        return new ArmorLayer(texture, Dye.ONLY_IF_DYED);
    }
}
