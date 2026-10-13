package club.rainbowkitty.kalic0re.common.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

// Every lit frame of one Portals, saved with the overworld, with an index from chunk to the
// frames whose ring or interior touches it, so a crossing or a block break finds its portal
// without a world read. A frame that died while its chunks were unloaded is cleared when they
// load.
final class LitFrames extends SavedData {
    private final Portals owner;
    private final Set<PortalFrame> portals = new HashSet<>();
    private final Map<ResourceKey<Level>, Long2ObjectMap<List<PortalFrame>>> byChunk =
            new HashMap<>();
    private final Map<ResourceKey<Level>, List<PortalFrame>> byDimension = new HashMap<>();
    // Dead frames whose blocks may still stand, in chunks that were not loaded when they died.
    private final Set<PortalFrame> pending = new HashSet<>();

    private LitFrames(Portals owner) {
        this.owner = owner;
    }

    private LitFrames(Portals owner, List<PortalFrame> lit, List<PortalFrame> pending) {
        this(owner);
        lit.forEach(this::add);
        this.pending.addAll(pending);
    }

    // The saved-data type for owner, under id; command storage is the vanilla type with no
    // fixer that reads into its contents.
    static SavedDataType<LitFrames> type(Portals owner, Identifier id) {
        Codec<LitFrames> codec = RecordCodecBuilder.create(instance -> instance
                .group(PortalFrame.CODEC.listOf().fieldOf("portals")
                                .forGetter(frames -> List.copyOf(frames.portals)),
                        PortalFrame.CODEC.listOf().fieldOf("pending")
                                .forGetter(frames -> List.copyOf(frames.pending)))
                .apply(instance, (lit, pending) -> new LitFrames(owner, lit, pending)));
        return new SavedDataType<>(id, () -> new LitFrames(owner), codec,
                DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
    }

    boolean has(PortalFrame frame) {
        return portals.contains(frame);
    }

    Set<PortalFrame> lit() {
        return Set.copyOf(portals);
    }

    // The lit frames in dimension, the live list.
    List<PortalFrame> inDimension(ResourceKey<Level> dimension) {
        return byDimension.getOrDefault(dimension, List.of());
    }

    // One tick of ambient particles for every lit frame in level: nothing when the kind has no
    // emitters, the level no portals or no players.
    void emit(ServerLevel level) {
        List<PortalEmitter> emitters = owner.emitters();
        List<PortalFrame> frames = inDimension(level.dimension());
        if (emitters.isEmpty() || frames.isEmpty() || level.players().isEmpty()) {
            return;
        }
        long time = level.getGameTime();
        for (PortalEmitter emitter : emitters) {
            if (emitter.due(time)) {
                for (PortalFrame frame : frames) {
                    emitter.burst(level, frame, time);
                }
            }
        }
    }

    // The lit frame whose interior holds pos, if any.
    @Nullable PortalFrame portalAt(ResourceKey<Level> dimension, BlockPos pos) {
        for (PortalFrame frame : candidates(dimension, pos)) {
            if (frame.interiorContains(pos)) {
                return frame;
            }
        }
        return null;
    }

    // The lit frame whose interior or ring holds pos, if any.
    @Nullable PortalFrame portalOwning(ResourceKey<Level> dimension, BlockPos pos) {
        for (PortalFrame frame : candidates(dimension, pos)) {
            if (frame.interiorContains(pos) || frame.ringContains(pos)) {
                return frame;
            }
        }
        return null;
    }

    // Lights frame: a record it already has is faded first, so a record whose blocks a command
    // cleared without the removal hook never keeps what the consumer tied to it; a frame no
    // longer complete by then is refused. Blocks are set without shape updates, as vanilla
    // lights a nether portal, or a half-filled interior would clear itself.
    boolean light(MinecraftServer server, PortalFrame frame) {
        if (portals.contains(frame)) {
            fade(server, frame);
        }
        ServerLevel level = server.getLevel(frame.dimension());
        if (level == null || !owner.finder().isComplete(level, frame)) {
            return false;
        }
        add(frame);
        setDirty();
        BlockState state = owner.block().stateFor(frame.plane());
        for (BlockPos pos : frame.interior()) {
            level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }
        owner.kind().onLit(level, frame);
        return true;
    }

    // Forgets frame, tells the kind, and clears its blocks on a following tick, or when its
    // chunks next load. Deferred so a removal that starts inside a block update touches no other
    // chunk from there.
    void fade(MinecraftServer server, PortalFrame frame) {
        if (!portals.remove(frame)) {
            return;
        }
        unindex(frame);
        pending.add(frame);
        setDirty();
        owner.kind().onFaded(server, server.getLevel(frame.dimension()), frame);
        server.schedule(new TickTask(server.getTickCount(), () -> clearIfDead(server, frame)));
    }

    // One block of the portal at pos is gone, so the portal is.
    void onBlockRemoved(ServerLevel level, BlockPos pos) {
        PortalFrame frame = portalAt(level.dimension(), pos);
        if (frame != null) {
            fade(level.getServer(), frame);
        }
    }

    // Dead frames in this chunk are cleared on a following tick, once the chunk is in the world.
    void onChunkLoad(ServerLevel level, ChunkPos chunk) {
        if (pending.isEmpty()) {
            return;
        }
        long key = chunk.pack();
        for (PortalFrame frame : pending) {
            if (frame.dimension().equals(level.dimension()) && touches(frame, key)) {
                level.getServer().schedule(new TickTask(level.getServer().getTickCount(),
                        () -> clearIfDead(level.getServer(), frame)));
            }
        }
    }

    // Clears the blocks of a dead frame whose chunks are loaded; one relit since stays, one in
    // unloaded chunks waits for them.
    private void clearIfDead(MinecraftServer server, PortalFrame frame) {
        if (portals.contains(frame)) {
            if (pending.remove(frame)) {
                setDirty();
            }
            return;
        }
        ServerLevel level = server.getLevel(frame.dimension());
        if (level == null || isLoaded(level, frame)) {
            if (pending.remove(frame)) {
                setDirty();
            }
            if (level != null) {
                unlight(level, frame);
            }
        }
    }

    private void add(PortalFrame frame) {
        portals.add(frame);
        byDimension.computeIfAbsent(frame.dimension(), dimension -> new ArrayList<>()).add(frame);
        Long2ObjectMap<List<PortalFrame>> chunks = byChunk.computeIfAbsent(frame.dimension(),
                dimension -> new Long2ObjectOpenHashMap<>());
        for (long chunk : frame.chunks()) {
            chunks.computeIfAbsent(chunk, key -> new ArrayList<>(1)).add(frame);
        }
    }

    private void unindex(PortalFrame frame) {
        List<PortalFrame> inDimension = byDimension.get(frame.dimension());
        if (inDimension != null) {
            inDimension.remove(frame);
        }
        Long2ObjectMap<List<PortalFrame>> chunks = byChunk.get(frame.dimension());
        if (chunks == null) {
            return;
        }
        for (long chunk : frame.chunks()) {
            List<PortalFrame> inChunk = chunks.get(chunk);
            if (inChunk != null && inChunk.remove(frame) && inChunk.isEmpty()) {
                chunks.remove(chunk);
            }
        }
    }

    private List<PortalFrame> candidates(ResourceKey<Level> dimension, BlockPos pos) {
        Long2ObjectMap<List<PortalFrame>> chunks = byChunk.get(dimension);
        List<PortalFrame> inChunk = chunks == null ? null : chunks.get(ChunkPos.pack(pos));
        return inChunk == null ? List.of() : inChunk;
    }

    // Destroyed, not set to air, so every block shatters as the ones the cascade takes do.
    private void unlight(ServerLevel level, PortalFrame frame) {
        for (BlockPos pos : frame.interior()) {
            if (owner.block().isPortal(level.getBlockState(pos), frame.plane())) {
                level.destroyBlock(pos, false);
            }
        }
    }

    private static boolean touches(PortalFrame frame, long chunk) {
        for (long candidate : frame.chunks()) {
            if (candidate == chunk) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLoaded(ServerLevel level, PortalFrame frame) {
        for (long chunk : frame.chunks()) {
            if (!level.hasChunk(ChunkPos.getX(chunk), ChunkPos.getZ(chunk))) {
                return false;
            }
        }
        return true;
    }
}
