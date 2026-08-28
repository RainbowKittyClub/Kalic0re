package club.rainbowkitty.rkcore.books.hover;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Shows the title of the book under a player's crosshair while they look at a chiseled bookshelf
 * or an item frame.
 *
 * <p>Labels are Polymer virtual entities addressed to a single player, so nothing is spawned in
 * the world and vanilla clients need no install.
 */
public final class HoverTitles {
    private static final Map<UUID, HoverLabel> LABELS = new HashMap<>();

    private HoverTitles() {
    }

    /** Wires the per-tick scan and the disconnect cleanup. */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(HoverTitles::onEndTick);
        ServerPlayerEvents.LEAVE.register(HoverTitles::onLeave);
    }

    // Scans every online player once per tick.
    private static void onEndTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            update(player);
        }
    }

    // Shows, moves or hides one player's label to match what they are looking at.
    private static void update(ServerPlayer player) {
        Target target = findTarget(player);

        if (target == null) {
            HoverLabel existing = LABELS.get(player.getUUID());
            if (existing != null) {
                existing.hide();
            }
            return;
        }

        LABELS.computeIfAbsent(player.getUUID(), HoverLabel::new).show(player, target);
    }

    // Resolves the player's crosshair to a book, in a frame or on a shelf, or null if there is
    // none. A frame only counts when it is nearer than whatever block was hit, so a frame behind
    // a wall stays silent.
    private static Target findTarget(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return null;
        }

        // pick() clips to exactly the reach distance, so no further range filtering is needed.
        double range = player.blockInteractionRange();
        HitResult blockHit = player.pick(range, 1.0F, false);
        Vec3 eye = player.getEyePosition();
        double limit = blockHit.getType() == HitResult.Type.MISS
                ? range * range
                : eye.distanceToSqr(blockHit.getLocation());

        Target framed = findFramedBook(player, level, eye, range, limit);
        return framed != null ? framed : findShelvedBook(level, blockHit);
    }

    // The book in the item frame under the crosshair. Glow frames are ItemFrames too.
    private static Target findFramedBook(ServerPlayer player, ServerLevel level, Vec3 eye,
            double range, double limit) {
        Vec3 look = player.getViewVector(1.0F).scale(range);
        AABB search = player.getBoundingBox().expandTowards(look).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player, eye, eye.add(look), search,
                entity -> entity instanceof ItemFrame, limit);

        if (hit == null || !(hit.getEntity() instanceof ItemFrame frame)) {
            return null;
        }

        ItemStack book = frame.getItem();
        if (!book.typeHolder().is(ItemTags.BOOKSHELF_BOOKS)) {
            return null;
        }

        // A floor or ceiling frame has no facing to pin the text to, so it takes the viewer's
        // instead — labels are per-player, so that costs nothing.
        Direction facing = frame.getDirection();
        Direction plane = facing.getAxis().isHorizontal()
                ? facing
                : player.getDirection().getOpposite();

        Component title = BookTitle.of(book);
        return new Target(level, frame.position(), plane.toYRot(), title,
                LabelPlacement.frame(facing, title));
    }

    // The book in the bookshelf slot under the crosshair.
    private static Target findShelvedBook(ServerLevel level, HitResult result) {
        if (!(result instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChiseledBookShelfBlock shelf)) {
            return null;
        }

        // getHitSlot is empty unless the hit face is the shelf's front, which is the only side
        // with books on it, so this doubles as the "looking at the readable face" check.
        Direction facing = state.getValue(ChiseledBookShelfBlock.FACING);
        OptionalInt slot = shelf.getHitSlot(hit, facing);
        if (slot.isEmpty()) {
            return null;
        }

        if (!(level.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity shelfEntity)) {
            return null;
        }

        ItemStack book = shelfEntity.getItem(slot.getAsInt());
        if (book.isEmpty()) {
            return null;
        }

        int row = slot.getAsInt() / shelf.getColumns();
        Component title = BookTitle.of(book);
        return new Target(level, Vec3.atCenterOf(pos), facing.toYRot(), title,
                LabelPlacement.shelf(facing, row, title));
    }

    // Drops a disconnecting player's label so its holder is not leaked.
    private static void onLeave(ServerPlayer player) {
        HoverLabel label = LABELS.remove(player.getUUID());
        if (label != null) {
            label.hide();
        }
    }

    // The book a player is currently looking at, and where to hang its label.
    record Target(ServerLevel level, Vec3 anchor, float yaw, Component title, Vec3 offset) {
    }
}
