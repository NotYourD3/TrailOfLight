package pers.notyourd3.trailoflight.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pers.notyourd3.trailoflight.item.ModDataComponents;
import pers.notyourd3.trailoflight.item.custom.IChargableItem;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
    public void onisBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.getItem() instanceof IChargableItem){
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
    public void ongetBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(stack.getItem() instanceof IChargableItem){
            ModDataComponents.BeamStorageInfoRecord data = stack.get(ModDataComponents.BEAM_STORAGE_INFO.get());
            if (data == null || data.alpha() <= 0) {
                cir.setReturnValue(0);
            }
            if (data != null) {
                cir.setReturnValue(Math.round((float) data.alpha() * 13.0F / (float) data.maxAlpha()));
            }
        }
    }
    @Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
    public void ongetBarColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(stack.getItem() instanceof IChargableItem){
            ModDataComponents.BeamStorageInfoRecord data = stack.get(ModDataComponents.BEAM_STORAGE_INFO.get());
            if (data == null || data.alpha() <= 0) {
                cir.setReturnValue(0);
            }
            cir.setReturnValue(IChargableItem.getBeamColor(stack).getRGB());
        }
    }
}
