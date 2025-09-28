package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import pers.notyourd3.trailoflight.block.IBeamHandler;
import pers.notyourd3.trailoflight.block.entity.custom.LaserAssemblyTableEntity;
import pers.notyourd3.trailoflight.feature.Beam;

import javax.annotation.Nullable;

public class LaserAssemblyTableBlock extends BaseEntityBlock implements IBeamHandler {
    private static final MapCodec<LaserAssemblyTableBlock> CODEC = simpleCodec(LaserAssemblyTableBlock::new);

    public LaserAssemblyTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LaserAssemblyTableEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        LaserAssemblyTableEntity assemblyTable = (LaserAssemblyTableEntity) level.getBlockEntity(pos);


        if (!stack.isEmpty()) {

            ItemStack remaining = assemblyTable.addItem(stack.copy());
            player.setItemInHand(hand, remaining);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {

            ItemStack extracted = assemblyTable.extractItem();
            if (!extracted.isEmpty()) {
                player.addItem( extracted);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onBeam(Level level, BlockPos pos, Beam beam) {
        LaserAssemblyTableEntity entity = (LaserAssemblyTableEntity) level.getBlockEntity(pos);
        entity.onBeam(beam);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        LaserAssemblyTableEntity entity = (LaserAssemblyTableEntity) level.getBlockEntity(pos);
        for (int i = 0; i < entity.getContainerSize(); i++) {
            ItemStack stack = entity.getItem(i);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}