package club.rainbowkitty.kalic0re.common.glow;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.TeamColor;

/**
 * Lights one entity up for one player, without the rest of the server seeing it.
 *
 * <p>Glowing is a bit of an entity's shared flags and its colour comes from the scoreboard team the
 * entity is in, so both are server state that every client is told about. This lies to one client
 * instead: it sends that player a flags value the server does not hold, and a team that exists
 * only in their copy of the scoreboard. Nothing here touches the entity or the real scoreboard, so
 * there is nothing to clean up on the server and nothing another player can be shown by mistake.
 *
 * <p>The lie is only as durable as the client's copy. Any real update to the entity's flags
 * overwrites it, so a caller has to keep saying it — a repeating tick, not a one-off call.
 */
public final class PrivateGlow {

    // Which bit of the shared flags byte means glowing.
    private static final int GLOWING_FLAG = 6;

    // Entity.DATA_SHARED_FLAGS_ID is protected, and widening it would fall to every mod this is
    // relocated into. Rebuilt: Entity roots the hierarchy and defines the flags first, so id 0.
    private static final EntityDataAccessor<Byte> SHARED_FLAGS =
            new EntityDataAccessor<>(0, EntityDataSerializers.BYTE);

    // One name per colour, shared across relocated copies on purpose: a client's team ADD merges
    // members, so two mods can light different entities the same colour without fighting over it.
    private static final String TEAM_PREFIX = "kalic0re_glow_";

    // The teams only ever exist in one client's scoreboard, so they are built against a throwaway
    // one rather than the world's: they are here to be serialised into a packet, never registered.
    private static final Scoreboard PACKET_SCOREBOARD = new Scoreboard();

    // Which colours a client has already been told to make a team for. Keyed on the connection
    // because that is exactly how long its scoreboard lives — across a respawn, not a relog —
    // and weakly, so a player leaving drops their entry without anything here being told.
    private static final Map<ServerGamePacketListenerImpl, Set<TeamColor>> ANNOUNCED =
            new WeakHashMap<>();

    // Static-only; blocks instantiation.
    private PrivateGlow() {}

    /**
     * Shows {@code viewer} the outline of {@code entity} in {@code colour}, or takes it away again.
     *
     * <p>Must be repeated to stay up: the client forgets it as soon as the server tells it anything
     * else about that entity.
     *
     * @param viewer the only player who is shown this
     * @param entity what to outline, real server-side entity and all
     * @param colour the outline colour, which also tints any name tag that entity shows
     * @param lit whether the outline is on this time round, so a caller can blink it
     */
    public static void show(
            ServerPlayer viewer, Entity entity, TeamColor colour, boolean lit) {
        viewer.connection.send(joinPacket(viewer, entity, colour));
        viewer.connection.send(new ClientboundSetEntityDataPacket(
                entity.getId(), List.of(flags(entity, lit))));
    }

    /**
     * Puts {@code viewer}'s copy of {@code entity} back to what the server actually holds.
     * Harmless on an entity that was never lit, and on one that has since been removed.
     */
    public static void clear(ServerPlayer viewer, Entity entity) {
        viewer.connection.send(new ClientboundSetEntityDataPacket(
                entity.getId(), List.of(flags(entity, false))));
    }

    // The entity's own flags byte with the glowing bit forced on or off, packed as the one synced
    // value to overwrite. Built from the live byte so nothing else about the entity is disturbed:
    // a cart that is genuinely on fire stays on fire in the copy the viewer is sent.
    private static SynchedEntityData.DataValue<?> flags(Entity entity, boolean lit) {
        byte held = entity.getEntityData().get(SHARED_FLAGS);
        byte value = (byte) (lit
                ? held | 1 << GLOWING_FLAG
                : held & ~(1 << GLOWING_FLAG));

        return SynchedEntityData.DataValue.create(SHARED_FLAGS, value);
    }

    // Puts the entity in this colour's team, one team per colour rather than one per entity: the
    // client drops an entity from the team it was in as it joins the next, so a cart cannot end up
    // wearing two colours at once.
    private static ClientboundSetPlayerTeamPacket joinPacket(
            ServerPlayer viewer, Entity entity, TeamColor colour) {
        PlayerTeam team = new PlayerTeam(
                PACKET_SCOREBOARD, TEAM_PREFIX + colour.getSerializedName());
        team.setColor(Optional.of(colour));

        // Only the first packet makes the team. Asking a client to make one it already has is not
        // an error, but it warns in their log, and the outline says this several times a second.
        if (announce(viewer, colour)) {
            team.getPlayers().add(entity.getStringUUID());

            return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
        }

        return ClientboundSetPlayerTeamPacket.createPlayerPacket(
                team, entity.getStringUUID(), ClientboundSetPlayerTeamPacket.Action.ADD);
    }

    // Whether this viewer still has to be sent the team for this colour, claiming it if so.
    private static boolean announce(ServerPlayer viewer, TeamColor colour) {
        return ANNOUNCED
                .computeIfAbsent(viewer.connection, open -> EnumSet.noneOf(TeamColor.class))
                .add(colour);
    }
}
