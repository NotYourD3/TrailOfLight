package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.entity.IBeamHandler;
import pers.notyourd3.trailoflight.block.entity.custom.ChargerEntity;
import pers.notyourd3.trailoflight.feature.Beam;

public class ChargerBlock extends BaseEntityBlock implements IBeamHandler {
    public static final MapCodec<ChargerBlock> CODEC = simpleCodec(ChargerBlock::new);

    public ChargerBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public ChargerEntity getEntity(Level level, BlockPos pos) {
        return (ChargerEntity) level.getBlockEntity(pos);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ChargerEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ChargerEntity charger = getEntity(level, pos);
        var inventory = charger.getItemHandler();
        var input = inventory.getStackInSlot(0);
        if (input.isEmpty() && !stack.isEmpty()) {
            charger.setStack(stack.copyWithCount(1));
            player.setItemInHand(hand, stack.consumeAndReturn(stack.getCount() - 1, null));
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else if (stack.isEmpty()) {
            charger.setStack(ItemStack.EMPTY);
            var item = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), input);
            item.setNoPickUpDelay();
            level.addFreshEntity(item);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onBeam(Level level, BlockPos pos, Beam beam) {
        ChargerEntity entity = getEntity(level, pos);
        entity.onBeam(beam);
    }
    
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ChargerEntity entity = getEntity(level, pos);
            ItemStack stack = entity.getStack();
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }

        return super.playerWillDestroy(level, pos, state, player);
    }
}