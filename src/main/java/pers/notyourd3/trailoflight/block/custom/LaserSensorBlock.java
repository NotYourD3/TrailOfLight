package pers.notyourd3.trailoflight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import pers.notyourd3.trailoflight.block.IBeamHandler;
import pers.notyourd3.trailoflight.feature.Beam;

public class LaserSensorBlock extends Block implements IBeamHandler {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    
    public LaserSensorBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void onBeam(Level level, BlockPos pos, Beam beam) {
        if (!level.getBlockState(pos).getValue(POWERED)) {
            level.setBlockAndUpdate(pos, defaultBlockState().setValue(POWERED, true));
            level.updateNeighborsAt(pos, this);
        }
    }
    @Override
    public void onBeamRemoved(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getValue(POWERED)) {
            level.setBlockAndUpdate(pos, defaultBlockState());
            level.updateNeighborsAt(pos, this);
        }
    }
    

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
    
    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (blockState.getValue(POWERED)) {
            return 15;
        }
        return 0;
    }
}