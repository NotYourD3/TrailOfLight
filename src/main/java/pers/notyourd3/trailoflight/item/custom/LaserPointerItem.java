package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.world.item.Item;

public class LaserPointerItem extends Item implements IChargeable {
    public LaserPointerItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxCharge() {
        return 720;
    }

    @Override
    public void charge(int charge) {

    }

    @Override
    public void decharge(int charge) {

    }
}
