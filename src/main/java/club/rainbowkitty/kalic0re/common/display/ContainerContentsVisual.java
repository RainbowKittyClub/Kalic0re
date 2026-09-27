package club.rainbowkitty.kalic0re.common.display;

import java.util.Objects;

import com.mojang.math.Axis;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Where and how each slot of a container is drawn as an item: the geometry behind
 * {@link ContainerContentsHolder}, independent of which holder shows it.
 *
 * <p>An item whose inventory icon is a flat sprite is drawn as that icon, its 16-pixel canvas
 * {@code spriteSize} wide. That covers a trident or spyglass too, which a hand holds as a model of
 * its own. Any other item is drawn as a hand holds it, at {@code blockSize} per block of model.
 * Every item stands upright with its front toward whoever the facing came from, its front being
 * where its {@code fixed} display rotation says ({@link ItemModelExtents.Extents#frontYaw}); an
 * icon's is its face. {@link ItemModelExtents}'s box then centres the item on its slot's anchor,
 * resting on it or floating around it. An icon's box is its whole canvas, so a sprite with empty
 * rows at the bottom stands that much above the anchor.
 *
 * <p>Icons and models may each be billboarded, turned by the client to face whoever looks at
 * them: {@code CENTER} faces the viewer outright, {@code VERTICAL} turns about the upright axis
 * only. A billboarded item's front faces the viewer rather than the facing's player, and it turns
 * about its own centre, which stands where an upright item's would. Turned toward a viewer above
 * it, an item's lower corners can dip below that, so a billboarded layout wants a little height
 * above any surface. A cube billboarded {@code CENTER} only ever shows its front face.
 *
 * <p>An icon is drawn in {@code ItemDisplayContext.GUI}, anything else in {@code NONE}. The client
 * centres the model on the entity, 16 units to a block, before the renderer's own
 * {@link ItemDisplayPose#RENDERER_YAW_OFFSET} turn. {@code NONE} applies no display transform.
 * {@code GUI} applies the icon's own, as an inventory slot does: none for nearly every sprite, a
 * nudge that centres an amethyst bud.
 *
 * @param layout each slot's anchor
 * @param blockSize the size one block of a standing model is drawn at, in blocks
 * @param spriteSize the width an icon's canvas is drawn at, in blocks
 * @param placement whether an item rests on its anchor or floats centred around it
 * @param iconBillboard how an item drawn as its icon turns to its viewer; {@code FIXED} for not
 * @param modelBillboard how any other item turns to its viewer; {@code FIXED} for not
 */
public record ContainerContentsVisual(SlotLayout layout, float blockSize, float spriteSize,
        Placement placement, Display.BillboardConstraints iconBillboard,
        Display.BillboardConstraints modelBillboard) {
    /** How far above a surface a resting item is drawn, so it never ties on depth with it. */
    public static final float SURFACE_LIFT = 1.0f / 512.0f;

    /**
     * The pose one slot's item takes.
     *
     * @param slot the slot index
     * @param facing the horizontal direction the container's layout is turned to
     * @param stack the slot's item, not empty
     * @return the element's offset from the holder, and its transformation
     */
    public Pose pose(int slot, Direction facing, ItemStack stack) {
        ItemModelExtents.Extents icon = ItemModelExtents.of(stack, ItemDisplayContext.GUI);
        boolean asIcon = icon != null && icon.flat();
        ItemDisplayContext context = asIcon ? ItemDisplayContext.GUI : ItemDisplayContext.NONE;
        ItemModelExtents.Extents box = asIcon ? icon : Objects.requireNonNullElse(
                ItemModelExtents.of(stack, context), ItemModelExtents.FULL_BLOCK);
        float size = box.flat() ? spriteSize : blockSize;
        Display.BillboardConstraints billboard = box.flat() ? iconBillboard : modelBillboard;
        boolean billboarded = billboard != Display.BillboardConstraints.FIXED;
        // A billboard's frame turns so that its +z points at the viewer, as the world's own does at
        // a player facing north.
        Direction toward = billboarded ? Direction.NORTH : facing;
        Quaternionf rotation = Axis.YP.rotationDegrees(
                ItemDisplayPose.RENDERER_YAW_OFFSET - toward.toYRot() + box.frontYaw());
        Vector3f min = new Vector3f(Float.MAX_VALUE);
        Vector3f max = new Vector3f(-Float.MAX_VALUE);
        for (int corner = 0; corner < 8; corner++) {
            float x = (corner & 1) == 0 ? box.minX() : box.maxX();
            float y = (corner & 2) == 0 ? box.minY() : box.maxY();
            float z = (corner & 4) == 0 ? box.minZ() : box.maxZ();
            // Centred on the entity, then turned by the renderer, then scaled and rotated here.
            Vector3f point = new Vector3f(-(x / 16.0f - 0.5f), y / 16.0f - 0.5f,
                    -(z / 16.0f - 0.5f)).mul(size).rotate(rotation);
            min.min(point);
            max.max(point);
        }
        Vector3f centre = new Vector3f(min).add(max).mul(0.5f);
        Vec3 anchor = layout.anchor(slot, facing);
        if (billboarded) {
            // The client turns the item about the element's origin, so that is its centre.
            float rise = placement == Placement.RESTING ? (max.y - min.y) / 2.0f : 0.0f;
            return new Pose(anchor.add(0, rise, 0), context, billboard, rotation, size,
                    centre.negate());
        }
        float lift = placement == Placement.RESTING ? -min.y : -centre.y;
        Vector3f translation = new Vector3f(-centre.x, lift, -centre.z);
        return new Pose(anchor, context, billboard, rotation, size, translation);
    }

    /** How an item sits against its slot's anchor. */
    public enum Placement {
        /** The item's lowest point is on the anchor, as if set down on a surface there. */
        RESTING,
        /** The item's box is centred on the anchor. */
        FLOATING
    }

    /** Each slot's anchor, the point its item is placed against. */
    @FunctionalInterface
    public interface SlotLayout {
        /**
         * A slot's anchor.
         *
         * @param slot the slot index
         * @param facing the horizontal direction the layout is turned to
         * @return the anchor's offset from the holder's origin, a block's centre for a block
         */
        public Vec3 anchor(int slot, Direction facing);

        /**
         * A grid of slots on a block's top face, read as a menu's grid reads to someone looking
         * along {@code facing}: slot {@code column + columns * row}, row 0 on the far side and
         * column 0 on the left.
         *
         * @param columns slots per row
         * @param rows row count
         * @param pitch distance between neighbouring slots' anchors, in blocks
         * @param height how far above the face the anchors sit, in blocks
         * @return the layout, centred on the face, {@link #SURFACE_LIFT} above that height
         */
        public static SlotLayout topGrid(int columns, int rows, double pitch, double height) {
            return (slot, facing) -> {
                double along = ((rows - 1) / 2.0 - slot / columns) * pitch;
                double across = (slot % columns - (columns - 1) / 2.0) * pitch;
                Direction right = facing.getClockWise();
                return new Vec3(
                        facing.getStepX() * along + right.getStepX() * across,
                        0.5 + height + SURFACE_LIFT,
                        facing.getStepZ() * along + right.getStepZ() * across);
            };
        }
    }

    /**
     * One item's pose.
     *
     * @param offset the element's offset from the holder's origin
     * @param context the display context the item is drawn in
     * @param billboard how the item turns to its viewer
     * @param leftRotation the transformation's left rotation
     * @param scale the transformation's uniform scale
     * @param translation the transformation's translation
     */
    public record Pose(Vec3 offset, ItemDisplayContext context,
            Display.BillboardConstraints billboard, Quaternionf leftRotation, float scale,
            Vector3f translation) {
    }
}
