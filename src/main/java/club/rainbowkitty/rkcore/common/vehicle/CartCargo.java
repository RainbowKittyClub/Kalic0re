package club.rainbowkitty.rkcore.common.vehicle;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.level.block.state.BlockState;

/**
 * What a minecart carries, implemented by the cart entity itself.
 *
 * <p>A vanilla minecart already carries an arbitrary block: the cargo is a synched block state and
 * the renderer draws whatever model resolves from it. What is not arbitrary is which blocks have a
 * model — a chest, a shulker box and their kin are drawn by a hardcoded table keyed on the block
 * object, so a modded block resolves to nothing at all. {@link CartCargoSupport} therefore takes
 * one of two routes per cart: a cargo the client can render is handed straight to vanilla's cargo
 * slot, and a cargo it cannot is blanked there and drawn by the Polymer stand-in {@link
 * #createCargoDisplay()} returns instead.
 */
public interface CartCargo {
    /**
     * The block state this cart carries.
     *
     * <p>When there is no stand-in this state is sent to every client as-is. Nothing remaps block
     * state ids inside entity data the way chunk data is remapped, so the vanilla route is only
     * safe for a state the client already knows — in practice a vanilla block. A modded state needs
     * a stand-in, whether or not the mod could render it locally.
     *
     * @return the state vanilla draws when {@link #createCargoDisplay()} returns {@code null}; with
     *     a stand-in it is the cargo's identity only, and nothing renders it
     */
    BlockState cargoState();

    /**
     * A stand-in drawn over the cart for clients that cannot render {@link #cargoState()}, or
     * {@code null} to let vanilla draw that state itself.
     *
     * <p>Called once per cargo change rather than per tick, so it may build elements eagerly. To
     * send the stand-in to only some connections — a client running the mod may be able to render
     * the real thing — override {@code startWatching} on the returned holder.
     */
    @Nullable CargoDisplayHolder createCargoDisplay();
}
