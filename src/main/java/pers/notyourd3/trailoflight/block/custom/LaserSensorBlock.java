package pers.notyourd3.trailoflight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
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
    private static void makeParticle(BlockState state, LevelAccessor level, BlockPos pos, float alpha) {
        double d0 = (double)pos.getX() +0.5 + 1.2 * Math.random();
        double d1 = (double)pos.getY() +0.5 + 1.2 * Math.random();
        double d2 = (double)pos.getZ() +0.5 + 1.2 * Math.random();
        level.addParticle(new DustParticleOptions(16711680, alpha), d0, d1, d2, 0.0, 0.0, 0.0);
    }

    public void animateTick(BlockState p_221395_, Level p_221396_, BlockPos p_221397_, RandomSource p_221398_) {
        if ((Boolean)p_221395_.getValue(POWERED) && p_221398_.nextFloat() < 0.25F) {
            makeParticle(p_221395_, p_221396_, p_221397_, 0.5F);
        }
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