package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class EnergyDrinkItem extends Item implements IChargableItem {
    public EnergyDrinkItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxAlpha() {
        return 80000;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        stack.get(DataComponents.CONSUMABLE).onConsume(level, entity, stack.copy());
        if (IChargableItem.getAlpha(stack) >= 10000) {
            IChargableItem.decreaseBeam(stack, 10000);
        }
        return stack;
    }


}
