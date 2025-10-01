package pers.notyourd3.trailoflight.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.block.entity.custom.LaserGeneratorEntity;

import java.awt.*;

public class LaserGeneratorBlock extends AbstractGeneratorBlock {
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    private static final MapCodec<LaserGeneratorBlock> CODEC = simpleCodec(LaserGeneratorBlock::new);

    public LaserGeneratorBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTickerHelper(Level level, BlockEntityType<T> type, BlockEntityType<? extends LaserGeneratorEntity> type2) {
        return createTickerHelper(type, type2, LaserGeneratorEntity::tick);
    }

    @Override
    public Color getBeamColor() {
        return new Color(255, 0, 0, 16);
    }

    public LaserGeneratorEntity getEntity(Level level, BlockPos pos) {
        return (LaserGeneratorEntity) level.getBlockEntity(pos);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LaserGeneratorEntity(blockPos, blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(level, type, ModBlockEntities.LASER_GENERATOR.get());
    }


    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        LaserGeneratorEntity entity = getEntity(level, pos);
        int burnTime = stack.getBurnTime(RecipeType.SMELTING, level.fuelValues());
        if (burnTime > 0 && stack.getCraftingRemainder() == ItemStack.EMPTY) {
            entity.addFuel(burnTime / 5);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.CONSUME;
        }


        return InteractionResult.PASS;
    }
}
