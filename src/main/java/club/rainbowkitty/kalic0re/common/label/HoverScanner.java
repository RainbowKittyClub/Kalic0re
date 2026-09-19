package club.rainbowkitty.kalic0re.common.label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import club.rainbowkitty.kalic0re.common.aim.Crosshair;

/**
 * Shows one player-private label over whatever a player has under their crosshair.
 *
 * <p>Resolves each player's crosshair once per tick and offers the hit to the registered
 * providers, so a feature only has to say what it labels and how, not how to find it.
 */
public final class HoverScanner {
    private static final Map<UUID, HoverLabel> LABELS = new HashMap<>();
    private static final List<EntityLabels> ENTITY_PROVIDERS = new ArrayList<>();
    private static final List<BlockLabels> BLOCK_PROVIDERS = new ArrayList<>();

    private HoverScanner() {
    }

    /** Wires the per-tick scan and the disconnect cleanup. */
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(HoverScanner::onEndTick);
        ServerPlayerEvents.LEAVE.register(HoverScanner::onLeave);
    }

    /** Adds an entity provider. Providers are offered a hit in registration order. */
    public static void register(EntityLabels provider) {
        ENTITY_PROVIDERS.add(provider);
    }

    /** Adds a block provider. Providers are offered a hit in registration order. */
    public static void register(BlockLabels provider) {
        BLOCK_PROVIDERS.add(provider);
    }

    private static void onEndTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            update(player);
        }
    }

    // Shows, moves or hides one player's label to match what they are looking at.
    private static void update(ServerPlayer player) {
        LabelTarget target = findTarget(player);

        if (target == null) {
            HoverLabel existing = LABELS.get(player.getUUID());
            if (existing != null) {
                existing.hide();
            }
            return;
        }

        LABELS.computeIfAbsent(player.getUUID(), HoverLabel::new).show(player, target);
    }

    private static LabelTarget findTarget(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return null;
        }

        Crosshair.Hit hit = Crosshair.of(player, HoverScanner::handled);
        LabelTarget entity = hit.entity() == null ? null : fromEntity(level, hit.entity());

        return entity != null ? entity : fromBlock(level, hit.block());
    }

    private static LabelTarget fromEntity(ServerLevel level, EntityHitResult hit) {
        // Only the nearest handled entity is considered: something further along the ray is not
        // what the player is pointing at, even if the near one turns out to have no label.
        for (EntityLabels provider : ENTITY_PROVIDERS) {
            if (!provider.handles(hit.getEntity())) {
                continue;
            }
            LabelTarget target = provider.target(level, hit);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    private static boolean handled(Entity entity) {
        for (EntityLabels provider : ENTITY_PROVIDERS) {
            if (provider.handles(entity)) {
                return true;
            }
        }
        return false;
    }

    private static LabelTarget fromBlock(ServerLevel level, HitResult result) {
        if (!(result instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        for (BlockLabels provider : BLOCK_PROVIDERS) {
            LabelTarget target = provider.target(level, hit);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    // Drops a disconnecting player's label so its holder is not leaked.
    private static void onLeave(ServerPlayer player) {
        HoverLabel label = LABELS.remove(player.getUUID());
        if (label != null) {
            label.hide();
        }
    }
}
