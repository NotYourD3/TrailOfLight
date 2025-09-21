package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.entity.IBeamHandler;
import pers.notyourd3.trailoflight.block.entity.ILaserTrace;
import pers.notyourd3.trailoflight.block.entity.IPrecision;
import pers.notyourd3.trailoflight.block.entity.custom.MirrorEntity;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.Matrix4;

import java.util.Optional;
import java.util.stream.Stream;

public class MirrorBlock extends BaseEntityBlock implements ILaserTrace, IBeamHandler, IPrecision {
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class, Stream.of(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH).toList());
    private static final MapCodec<MirrorBlock> CODEC = simpleCodec(MirrorBlock::new);

    public MirrorBlock(Properties properties) {
        super(properties);
    }

    public MirrorEntity getEntity(Level level, BlockPos pos) {
        return (MirrorEntity) level.getBlockEntity(pos);
    }

    @Nullable
    @Override
    public HitResult collisionRayTraceLaser(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos pos, @NotNull Vec3 startRaw, @NotNull Vec3 endRaw) {
        double size = 0.4375;
        double depth = 0.01;

        MirrorEntity tile = getEntity(level, pos);
        if (tile == null) return null;


        Vec3 start = startRaw.subtract(Vec3.atCenterOf(pos));
        Vec3 end = endRaw.subtract(Vec3.atCenterOf(pos));

        Matrix4 matrix = new Matrix4();
        matrix.rotate(-Math.toRadians(tile.getRotX()), new Vec3(1, 0, 0));
        matrix.rotate(-Math.toRadians(tile.getRotY()), new Vec3(0, 1, 0));

        Matrix4 inverse = new Matrix4();
        inverse.rotate(Math.toRadians(tile.getRotY()), new Vec3(0, 1, 0));
        inverse.rotate(Math.toRadians(tile.getRotX()), new Vec3(1, 0, 0));


        start = matrix.apply(start);
        end = matrix.apply(end);

        AABB aabb = new AABB(-size, -size, -depth, size, size, depth);
        Optional<Vec3> result = aabb.clip(start, end);
        if (result.isEmpty())
            return null;
        Vec3 hitLocal = result.get();


        hitLocal = inverse.apply(hitLocal);
        hitLocal = hitLocal.add(Vec3.atCenterOf(pos));

        return new BlockHitResult(hitLocal, Direction.UP, pos, false);
    }

    @Override
    public void onBeam(Level level, BlockPos pos, Beam beam) {
        getEntity(level, pos).onBeam(beam);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Shapes.rotateHorizontal(makeShape()).get(state.getValue(FACING));
    }

    public VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.4375, 0.0625, 0.6875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.25, 0.0625, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.25, 1, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.0625, 0.4375, 1, 0.6875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.25, 0.4375, 0.9375, 0.5, 0.5625), BooleanOp.OR);

        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) {
            float x = 0, y = 0;
            switch (state.getValue(FACING)) {
                case WEST:
                    y = 90;
                    break;
                case EAST:
                    y = 270;
                    break;
                case NORTH:
                    y = 180;
                    break;
                case SOUTH:
                    y = 0;
                    break;
            }
            getEntity(level, pos).setRotX(x);
            getEntity(level, pos).setRotY(y);
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @javax.annotation.Nullable Orientation orientation, boolean movedByPiston) {
        MirrorEntity entity = getEntity(level, pos);
        if (entity == null) return;
        boolean isReceiving = level.hasNeighborSignal(pos);
        if (entity.isPowered != isReceiving) {
            entity.isPowered = isReceiving;
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MirrorEntity(blockPos, blockState);
    }

    @Override
    public float getRotX(Level level, BlockPos pos) {
        return getEntity(level, pos).getRotX();
    }

    @Override
    public float getRotY(Level level, BlockPos pos) {
        return getEntity(level, pos).getRotY();
    }

    @Override
    public void setRotX(Level level, BlockPos pos, float rotX) {
        getEntity(level, pos).setRotX(rotX);
    }

    @Override
    public void setRotY(Level level, BlockPos pos, float rotY) {
        getEntity(level, pos).setRotY(rotY);
    }
}