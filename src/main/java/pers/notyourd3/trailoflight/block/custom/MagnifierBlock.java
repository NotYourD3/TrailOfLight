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
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.block.entity.custom.MagnifierEntity;

public class MagnifierBlock extends BaseEntityBlock {
    private static final MapCodec<MagnifierBlock> CODEC = simpleCodec(MagnifierBlock::new);

    public MagnifierBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTickerHelper(Level level, BlockEntityType<T> type, BlockEntityType<? extends MagnifierEntity> type2) {
        return createTickerHelper(type, type2, MagnifierEntity::tick);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MagnifierEntity(blockPos, blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(level, type, ModBlockEntities.MAGNIFIER.get());
    }
}
