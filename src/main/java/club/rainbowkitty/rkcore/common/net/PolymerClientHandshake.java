package club.rainbowkitty.rkcore.common.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;

import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.Nullable;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * A per-connection "does this client also run my mod" flag, from Polymer's networking handshake,
 * plus an optional set of named capability keys for content the mod only registers on some
 * installations.
 *
 * <p>{@link #create} makes one per mod id; {@link #register()} (call from {@code onInitialize},
 * which runs on both sides) adds a capability id to the set the client advertises during the
 * handshake, and the server then reports a version {@code >= 0} for connections whose client also
 * loaded the mod. The payload carries nothing and is never sent — the id exists only as a marker.
 *
 * <p>{@link #register(String)} adds one further marker per key, under
 * {@code <modId>:handshake/<key>}, and {@link #supports} answers it per connection. Each side must
 * register only the keys it actually has content for: the difference between the two sides' key
 * sets is the entire signal, so a mod whose registered content varies by installation — an optional
 * dependency, a config toggle — has to derive its keys from what it really registered rather than
 * from a fixed list.
 *
 * <p>A client-optional Polymer mod uses this to send real content (the real block state, no virtual
 * entity) to clients that can render it, and a substitute to everyone else. Sending real content
 * for a key the client never registered is not a cosmetic error: the client cannot resolve the
 * identifier, and the packet carrying it fails to decode.
 *
 * <p>Keys are entries in the handshake's own version map, which travels inside a custom payload
 * capped at 32767 bytes. That is roomy for tens of keys alongside Polymer's own ids, and the wrong
 * mechanism for thousands.
 */
public final class PolymerClientHandshake {
    private final String modId;
    private final CustomPacketPayload.Type<Payload> type;
    // Written only from onInitialize but read from network threads, so the map publishes the keys
    // safely without a separate "registration has finished" handoff.
    private final Map<String, CustomPacketPayload.Type<Payload>> keyTypes =
            new ConcurrentHashMap<>();

    private PolymerClientHandshake(String modId) {
        this.modId = modId;
        this.type = PolymerNetworking.id(modId, "handshake");
    }

    /** A handshake for {@code <modId>:handshake}. */
    public static PolymerClientHandshake create(String modId) {
        return new PolymerClientHandshake(modId);
    }

    /** Registers the capability id. Call once from {@code onInitialize}. */
    public void register() {
        registerMarker(type);
    }

    /**
     * Registers one capability key, {@code <modId>:handshake/<key>}. Call from
     * {@code onInitialize}, once per key this side actually registered content for.
     *
     * @param key an identifier path segment; an invalid one throws here, at startup, rather than
     *     silently never matching on the wire
     */
    public void register(String key) {
        keyTypes.computeIfAbsent(key, path -> {
            CustomPacketPayload.Type<Payload> keyType =
                    PolymerNetworking.id(modId, "handshake/" + path);
            registerMarker(keyType);
            return keyType;
        });
    }

    /** Whether this connection's client also loaded the mod. */
    public boolean has(ServerGamePacketListenerImpl handler) {
        return PolymerServerNetworking.getSupportedVersion(handler, type.id()) >= 0;
    }

    /** Whether the context's client also loaded the mod; {@code false} for a null context. */
    public boolean has(@Nullable PacketContext context) {
        return versionFor(context, type) >= 0;
    }

    /**
     * Whether this connection's client also loaded the mod <em>and</em> registered {@code key}.
     * A key this side never registered is {@code false}, so an unknown key can never be mistaken
     * for a universally supported one.
     */
    public boolean supports(ServerGamePacketListenerImpl handler, String key) {
        CustomPacketPayload.Type<Payload> keyType = keyTypes.get(key);
        return keyType != null && has(handler)
                && PolymerServerNetworking.getSupportedVersion(handler, keyType.id()) >= 0;
    }

    /** As {@link #supports(ServerGamePacketListenerImpl, String)}; false for a null context. */
    public boolean supports(@Nullable PacketContext context, String key) {
        CustomPacketPayload.Type<Payload> keyType = keyTypes.get(key);
        return keyType != null && has(context) && versionFor(context, keyType) >= 0;
    }

    /**
     * Whether this connection's client registered <em>every</em> key this side did — that is,
     * whether its content is a superset of ours and it can therefore decode anything we send.
     * Equivalent to {@link #has} when no keys are registered.
     *
     * <p>This, not the per-key {@link #supports}, is usually the right gate for deciding whether to
     * send real content. Anything sent by raw registry id — Polymer's own item and block sync
     * included — is only decodable when both sides agree on that id, and one absent mod shifts
     * every id after it, so a per-key answer is not enough to make a single key safe.
     *
     * <p>It is not <em>sufficient</em> for raw-id content either, and a caller sending any should
     * know why. Key parity does not imply id parity: Fabric's entrypoint order is hash-shuffled per
     * launch rather than sorted, and Polymer orders each registry's own entries by registration, so
     * two sides running the <em>same</em> mod set can still disagree about every id after the first
     * mod that registered in a different order. Observed, not theorised — it kicked a matching dev
     * client on roughly half of all server launches, and only on the launches where the orders
     * differed. Closing it needs an id fingerprint negotiated as a key, so that a reordering fails
     * this gate instead of corrupting the stream.
     */
    public boolean supportsAll(@Nullable PacketContext context) {
        return has(context) && allKeysSupported(keyType -> versionFor(context, keyType));
    }

    /** As {@link #supportsAll(PacketContext)}, for a play-phase connection. */
    public boolean supportsAll(ServerGamePacketListenerImpl handler) {
        return has(handler) && allKeysSupported(
                keyType -> PolymerServerNetworking.getSupportedVersion(handler, keyType.id()));
    }

    // True when every registered key negotiates a version >= 0 under the given lookup. Iterates the
    // values rather than re-entering supports(), which would re-check the base marker per key.
    private boolean allKeysSupported(ToIntFunction<CustomPacketPayload.Type<Payload>> versionOf) {
        for (CustomPacketPayload.Type<Payload> keyType : keyTypes.values()) {
            if (versionOf.applyAsInt(keyType) < 0) {
                return false;
            }
        }
        return true;
    }

    // Registers a never-sent marker: its presence in the handshake's version map is the whole
    // message, so the codec is never exercised.
    private static void registerMarker(CustomPacketPayload.Type<Payload> markerType) {
        PolymerNetworking.registerClientboundVersioned(
                markerType, 1, StreamCodec.unit(new Payload(markerType)));
    }

    // Negotiated version of one marker on the context's connection, or -1 when there is none.
    private static int versionFor(@Nullable PacketContext context,
            CustomPacketPayload.Type<Payload> markerType) {
        return context == null ? -1 : PolymerNetworking.getSupportedVersion(
                context.get(PacketContext.CONNECTION), markerType.id());
    }

    // The marker's payload type; instances exist only to satisfy the codec and are never sent.
    private record Payload(CustomPacketPayload.Type<Payload> type) implements CustomPacketPayload {}
}
