package club.rainbowkitty.kalic0re.common.portal;

import java.util.BitSet;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Finds frames of one {@link PortalKind}: around an interior block, or beside a frame block a
 * player clicked. An interior is any region of air joined at its faces within the plane, and its
 * ring every block a face away, so ring blocks may meet at faces or only at edges.
 */
public final class FrameFinder {
    private static final int[] STEP_U = {1, -1, 0, 0};
    private static final int[] STEP_V = {0, 0, 1, -1};

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

    /** {@link #find(Level, BlockPos, PortalPlane, int)} up to {@link PortalFrame#MAX_SIZE}. */
    public Optional<PortalFrame> find(Level level, BlockPos interiorPos, PortalPlane plane) {
        return find(level, interiorPos, plane, PortalFrame.MAX_SIZE);
    }

    /**
     * The frame in {@code plane} whose interior holds {@code interiorPos}: every block reached
     * from it a face at a time through air (or portal of that plane), at most {@code maxSize}
     * across on either in-plane axis, with a frame block on every face that leads out of it.
     */
    public Optional<PortalFrame> find(Level level, BlockPos interiorPos, PortalPlane plane,
            int maxSize) {
        int limit = Math.min(maxSize, PortalFrame.MAX_SIZE);
        if (limit < 1 || !isInterior(level.getBlockState(interiorPos), plane)) {
            return Optional.empty();
        }
        // Cells by offset from interiorPos: no interior cell lies further than limit - 1 away.
        int span = 2 * limit - 1;
        int reach = limit - 1;
        BitSet seen = new BitSet(span * span);
        int[] queue = new int[limit * limit];
        int head = 0;
        int tail = 0;
        int minU = 0;
        int maxU = 0;
        int minV = 0;
        int maxV = 0;
        seen.set(reach * span + reach);
        queue[tail++] = reach * span + reach;
        Direction along = Direction.get(Direction.AxisDirection.POSITIVE, plane.along());
        Direction up = Direction.get(Direction.AxisDirection.POSITIVE, plane.up());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        while (head < tail) {
            int cell = queue[head++];
            int u = cell % span - reach;
            int v = cell / span - reach;
            for (int i = 0; i < STEP_U.length; i++) {
                int nu = u + STEP_U[i];
                int nv = v + STEP_V[i];
                boolean inReach = Math.abs(nu) <= reach && Math.abs(nv) <= reach;
                int next = (nv + reach) * span + nu + reach;
                if (inReach && seen.get(next)) {
                    continue;
                }
                pos.set(interiorPos).move(along, nu).move(up, nv);
                BlockState state = level.getBlockState(pos);
                if (!isInterior(state, plane)) {
                    if (!frameBlock.test(state)) {
                        return Optional.empty();
                    }
                    continue;
                }
                minU = Math.min(minU, nu);
                maxU = Math.max(maxU, nu);
                minV = Math.min(minV, nv);
                maxV = Math.max(maxV, nv);
                if (maxU - minU >= limit || maxV - minV >= limit) {
                    return Optional.empty();
                }
                seen.set(next);
                queue[tail++] = next;
            }
        }
        int width = maxU - minU + 1;
        BitSet cells = new BitSet(width * (maxV - minV + 1));
        for (int i = seen.nextSetBit(0); i >= 0; i = seen.nextSetBit(i + 1)) {
            cells.set((i / span - reach - minV) * width + i % span - reach - minU);
        }
        BlockPos origin = interiorPos.relative(plane.along(), minU).relative(plane.up(), minV);
        return Optional.of(new PortalFrame(level.dimension(), origin, plane, width,
                maxV - minV + 1, cells));
    }

    /** Whether {@code frame} stands: every interior block interior, every ring block frame. */
    public boolean isComplete(LevelReader level, PortalFrame frame) {
        return hasRing(level, frame) && interiorIs(level, frame, false);
    }

    /** Whether {@code frame} stands lit: a complete ring around nothing but portal blocks. */
    public boolean isLit(LevelReader level, PortalFrame frame) {
        return hasRing(level, frame) && interiorIs(level, frame, true);
    }

    /** {@link #findAround(Level, BlockPos, Direction, int)} up to {@link PortalFrame#MAX_SIZE}. */
    public Optional<PortalFrame> findAround(Level level, BlockPos framePos,
            Direction clickedFace) {
        return findAround(level, framePos, clickedFace, PortalFrame.MAX_SIZE);
    }

    /**
     * The frame beside the frame block at {@code framePos}, at most {@code maxSize} across: the
     * block the clicked face looks at is tried first, then the other five neighbours, each in the
     * planes that span that direction, upright before flat. The first frame found wins.
     */
    public Optional<PortalFrame> findAround(Level level, BlockPos framePos,
            Direction clickedFace, int maxSize) {
        Optional<PortalFrame> found = findBeside(level, framePos, clickedFace, maxSize);
        for (Direction direction : Direction.values()) {
            if (found.isEmpty() && direction != clickedFace) {
                found = findBeside(level, framePos, direction, maxSize);
            }
        }
        return found;
    }

    // The frame whose interior starts one block from framePos toward toInterior, in any plane.
    private Optional<PortalFrame> findBeside(Level level, BlockPos framePos,
            Direction toInterior, int maxSize) {
        BlockPos pos = framePos.relative(toInterior);
        for (PortalPlane plane : PortalPlane.spanning(toInterior.getAxis())) {
            Optional<PortalFrame> frame = find(level, pos, plane, maxSize);
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

    // Air, or an existing portal block of the same plane, so a lit frame is found again.
    private boolean isInterior(BlockState state, PortalPlane plane) {
        return state.isAir() || portalBlock.isPortal(state, plane);
    }
}
