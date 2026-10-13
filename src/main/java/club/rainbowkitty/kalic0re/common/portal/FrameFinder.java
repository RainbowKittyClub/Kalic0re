package club.rainbowkitty.kalic0re.common.portal;

import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Finds frames of one {@link PortalKind}: around an interior block, or beside a frame block a
 * player clicked.
 */
public final class FrameFinder {
    private final Predicate<BlockState> frameBlock;
    private final PortalBlock portalBlock;

    FrameFinder(Predicate<BlockState> frameBlock, PortalBlock portalBlock) {
        this.frameBlock = frameBlock;
        this.portalBlock = portalBlock;
    }

    /** Whether {@code state} may be part of a frame. */
    public boolean isFrameBlock(BlockState state) {
        return frameBlock.test(state);
    }

    /**
     * The frame in {@code plane} whose interior holds {@code interiorPos}: the rectangle of air
     * (or portal of that plane) around it, at most {@link PortalFrame#MAX_SIZE} each way, with a
     * complete ring of frame blocks.
     */
    public Optional<PortalFrame> find(Level level, BlockPos interiorPos, PortalPlane plane) {
        if (!isInterior(level.getBlockState(interiorPos), plane)) {
            return Optional.empty();
        }
        int back = run(level, interiorPos, plane, plane.along(), -1);
        int forward = run(level, interiorPos, plane, plane.along(), 1);
        int down = run(level, interiorPos, plane, plane.up(), -1);
        int upward = run(level, interiorPos, plane, plane.up(), 1);
        int width = back + 1 + forward;
        int height = down + 1 + upward;
        if (width > PortalFrame.MAX_SIZE || height > PortalFrame.MAX_SIZE) {
            return Optional.empty();
        }
        BlockPos origin = interiorPos.relative(plane.along(), -back).relative(plane.up(), -down);
        PortalFrame frame = new PortalFrame(level.dimension(), origin, plane, width, height);
        return isComplete(level, frame) ? Optional.of(frame) : Optional.empty();
    }

    /** Whether {@code frame} stands: every interior block interior, every ring block frame. */
    public boolean isComplete(LevelReader level, PortalFrame frame) {
        return hasRing(level, frame) && interiorIs(level, frame, false);
    }

    /** Whether {@code frame} stands lit: a complete ring around nothing but portal blocks. */
    public boolean isLit(LevelReader level, PortalFrame frame) {
        return hasRing(level, frame) && interiorIs(level, frame, true);
    }

    /**
     * The frame beside the frame block at {@code framePos}: the block the clicked face looks at
     * is tried first, then the other five neighbours, each in the planes that span that direction,
     * upright before flat. The first complete frame wins.
     */
    public Optional<PortalFrame> findAround(Level level, BlockPos framePos,
            Direction clickedFace) {
        Optional<PortalFrame> found = findBeside(level, framePos, clickedFace);
        for (Direction direction : Direction.values()) {
            if (found.isEmpty() && direction != clickedFace) {
                found = findBeside(level, framePos, direction);
            }
        }
        return found;
    }

    // The frame whose interior starts one block from framePos toward toInterior, in any plane.
    private Optional<PortalFrame> findBeside(Level level, BlockPos framePos,
            Direction toInterior) {
        BlockPos pos = framePos.relative(toInterior);
        for (PortalPlane plane : PortalPlane.spanning(toInterior.getAxis())) {
            Optional<PortalFrame> frame = find(level, pos, plane);
            if (frame.isPresent()) {
                return frame;
            }
        }
        return Optional.empty();
    }

    private boolean hasRing(LevelReader level, PortalFrame frame) {
        for (BlockPos pos : frame.ring()) {
            if (!frameBlock.test(level.getBlockState(pos))) {
                return false;
            }
        }
        return true;
    }

    // Whether every interior block is portal (litOnly) or air-or-portal.
    private boolean interiorIs(LevelReader level, PortalFrame frame, boolean litOnly) {
        for (BlockPos pos : frame.interior()) {
            BlockState state = level.getBlockState(pos);
            boolean portal = portalBlock.isPortal(state, frame.plane());
            if (!(portal || !litOnly && state.isAir())) {
                return false;
            }
        }
        return true;
    }

    // How many interior blocks follow `from` along `axis` in the direction of `sign`, at most
    // MAX_SIZE.
    private int run(Level level, BlockPos from, PortalPlane plane, Direction.Axis axis, int sign) {
        Direction.AxisDirection toward =
                sign < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE;
        Direction step = Direction.get(toward, axis);
        BlockPos.MutableBlockPos pos = from.mutable();
        int count = 0;
        while (count < PortalFrame.MAX_SIZE
                && isInterior(level.getBlockState(pos.move(step)), plane)) {
            count++;
        }
        return count;
    }

    // Air, or an existing portal block of the same plane, so a lit frame is found again.
    private boolean isInterior(BlockState state, PortalPlane plane) {
        return state.isAir() || portalBlock.isPortal(state, plane);
    }
}
