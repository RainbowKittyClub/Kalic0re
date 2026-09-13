package club.rainbowkitty.kalic0re.common.vehicle;

import java.util.Optional;

import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import org.jspecify.annotations.Nullable;

import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.Blocks;

/**
 * Wires a {@link CartCargo} cart to its cargo — picking vanilla's cargo slot or a Polymer stand-in,
 * and owning the stand-in's lifetime.
 *
 * <p>Held by the cart entity rather than inherited by it, because the cart already has a superclass
 * worth keeping ({@code MinecartChest} and its siblings carry the whole container implementation).
 * The entity drives it from three places: {@link #tick()} from its own {@code tick},
 * {@link #destroy()} from {@code remove}, and {@link #refresh()} whenever its cargo changes —
 * including when the cargo first arrives from the placing item stack, which happens after the
 * entity is constructed.
 */
public final class CartCargoSupport {
    private final AbstractMinecart cart;
    private final CartCargo cargo;

    private @Nullable HolderAttachment attachment;
    private boolean applied;

    private CartCargoSupport(AbstractMinecart cart, CartCargo cargo) {
        this.cart = cart;
        this.cargo = cargo;
    }

    /** Support for a cart that is its own {@link CartCargo}, which is the ordinary case. */
    public static <T extends AbstractMinecart & CartCargo> CartCargoSupport of(T cart) {
        return new CartCargoSupport(cart, cart);
    }

    /**
     * Applies the cargo once the cart is in a server level, and does nothing thereafter. Call from
     * the cart's {@code tick}, which is late enough for the cargo to have been read off the stack
     * that placed it.
     */
    public void tick() {
        if (!applied && cart.level() instanceof ServerLevel) {
            apply();
        }
    }

    /** Rebuilds the stand-in from the cart's current cargo, on the next tick. */
    public void refresh() {
        applied = false;
    }

    /** Drops the stand-in. Call from the cart's {@code remove}. */
    public void destroy() {
        if (attachment != null) {
            attachment.destroy();
            attachment = null;
        }
        applied = false;
    }

    // Picks the route for the current cargo: vanilla's own slot for a state the client can render,
    // a mounted stand-in for anything else.
    private void apply() {
        destroy();
        applied = true;

        CargoDisplayHolder display = cargo.createCargoDisplay();
        if (display == null) {
            cart.setCustomDisplayBlockState(Optional.of(cargo.cargoState()));
            return;
        }

        // Air renders as nothing, so the cargo slot resolves to an empty model and the renderer
        // skips it entirely — leaving the stand-in as the only thing drawn in the cart. Blanking
        // has to be explicit: a container cart's default cargo is its own vanilla block.
        cart.setCustomDisplayBlockState(Optional.of(Blocks.AIR.defaultBlockState()));
        attachment = EntityAttachment.ofTicking(display, cart);

        // Riders are announced to a client only when it starts tracking the cart. This runs on the
        // cart's first tick, and again whenever its cargo changes, by which time every client
        // nearby is already tracking it — so without re-announcing, the cargo is spawned and then
        // never mounted, and hangs wherever the cart happened to be.
        display.sendPacket(new ClientboundSetPassengersPacket(cart));
    }
}
