package pers.notyourd3.trailoflight.block.custom;

import net.minecraft.world.level.block.BaseEntityBlock;

import java.awt.*;

public abstract class AbstractGeneratorBlock extends BaseEntityBlock {
    public AbstractGeneratorBlock(Properties p_49224_) {
        super(p_49224_);
    }

    public abstract Color getBeamColor();
}
