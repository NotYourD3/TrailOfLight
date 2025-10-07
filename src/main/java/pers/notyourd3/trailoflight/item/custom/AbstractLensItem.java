package pers.notyourd3.trailoflight.item.custom;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import pers.notyourd3.trailoflight.feature.Beam;

import java.awt.*;

public abstract class AbstractLensItem extends Item {
    public AbstractLensItem(Properties properties) {
        super(properties);
    }

    public abstract void onSpawn(Beam beam, ItemStack stack);

    public abstract Pair<Color, Color> getColorRange();
}
