package club.rainbowkitty.kalic0re.common.portal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import it.unimi.dsi.fastutil.ints.IntList;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

/**
 * One interior block's look: an item display of the material's item for a watcher with the
 * server's resource pack, a block display of its block state for any other. Each watcher is sent
 * exactly one of the two, remembered until it stops watching. Both lie as the X plane's slab and
 * are turned together for the other planes. Nothing here ever changes, so the holder need not tick.
 */
final class MaterialElement implements VirtualElement {
    private static final Vector3f BLOCK_CENTRE = new Vector3f(0.5F, 0.5F, 0.5F);
    // Drawn as a nether portal's own light of 11 draws it under open sky, whatever light the
    // portal block gives, so a dim portal keeps its look. A display with a brightness of its own
    // is also no light source to LambDynamicLights, which otherwise lights each display by what
    // it shows: a nether portal at 11, the material's item at its own.
    private static final Brightness SURFACE = new Brightness(11, 15);

    private final ItemDisplayElement packed;
    private final BlockDisplayElement plain;
    private final Set<ServerPlayer> shownPacked = new HashSet<>();

    MaterialElement(PortalMaterial material, PortalPlane plane) {
        Quaternionf turn = turn(plane);
        // An item model is drawn about the entity's position; a block model from it as the
        // model's origin corner, so that one is turned about its own centre and moved back.
        packed = new ItemDisplayElement(material.packed());
        packed.setItemDisplayContext(ItemDisplayContext.NONE);
        packed.setLeftRotation(turn);
        packed.setBrightness(SURFACE);
        plain = new BlockDisplayElement(material.plain());
        plain.setLeftRotation(turn);
        plain.setBrightness(SURFACE);
        plain.setTranslation(turn.transform(new Vector3f(BLOCK_CENTRE)).negate());
    }

    @Override
    public IntList getEntityIds() {
        return IntList.of(packed.getEntityId(), plain.getEntityId());
    }

    @Override
    public void setHolder(@Nullable ElementHolder holder) {
        packed.setHolder(holder);
        plain.setHolder(holder);
    }

    @Override
    public @Nullable ElementHolder getHolder() {
        return packed.getHolder();
    }

    @Override
    public Vec3 getOffset() {
        return packed.getOffset();
    }

    @Override
    public void setOffset(Vec3 offset) {
        packed.setOffset(offset);
        plain.setOffset(offset);
    }

    @Override
    public void setInitialPosition(Vec3 position) {
        packed.setInitialPosition(position);
        plain.setInitialPosition(position);
    }

    @Override
    public void startWatching(ServerPlayer player,
            Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        if (PolymerResourcePackUtils.hasMainPack(player.connection)) {
            shownPacked.add(player);
            packed.startWatching(player, packetConsumer);
        } else {
            plain.startWatching(player, packetConsumer);
        }
    }

    @Override
    public void stopWatching(ServerPlayer player,
            Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        (shownPacked.remove(player) ? packed : plain).stopWatching(player, packetConsumer);
    }

    @Override
    public void notifyMove(Vec3 oldPos, Vec3 currentPos, Vec3 delta) {
        packed.notifyMove(oldPos, currentPos, delta);
        plain.notifyMove(oldPos, currentPos, delta);
    }

    @Override
    public void tick() {
        packed.tick();
        plain.tick();
    }

    @Override
    public InteractionHandler getInteractionHandler(ServerPlayer player) {
        return InteractionHandler.EMPTY;
    }

    // The rotation that lays the X plane's slab into `plane`.
    private static Quaternionf turn(PortalPlane plane) {
        return switch (plane) {
            case X -> new Quaternionf();
            case Z -> new Quaternionf().rotationY((float) Math.toRadians(90.0));
            case Y -> new Quaternionf().rotationX((float) Math.toRadians(90.0));
        };
    }
}
