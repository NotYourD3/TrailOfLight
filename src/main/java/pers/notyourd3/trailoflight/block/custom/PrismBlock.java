package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.IBeamHandler;
import pers.notyourd3.trailoflight.block.ILaserTrace;
import pers.notyourd3.trailoflight.block.entity.custom.PrismBlockEntity;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.Matrix4;
import pers.notyourd3.trailoflight.feature.Tri;

import java.awt.*;
import java.util.stream.Stream;

public class PrismBlock extends BaseEntityBlock implements ILaserTrace, IBeamHandler{
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class, Stream.of(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH).toList());
    private static final MapCodec<PrismBlock> CODEC = simpleCodec(PrismBlock::new);
    public PrismBlock(Properties p_49224_) {
        super(p_49224_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PrismBlockEntity(pos, state);
    }

    public static Vec3 refracted(double from, double to, Vec3 vec, Vec3 normal) {
        double ratio = from / to;
        double mag = normal.cross(vec).lengthSqr();

        Vec3 first = normal.scale(-1).cross(vec);
        first = normal.cross(first).scale(ratio);

        Vec3 second = normal.scale(Math.sqrt(1 - ratio * ratio * mag));

        return first.subtract(second);
    }

    @Nullable
    @Override
    public HitResultWithData<Vec3> collisionRayTraceLaser(@NotNull BlockState blockState, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Vec3 startRaw, @NotNull Vec3 endRaw) {
        Direction facing = blockState.getValue(FACING);
        Matrix4 matrixA = new Matrix4();
        Matrix4 matrixB = new Matrix4();
        switch (facing) {
            case UP:
            case DOWN:
            case EAST:
                break;
            case NORTH:
                matrixA.rotate(Math.toRadians(270), new Vec3(0, -1, 0));
                matrixB.rotate(Math.toRadians(270), new Vec3(0, 1, 0));
                break;
            case SOUTH:
                matrixA.rotate(Math.toRadians(90), new Vec3(0, -1, 0));
                matrixB.rotate(Math.toRadians(90), new Vec3(0, 1, 0));
                break;
            case WEST:
                matrixA.rotate(Math.toRadians(180), new Vec3(0, -1, 0));
                matrixB.rotate(Math.toRadians(180), new Vec3(0, 1, 0));
                break;
        }

        Vec3
                a = new Vec3(0.001, 0.001, 0), // This needs to be offset
                b = new Vec3(1, 0.001, 0.5),
                c = new Vec3(0.001, 0.001, 1), // and this too. Just so that blue refracts in ALL cases

                A = a.add(0, 0.998, 0),
                B = b.add(0, 0.998, 0), // these y offsets are to fix translocation issues
                C = c.add(0, 0.998, 0);

        Tri[] tris = new Tri[]{
                new Tri(a, b, c),
                new Tri(A, C, B),

                new Tri(a, c, C),
                new Tri(a, C, A),

                new Tri(a, A, B),
                new Tri(a, B, b),

                new Tri(b, B, C),
                new Tri(b, C, c)
        };

        Vec3 start = matrixA.apply(startRaw.subtract(new Vec3(pos)).subtract(0.5, 0.5, 0.5)).add(0.5, 0.5, 0.5);
        Vec3 end = matrixA.apply(endRaw.subtract(new Vec3(pos)).subtract(0.5, 0.5, 0.5)).add(0.5, 0.5, 0.5);

        Tri hitTri = null;
        Vec3 hit = null;
        double shortestSq = Double.POSITIVE_INFINITY;

        for (Tri tri : tris) {
            Vec3 v = tri.trace(start, end);
            if (v != null) {
                double distSq = start.subtract(v).lengthSqr();
                if (distSq < shortestSq) {
                    hit = v;
                    shortestSq = distSq;
                    hitTri = tri;
                }
            }
        }

        if (hit == null)
            return null;

        return new HitResultWithData<Vec3>(matrixB.apply(hit.subtract(0.5, 0.5, 0.5)).add(0.5, 0.5, 0.5).add(new Vec3(pos)), Direction.UP, pos, false).data(matrixB.apply(hitTri.normal()));
    }

    @Override
    public void onBeam(Level level, BlockPos pos, Beam beam) {
        BlockState state = level.getBlockState(pos);

        Color color = beam.color;

        int sum = color.getRed() + color.getBlue() + color.getGreen();
        double red = color.getAlpha() * color.getRed() / sum;
        double green = color.getAlpha() * color.getGreen() / sum;
        double blue = color.getAlpha() * color.getBlue() / sum;

        Vec3 hitPos = beam.endLoc;

        if (color.getRed() != 0)
            fireColor(level, pos, state, hitPos, beam.endLoc.subtract(beam.initLoc).normalize(), 0.6, new Color(color.getRed(), 0, 0, (int) red), beam);
        if (color.getGreen() != 0)
            fireColor(level, pos, state, hitPos, beam.endLoc.subtract(beam.initLoc).normalize(), 0.4, new Color(0, color.getGreen(), 0, (int) green), beam);
        if (color.getBlue() != 0)
            fireColor(level, pos, state, hitPos, beam.endLoc.subtract(beam.initLoc).normalize(), 0.2, new Color(0, 0, color.getBlue(), (int) blue), beam);
    }

    private void fireColor(Level world, BlockPos pos, BlockState state, Vec3 hitPos, Vec3 ref, double IORMod, Color color, Beam beam) {
        HitResultWithData<Vec3> r = collisionRayTraceLaser(state, world, pos, hitPos.subtract(ref), hitPos.add(ref));
        if (r != null && r.data != null) {
            Vec3 normal = r.data;
            ref = refracted(1.0 + IORMod, 1.2 + IORMod, ref, normal).normalize();
            hitPos = r.getLocation();

            for (int i = 0; i < 5; i++) {

                r = collisionRayTraceLaser(state, world, pos, hitPos.add(ref), hitPos);
                // trace backward so we don't hit hitPos first

                if (r != null && r.data != null) {
                    normal = r.data.scale(-1);
                    Vec3 oldRef = ref;
                    ref = refracted(1.2 + IORMod, 1.0 + IORMod, ref, normal).normalize();
                    if (Double.isNaN(ref.x) || Double.isNaN(ref.y) || Double.isNaN(ref.z)) {
                        ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
                        break;
                    }
                    //BlockLens.showBeam(world, hitPos, r.hitVec, color);
                    hitPos = r.getLocation();
                }
            }

            Beam beam1 = new Beam(hitPos, ref, world, color).increaseLife();
            beam1.spawn();
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    public static class HitResultWithData<T> extends BlockHitResult {
        public T data;

        public HitResultWithData(Vec3 location, Direction direction, BlockPos blockPos, boolean inside, T data) {
            super(location, direction, blockPos, inside);
            this.data = data;
        }

        public HitResultWithData(Vec3 location, Direction direction, BlockPos blockPos, boolean inside) {
            this(location, direction, blockPos, inside, null);
        }

        public HitResultWithData<T> data(T data) {
            this.data = data;
            return this;
        }
    }
}