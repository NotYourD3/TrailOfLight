package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.IPrecision;
import pers.notyourd3.trailoflight.block.entity.custom.LaserEmitterEntity;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.item.custom.AbstractLensItem;

import java.awt.*;

public class LaserEmitterBlock extends BaseEntityBlock{

    public LaserEmitterBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(LaserEmitterBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DirectionalBlock.FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LaserEmitterEntity(blockPos, blockState);
    }

    public void emitLaser(Color color, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LaserEmitterEntity emitterEntity) {
            emitterEntity.emitLaser(color);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(DirectionalBlock.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LaserEmitterEntity emitterEntity) {
            ItemStack lens = emitterEntity.getLens();
            if (lens.isEmpty() && !stack.isEmpty() && stack.getItem() instanceof AbstractLensItem) {
                // 安装透镜
                emitterEntity.setLens(stack.copyWithCount(1));
                player.setItemInHand(hand, stack.consumeAndReturn(stack.getCount() - 1, null));
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            } else if (stack.isEmpty() && !lens.isEmpty()) {
                // 取出透镜
                ItemStack lensStack = lens.copy();
                emitterEntity.setLens(ItemStack.EMPTY);
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), lensStack);
                itemEntity.setNoPickUpDelay();
                level.addFreshEntity(itemEntity);
                return InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.PASS;
    }
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LaserEmitterEntity emitterEntity) {
                ItemStack lens = emitterEntity.getLens();
                if (!lens.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, lens);
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            }
            return super.playerWillDestroy(level, pos, state, player);

    }
}