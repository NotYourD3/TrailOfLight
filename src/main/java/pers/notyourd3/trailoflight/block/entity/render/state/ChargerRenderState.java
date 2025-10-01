package pers.notyourd3.trailoflight.block.entity.render.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChargerRenderState extends BlockEntityRenderState {
    public ItemStack stack;
    public Level level;
    public float partialTicks;
}