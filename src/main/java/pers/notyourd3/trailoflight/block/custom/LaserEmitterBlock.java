package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;

import java.awt.*;

public class LaserEmitterBlock extends Block implements ILaserEmitter{

    public LaserEmitterBlock(Properties p_49224_) {
        super(p_49224_);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DirectionalBlock.FACING);
    }

    public void emitLaser(Color color, Level level, BlockPos pos){
        Beam beam = new Beam(pos.getCenter(),level.getBlockState(pos).getValue(DirectionalBlock.FACING).getUnitVec3(), level, color);
        BeamManager.INSTANCE.addBeam( beam);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(DirectionalBlock.FACING, context.getNearestLookingDirection().getOpposite());
    }
}
