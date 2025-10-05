package pers.notyourd3.trailoflight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.block.entity.custom.LaserGeneratorEntity;

import java.awt.*;

public abstract class AbstractGeneratorBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public AbstractGeneratorBlock(Properties p_49224_) {
        super(p_49224_);
    }

    public abstract Color getBeamColor();
    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTickerHelper(Level level, BlockEntityType<T> type, BlockEntityType<? extends LaserGeneratorEntity> type2) {
        return createTickerHelper(type, type2, LaserGeneratorEntity::tick);
    }
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        return level.isClientSide() ? null : createTickerHelper(level, type, ModBlockEntities.LASER_GENERATOR.get());
    }
    public LaserGeneratorEntity getEntity(Level level, BlockPos pos) {
        return (LaserGeneratorEntity) level.getBlockEntity(pos);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

}
