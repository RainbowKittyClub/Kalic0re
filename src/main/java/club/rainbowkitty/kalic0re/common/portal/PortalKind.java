package club.rainbowkitty.kalic0re.common.portal;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;

/**
 * One kind of portal, as a consumer defines it: what its frame is made of, what its interior
 * looks like, and what happens when one lights, fades, is entered or left. The framework finds
 * frames, keeps the lit ones and their blocks, draws them, and asks this at each of those
 * moments; which portal leads where, who may do what and what it sounds like are the consumer's.
 */
public interface PortalKind {
    /** Whether {@code state} may be part of a frame. */
    public boolean isFrameBlock(BlockState state);

    /**
     * What each interior block of a lit portal is shown as. Asked once, on first use, and kept;
     * the item stack is copied, so a stack that changes later changes nothing.
     */
    public PortalMaterial material();

    /** After the interior of {@code frame} is lit. */
    public default void onLit(ServerLevel level, PortalFrame frame) {
    }

    /**
     * When the portal in {@code frame} goes out, before its blocks clear: its frame broke, or a
     * consumer asked. {@code level} is null when its dimension no longer exists.
     */
    public default void onFaded(MinecraftServer server, @Nullable ServerLevel level,
            PortalFrame frame) {
    }

    /**
     * Where {@code entity}, standing in the portal block at {@code entryPos} of the lit portal in
     * {@code frame}, goes: a transition, or null to stay. Asked once per entry, after
     * {@link #transitionTime}, for a portal that has just been checked whole.
     * {@link Portals#transition} builds the usual answer.
     */
    public @Nullable TeleportTransition onEnter(ServerLevel level, Entity entity, PortalFrame frame,
            BlockPos entryPos);

    /**
     * After {@code entity} arrives in {@code destination} by {@link Portals#transition}: once per
     * crossing, for the entity that entered; its riders arrive with it and are not reported.
     */
    public default void onExit(Entity entity, PortalFrame destination) {
    }

    /** How long an entity stands in a portal of this kind before {@link #onEnter}, in ticks. */
    public default int transitionTime(ServerLevel level, Entity entity) {
        return 0;
    }
}
