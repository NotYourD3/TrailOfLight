package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import pers.notyourd3.trailoflight.block.custom.AbstractGeneratorBlock;
import pers.notyourd3.trailoflight.block.custom.ILaserEmitter;
import pers.notyourd3.trailoflight.block.custom.LaserGeneratorBlock;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;

import java.awt.*;

public class LaserGeneratorEntity extends BlockEntity {
    public int fuel = 0;

    public LaserGeneratorEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LASER_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LaserGeneratorEntity entity) {
        if (entity.fuel > 0) {
            Color color = new Color(255, 255, 255, 1);
            if (state.getBlock() instanceof AbstractGeneratorBlock block) {
                color = block.getBeamColor();
            }
            if(level.getBlockState(pos.relative(state.getValue(LaserGeneratorBlock.FACING))).getBlock() instanceof ILaserEmitter emitter){
                emitter.emitLaser(color,level,pos.relative(state.getValue(LaserGeneratorBlock.FACING)));
            }else{
            Beam beam = new Beam(pos.getCenter(), state.getValue(LaserGeneratorBlock.FACING).getUnitVec3(), level, color);
            BeamManager.INSTANCE.addBeam(beam);}
            entity.fuel--;
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fuel = input.getIntOr("fuel", 0);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("fuel", fuel);
    }

    public void addFuel(int fuel) {
        this.fuel += fuel;
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
}
