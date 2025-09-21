package pers.notyourd3.trailoflight.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IPrecision {
    float getRotX(Level level, BlockPos pos);

    float getRotY(Level level, BlockPos pos);

    void setRotX(Level level, BlockPos pos, float rotX);

    void setRotY(Level level, BlockPos pos, float rotY);

    default void adjust(Level worldIn, BlockPos pos, ItemStack stack, boolean sneaking, Direction side) {
        BlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() instanceof IPrecision && !worldIn.isClientSide()) {
            float jump = Helper.getRotationMultiplier(stack) * (sneaking ? -1 : 1);

            if (side.getAxis() == Direction.Axis.Y) {
                setRotY(worldIn, pos, (getRotY(worldIn, pos) + jump) % 360);
            } else {
                setRotX(worldIn, pos, (getRotX(worldIn, pos) + jump) % 360);
            }
        }
    }

    final class Helper {

        public static final String MODE_TAG = "mode";
        public static final int DEFAULT_MULTIPLIER = 4;

        public static final float[] multipliers = {
                0.125f, 0.25f, 0.5f, 1,
                5, 22.5f, 45, 90
        };

        public static float getRotationMultiplier(ItemStack stack) {
            return multipliers[getRotationIndex(stack)];
        }

        public static int getRotationIndex(ItemStack stack) {
            return 5;
        }
    }
}
