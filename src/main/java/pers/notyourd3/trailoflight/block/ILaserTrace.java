package pers.notyourd3.trailoflight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by TheCodeWarrior
 */
public interface ILaserTrace {
    @Nullable
    HitResult collisionRayTraceLaser(@Nonnull BlockState blockState, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull Vec3 startRaw, @Nonnull Vec3 endRaw);
}