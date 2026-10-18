package club.rainbowkitty.kalic0re.common.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

// The fallback's store for player values: one persistent attachment per mod, saved with the
// player's data and kept through death, mapping each setting key to its value in that setting's
// own codec. Registered in every mode, since attachment types are fixed at initialization; only
// written without the hub.
final class PlayerValues {
    private static final Codec<Map<String, Dynamic<?>>> CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.PASSTHROUGH);

    private final AttachmentType<Map<String, Dynamic<?>>> type;

    PlayerValues(String modId) {
        this.type = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(modId, "settings"),
                builder -> builder.persistent(CODEC).copyOnDeath());
    }

    // The player's stored value for setting; empty when none is stored or the entry does not
    // decode, which is logged once per entry.
    <T> Optional<T> stored(ServerPlayer player, Setting<T> setting) {
        Map<String, Dynamic<?>> entries = target(player).getAttached(type);
        Dynamic<?> entry = entries == null ? null : entries.get(setting.key());
        if (entry == null) {
            return Optional.empty();
        }
        DataResult<T> decoded = setting.codec().parse(entry);
        decoded.error().ifPresent(error -> setting.reportOnce("stored:" + entry.getValue(),
                "Ignoring stored " + setting.id() + " of " + player.getScoreboardName() + ": "
                        + error.message()));
        return decoded.result();
    }

    // Whether player has an entry for any of keys.
    boolean hasAny(ServerPlayer player, Collection<String> keys) {
        Map<String, Dynamic<?>> entries = target(player).getAttached(type);
        return entries != null && keys.stream().anyMatch(entries::containsKey);
    }

    // value as setting's entry, in the setting's own codec.
    static <T> Dynamic<?> encode(Setting<T> setting, T value) {
        return new Dynamic<>(NbtOps.INSTANCE,
                setting.codec().encodeStart(NbtOps.INSTANCE, value).getOrThrow());
    }

    // Stores changes over player's entries, key by key.
    void put(ServerPlayer player, Map<String, Dynamic<?>> changes) {
        Map<String, Dynamic<?>> entries = copy(player);
        entries.putAll(changes);
        target(player).setAttached(type, Map.copyOf(entries));
    }

    // Drops the entries for keys, and the attachment once none is left.
    void remove(ServerPlayer player, Collection<String> keys) {
        Map<String, Dynamic<?>> entries = copy(player);
        entries.keySet().removeAll(keys);
        if (entries.isEmpty()) {
            target(player).removeAttached(type);
        } else {
            target(player).setAttached(type, Map.copyOf(entries));
        }
    }

    // A mutable copy of player's entries; the attached map itself is never changed in place.
    private Map<String, Dynamic<?>> copy(ServerPlayer player) {
        Map<String, Dynamic<?>> entries = target(player).getAttached(type);
        return entries == null ? new LinkedHashMap<>() : new LinkedHashMap<>(entries);
    }

    // The player as Fabric's attachment target, which Entity implements at runtime; Loom does
    // not inject that interface from kalic0re's compileOnly fabric-api.
    private static AttachmentTarget target(ServerPlayer player) {
        return (AttachmentTarget) player;
    }
}
