package club.rainbowkitty.kalic0re.common.mining;

import eu.pb4.polymer.core.api.block.PolymerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A Polymer block that leaves a creative player's break to vanilla, so the breaker hears it once.
 *
 * <p>Polymer mines each of its blocks on the server by default
 * ({@link PolymerBlock#handleMiningOnServer}), holding the client back with mining fatigue, and
 * sends the breaker the break event ({@code LevelEvent.PARTICLES_DESTROY_BLOCK}) that client then
 * never plays. It sends that event in every game mode. A creative client breaks the block at once
 * and plays the event itself, so the breaker would hear the block break twice. Other game modes
 * stay with Polymer, and every other player gets vanilla's event either way.
 *
 * <p>A default method cannot override a {@code handleMiningOnServer} a block declares or inherits
 * from a class; such a block calls {@link #minesOnServer} from its own. Polymer also mines on the
 * server when the held item asks it to ({@code PolymerItem#handleMiningOnServer}), which this
 * leaves alone.
 */
public interface VanillaCreativeBreak extends PolymerBlock {
    /** Whether Polymer should mine a block on the server for {@code player}: not in creative. */
    public static boolean minesOnServer(ServerPlayer player) {
        return !player.isCreative();
    }

    @Override
    public default boolean handleMiningOnServer(ItemStack tool, BlockState state, BlockPos pos,
            ServerPlayer player) {
        return minesOnServer(player);
    }
}
