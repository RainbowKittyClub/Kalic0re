package club.rainbowkitty.kalic0re.common.portal;

import java.util.Map;

//? if <26.3
import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The block a lit portal's interior is made of, one per {@link Portals}. Vanilla's own portal
 * processing carries an entity through: the block only marks the entity as inside, and asks its
 * kind for the destination once the entity has stood in it long enough.
 *
 * <p>Every client sees an invisible light block of the same light level in its place, or a barrier
 * where the portal is {@link #SEALED}, and the kind's {@link PortalMaterial} drawn over it by a
 * display per block ({@link PortalHolder}).
 */
public final class PortalBlock extends Block implements Portal, PolymerBlock,
        BlockWithElementHolder {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    /** Whether clients see a barrier here, not light, so none walks in ({@link Portals#seal}). */
    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    private static final int LIGHT = 11;
    // 26.3 renamed BLOCK to IMMOVEABLE.
    //? if >=26.3 {
    /*private static final PushReaction IMMOVABLE = PushReaction.IMMOVEABLE;
    *///?} else {
    private static final PushReaction IMMOVABLE = PushReaction.BLOCK;
    //?}
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
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X)
                .setValue(SEALED, false));
    }

    /**
     * The nether portal's properties: no collision, unbreakable in survival, light 11, a wall to
     * pistons, nothing to drop, no random ticks. {@link Portals#register} sets the id.
     */
    public static Properties defaultProperties() {
        return BlockBehaviour.Properties.of().noCollision().strength(-1.0F)
                .sound(SoundType.GLASS).lightLevel(state -> LIGHT).pushReaction(IMMOVABLE)
                .noLootTable();
    }

    /** Whether {@code state} is this block lying in {@code plane}. */
    public boolean isPortal(BlockState state, PortalPlane plane) {
        return state.is(this) && state.getValue(AXIS) == plane.axis();
    }

    /** This block's state for a portal in {@code plane}, unsealed. */
    public BlockState stateFor(PortalPlane plane) {
        return defaultBlockState().setValue(AXIS, plane.axis());
    }

    // 26.3 dropped block codecs.
    //? if <26.3 {
    @Override
    protected MapCodec<PortalBlock> codec() {
        return MapCodec.unit(this);
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, SEALED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return SHAPES.get(state.getValue(AXIS));
    }

    // Polymer answers a block's collision with its stand-in's, and a sealed portal's is a
    // barrier: on the server everything still passes, or whatever stands in it suffocates.
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return Shapes.empty();
    }

    // The same for a player's collision, which Polymer asks about separately.
    @Override
    public boolean overridePlayerCollisionsWithPolymer(BlockGetter level, BlockPos pos,
            BlockState blockState, ServerPlayer player) {
        return false;
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

    // Vanilla asks this for every portal block the entity's box touched along its move; the
    // portal takes the entity only once its centre is in this block, or passed through it this
    // tick, so a fast faller is not missed and a shoulder brushing the plane is not taken.
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
            InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity.canUsePortal(false) && centreEntered(entity, pos)) {
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

    // Whether the centre of the entity's box is in the block at pos, or crossed it on the way
    // from where the entity stood at the start of the tick.
    private static boolean centreEntered(Entity entity, BlockPos pos) {
        AABB cube = new AABB(pos);
        Vec3 centre = entity.getBoundingBox().getCenter();
        if (cube.contains(centre)) {
            return true;
        }
        Vec3 lift = centre.subtract(entity.position());
        Vec3 before = new Vec3(entity.xo, entity.yo, entity.zo).add(lift);
        return cube.clip(before, centre).isPresent();
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        if (state.getValue(SEALED)) {
            return Blocks.BARRIER.defaultBlockState();
        }
        return STAND_INS[Math.min(LightBlock.MAX_LEVEL, state.getLightEmission())];
    }

    // The stand-in is invisible and breaks in silence; the material's block is what shatters.
    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, PacketContext context) {
        return owner.material().plain();
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel level, BlockPos pos,
            BlockState state) {
        return new PortalHolder(owner.material(), PortalPlane.of(state.getValue(AXIS)));
    }
}
