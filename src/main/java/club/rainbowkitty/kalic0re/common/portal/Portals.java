package club.rainbowkitty.kalic0re.common.portal;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

    private Portals(PortalKind kind, ResourceKey<Block> key, BlockBehaviour.Properties properties) {
        this.kind = kind;
        this.block = Registry.register(BuiltInRegistries.BLOCK, key,
                new PortalBlock(properties.setId(key), this));
        this.finder = new FrameFinder(kind::isFrameBlock, block);
        this.type = LitFrames.type(this, key.identifier());
    }

    /**
     * Registers the portal block {@code modId:path} for {@code kind}, with {@code properties}
     * (start from {@link PortalBlock#defaultProperties}), and returns the handle everything
     * else goes through. Call once, at init.
     */
    public static Portals register(String modId, String path, PortalKind kind,
            BlockBehaviour.Properties properties) {
        ResourceKey<Block> key =
                ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(modId, path));
        Portals portals = new Portals(kind, key, properties);
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, newChunk) ->
                portals.frames(level.getServer()).onChunkLoad(level, chunk.getPos()));
        return portals;
    }

    /** The kind these portals are. */
    public PortalKind kind() {
        return kind;
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

    /** The lit portal whose interior holds {@code pos}, if any. */
    public @Nullable PortalFrame portalAt(ServerLevel level, BlockPos pos) {
        return frames(level.getServer()).portalAt(level.dimension(), pos);
    }

    /** The lit portal whose interior or ring holds {@code pos}, if any. */
    public @Nullable PortalFrame portalOwning(ServerLevel level, BlockPos pos) {
        return frames(level.getServer()).portalOwning(level.dimension(), pos);
    }

    /**
     * Lights {@code frame}, a complete frame: fills its interior and tells the kind. A portal
     * already standing there is faded first.
     */
    public void light(MinecraftServer server, PortalFrame frame) {
        frames(server).light(server, frame);
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
     * the arrival through {@link PortalKind#onExit}.
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

    private LitFrames frames(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(type);
    }
}
