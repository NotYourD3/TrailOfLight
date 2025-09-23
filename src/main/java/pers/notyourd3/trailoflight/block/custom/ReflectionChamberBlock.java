package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.IBeamHandler;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.block.entity.custom.ReflectionChamberEntity;
import pers.notyourd3.trailoflight.feature.Beam;

public class ReflectionChamberBlock extends BaseEntityBlock implements IBeamHandler {
    private static final MapCodec<ReflectionChamberBlock> CODEC = simpleCodec(ReflectionChamberBlock::new);

    public ReflectionChamberBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTickerHelper(Level level, BlockEntityType<T> type, BlockEntityType<? extends ReflectionChamberEntity> type2) {
        return createTickerHelper(type, type2, ReflectionChamberEntity::tick);
    }

    public ReflectionChamberEntity getEntity(BlockState state, Level level, BlockPos pos) {
        return (ReflectionChamberEntity) level.getBlockEntity(pos);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReflectionChamberEntity(blockPos, blockState);
    }

    @Override
    public void onBeam(Level level, BlockPos pos, Beam beam) {
        ReflectionChamberEntity entity = getEntity(level.getBlockState(pos), level, pos);
        entity.onBeam(beam);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(level, type, ModBlockEntities.REFLECTION_CHAMBER.get());
    }
}
