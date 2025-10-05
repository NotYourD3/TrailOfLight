package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.entity.custom.LaserGeneratorEntity;
import pers.notyourd3.trailoflight.block.entity.custom.MirrorEntity;

import java.awt.*;

public class LavaGeneratorBlock extends AbstractGeneratorBlock{
    public static final MapCodec<LavaGeneratorBlock> CODEC = simpleCodec(LavaGeneratorBlock::new);
    public LavaGeneratorBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public Color getBeamColor() {
        return new Color(255,70,15,30);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
            return CODEC;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LaserGeneratorEntity(blockPos, blockState);
    }
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @javax.annotation.Nullable Orientation orientation, boolean movedByPiston) {
        LaserGeneratorEntity entity = getEntity(level, pos);
        if (entity == null) return;
        for (Direction direction : Direction.values()){
            BlockPos relative = pos.relative(direction);
            BlockState relativeState = level.getBlockState(relative);
            if(relativeState.is(Blocks.LAVA) && relativeState.getValue(LiquidBlock.LEVEL) == 0){
                entity.addFuel(500);
                level.setBlockAndUpdate(relative, Blocks.AIR.defaultBlockState());
                level.addParticle(ParticleTypes.FLAME, relative.getX() + 0.5, relative.getY() + 0.5, relative.getZ() + 0.5, 0, 0, 0);
            }
        }
    }

}
