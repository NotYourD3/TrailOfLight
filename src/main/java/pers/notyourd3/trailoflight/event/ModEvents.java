package pers.notyourd3.trailoflight.event;

import net.minecraft.world.InteractionHand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import pers.notyourd3.trailoflight.item.ModItems;
import pers.notyourd3.trailoflight.item.custom.LensExcavateItem;

public class ModEvents {
    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if(event.getEntity().isFakePlayer() && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.LENS_EXCAVATE)){
            event.setCanHarvest(true);
        }
    }
}
