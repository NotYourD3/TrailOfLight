package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.world.item.Item;
import pers.notyourd3.trailoflight.feature.Beam;

public abstract class AbstractLensItem extends Item {
    public AbstractLensItem(Properties properties) {
        super(properties);
    }
    public abstract void onSpawn(Beam beam);
}
