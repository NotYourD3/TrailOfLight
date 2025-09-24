package pers.notyourd3.trailoflight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.awt.*;

public interface ILaserEmitter {
    void emitLaser(Color color, Level level, BlockPos pos);
}
