package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;

public class PrismBlockEntity extends BlockEntity {
    public PrismBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PRISM.get(), pos, blockState);
    }
}