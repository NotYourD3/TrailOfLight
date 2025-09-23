package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import pers.notyourd3.trailoflight.block.IPrecision;


public class ScrewDriverItem extends Item {
    public ScrewDriverItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            ItemStack stack = context.getItemInHand();

            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            Block block = state.getBlock();


            if (block instanceof IPrecision) {
                ((IPrecision) block).adjust(context.getLevel(), context.getClickedPos(), stack, context.getPlayer().isCrouching(), context.getClickedFace());
            }
        }
        return InteractionResult.SUCCESS;
    }
}
