package club.rainbowkitty.kalic0re.common.portal;

import java.util.Set;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * One kind of portal in play: its block, its frame finder, and its lit frames. A consumer
 * registers one per {@link PortalKind} at init, lights and fades frames through it, and asks it
 * which portal a block belongs to. Everything a lit portal does beyond standing there, the kind
 * decides.
 *
 * <p>Nothing here runs per tick: vanilla's portal processing carries entities, Polymer attaches
 * the displays to chunks, and the lit frames are looked up by chunk.
 */
public final class Portals {
    private final PortalKind kind;
    private final PortalBlock block;
    private final FrameFinder finder;
    private final SavedDataType<LitFrames> type;
    private @Nullable PortalMaterial material;

    private Portals(PortalKind kind, ResourceKey<Block> key, BlockBehaviour.Properties properties) {
        this.kind = kind;
        this.block = Registry.register(BuiltInRegistries.BLOCK, key,
                new PortalBlock(properties.setId(key), this));
        this.finder = new FrameFinder(kind::isFrameBlock, block);
        Identifier id = key.identifier();
        this.type = LitFrames.type(this,
                Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_frames"));
    }

    /**
     * Registers the portal block {@code modId:path} for {@code kind}, with {@code properties}
     * (start from {@link PortalBlock#defaultProperties}), and returns the handle everything
     * else goes through. Its lit frames save as {@code modId:path_frames}. Call once, at init.
     */
    public static Portals register(String modId, String path, PortalKind kind,
            BlockBehaviour.Properties properties) {
        ResourceKey<Block> key =
                ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(modId, path));
        Portals portals = new Portals(kind, key, properties);
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, newChunk) ->
                portals.frames(level.getServer()).onChunkLoad(level, chunk.getPos()));
        // A display chose its variant when the player started watching; a pack accepted or
        // dropped since would otherwise show the wrong one until the chunk is watched anew.
        PolymerCommonUtils.ON_RESOURCE_PACK_STATUS_CHANGE.register(
                (handler, uuid, oldStatus, newStatus) -> {
                    if (handler instanceof ServerGamePacketListenerImpl play) {
                        portals.redraw(play);
                    }
                });
        return portals;
    }

    /** The kind these portals are. */
    public PortalKind kind() {
        return kind;
    }

    /** The kind's material, asked once on first use; its item stack is copied and kept. */
    public PortalMaterial material() {
        if (material == null) {
            PortalMaterial given = kind.material();
            material = new PortalMaterial(given.packed().copy(), given.plain());
        }
        return material;
    }

    /** The block a lit interior is made of. */
    public PortalBlock block() {
        return block;
    }

    /** Finds frames of this kind. */
    public FrameFinder finder() {
        return finder;
    }

    /** Whether a lit portal stands in {@code frame}. */
    public boolean has(MinecraftServer server, PortalFrame frame) {
        return frames(server).has(frame);
    }

    /** Every lit frame, as a copy, for a consumer that reconciles its own records against them. */
    public Set<PortalFrame> lit(MinecraftServer server) {
        return frames(server).lit();
    }

    /** The lit portal whose interior holds {@code pos}, if any. */
    public @Nullable PortalFrame portalAt(ServerLevel level, BlockPos pos) {
        return frames(level.getServer()).portalAt(level.dimension(), pos);
    }

    /** The lit portal whose interior or ring holds {@code pos}, if any. */
    public @Nullable PortalFrame portalOwning(ServerLevel level, BlockPos pos) {
        return frames(level.getServer()).portalOwning(level.dimension(), pos);
    }

    /**
     * Lights {@code frame}: fills its interior and tells the kind. A portal already standing
     * there is faded first. False, with nothing changed, when the frame is not complete by then,
     * as when it crosses a portal lit a moment ago.
     */
    public boolean light(MinecraftServer server, PortalFrame frame) {
        return frames(server).light(server, frame);
    }

    /**
     * Fades the portal in {@code frame}: tells the kind, then clears its blocks on a following
     * tick, or when its chunks next load. Nothing happens for a frame that is not lit.
     */
    public void fade(MinecraftServer server, PortalFrame frame) {
        frames(server).fade(server, frame);
    }

    /**
     * The usual answer to {@link PortalKind#onEnter}: {@code entity} comes out of the lit portal
     * in {@code to} where it went into {@code from} ({@link PortalExit}), and the kind hears of
     * the arrival through {@link PortalKind#onExit}. The caller has checked
     * {@link PortalExit#fits}; an entity too big for {@code to} would be set through its ring.
     */
    public TeleportTransition transition(Entity entity, PortalFrame from, ServerLevel toLevel,
            PortalFrame to) {
        return PortalExit.transition(entity, from, toLevel, to,
                arrived -> kind.onExit(arrived, to));
    }

    // The block's question: where an entity standing in it goes. A block no lit portal owns is
    // cleared; a portal no longer whole fades; otherwise the kind answers.
    @Nullable TeleportTransition destination(ServerLevel level, Entity entity, BlockPos pos) {
        PortalFrame frame = portalAt(level, pos);
        if (frame == null) {
            level.destroyBlock(pos, false);
            return null;
        }
        if (!finder.isLit(level, frame)) {
            fade(level.getServer(), frame);
            return null;
        }
        return kind.onEnter(level, entity, frame, pos);
    }

    // One of the block's blocks is gone.
    void onBlockRemoved(ServerLevel level, BlockPos pos) {
        frames(level.getServer()).onBlockRemoved(level, pos);
    }

    // Shows this player every portal display in loaded chunks of their level afresh, so each
    // picks its variant by their pack status as it stands now.
    private void redraw(ServerGamePacketListenerImpl handler) {
        ServerPlayer player = handler.player;
        ServerLevel level = player.level();
        for (PortalFrame frame : frames(level.getServer()).inDimension(level.dimension())) {
            for (BlockPos pos : frame.interior()) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(
                        SectionPos.blockToSectionCoord(pos.getX()),
                        SectionPos.blockToSectionCoord(pos.getZ()));
                BlockBoundAttachment attachment =
                        chunk == null ? null : BlockBoundAttachment.get(chunk, pos);
                if (attachment != null) {
                    ElementHolder holder = attachment.holder();
                    if (holder.stopWatching(handler)) {
                        holder.startWatching(handler);
                    }
                }
            }
        }
    }

    private LitFrames frames(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(type);
    }
}
