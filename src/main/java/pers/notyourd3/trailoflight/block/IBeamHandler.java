package pers.notyourd3.trailoflight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import pers.notyourd3.trailoflight.feature.Beam;


public interface IBeamHandler {
    void onBeam(Level level, BlockPos pos, Beam beam);
}
