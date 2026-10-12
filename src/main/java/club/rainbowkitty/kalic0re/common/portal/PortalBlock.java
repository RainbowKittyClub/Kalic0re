package club.rainbowkitty.kalic0re.common.portal;

import java.util.Map;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The block a lit portal's interior is made of, one per {@link Portals}. Vanilla's own portal
 * processing carries an entity through: the block only marks the entity as inside, and asks its
 * kind for the destination once the entity has stood in it long enough.
 *
 * <p>Every client sees an invisible light block of the same light level in its place, and the
 * kind's {@link PortalMaterial} drawn over it by a display per block ({@link PortalHolder}).
 */
public final class PortalBlock extends Block implements Portal, PolymerBlock,
        BlockWithElementHolder {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    private static final int LIGHT = 11;
    private static final Map<Direction.Axis, VoxelShape> SHAPES = Map.of(
            Direction.Axis.X, Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0),
            Direction.Axis.Z, Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0),
            Direction.Axis.Y, Block.box(0.0, 6.0, 0.0, 16.0, 10.0, 16.0));
    private static final BlockState[] STAND_INS = new BlockState[LightBlock.MAX_LEVEL + 1];

    static {
        for (int level = 0; level < STAND_INS.length; level++) {
            STAND_INS[level] = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, level);
        }
    }

    private final Portals owner;

    PortalBlock(Properties properties, Portals owner) {
        super(properties);
        this.owner = owner;
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    /**
     * The nether portal's properties: no collision, unbreakable in survival, light 11, a wall to
     * pistons, nothing to drop, no random ticks. {@link Portals#register} sets the id.
     */
    public static Properties defaultProperties() {
        return BlockBehaviour.Properties.of().noCollision().strength(-1.0F)
                .sound(SoundType.GLASS).lightLevel(state -> LIGHT).pushReaction(PushReaction.BLOCK)
                .noLootTable();
    }

    /** Whether {@code state} is this block lying in {@code plane}. */
    public boolean isPortal(BlockState state, PortalPlane plane) {
        return state.is(this) && state.getValue(AXIS) == plane.axis();
    }

    /** This block's state for a portal in {@code plane}. */
    public BlockState stateFor(PortalPlane plane) {
        return defaultBlockState().setValue(AXIS, plane.axis());
    }

    @Override
    protected MapCodec<PortalBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return SHAPES.get(state.getValue(AXIS));
    }

    // Air once an in-plane neighbour is neither portal of this plane nor frame block; the rest
    // of the interior then cascades after it, as a nether portal's does.
    @Override
    protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos,
            BlockState neighbourState, RandomSource random) {
        PortalPlane plane = PortalPlane.of(state.getValue(AXIS));
        if (!plane.spans(direction.getAxis()) || isPortal(neighbourState, plane)
                || owner.kind().isFrameBlock(neighbourState)) {
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos,
            boolean movedByPiston) {
        owner.onBlockRemoved(level, pos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
            InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        return owner.kind().transitionTime(level, entity);
    }

    @Override
    public @Nullable TeleportTransition getPortalDestination(ServerLevel level, Entity entity,
            BlockPos entryPos) {
        return owner.destination(level, entity, entryPos);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state,
            boolean includeData) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return STAND_INS[state.getLightEmission()];
    }

    // The stand-in is invisible and breaks in silence; the material's block is what shatters.
    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, PacketContext context) {
        return owner.kind().material().plain();
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel level, BlockPos pos,
            BlockState state) {
        return new PortalHolder(owner.kind().material(), PortalPlane.of(state.getValue(AXIS)));
    }
}
