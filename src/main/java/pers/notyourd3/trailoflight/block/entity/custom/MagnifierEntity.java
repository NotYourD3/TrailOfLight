package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;

import java.awt.*;

public class MagnifierEntity extends BlockEntity {
    public MagnifierEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MAGNIFIER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MagnifierEntity entity) {
        Color color = new Color(255, 255, 255, 48);
        Beam beam = new Beam(pos.getCenter(), new Vec3(0, -1, 0), level, color);
        BeamManager.INSTANCE.addBeam(beam);
    }
}
