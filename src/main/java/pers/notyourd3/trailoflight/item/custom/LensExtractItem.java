package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import pers.notyourd3.trailoflight.feature.Beam;

public class LensExtractItem extends AbstractLensItem{
    public LensExtractItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onSpawn(Beam beam){
        beam.fire((HitResult result) ->{
            if(result instanceof BlockHitResult hit){
                IItemHandler handler = beam.level.getCapability(Capabilities.ItemHandler.BLOCK,hit.getBlockPos(),hit.getDirection());
                if(handler != null){
                    ItemStack extractedStack;
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack slotStack = handler.getStackInSlot(i);
                        if (!slotStack.isEmpty()) {
                            extractedStack = handler.extractItem(i, 1, false);
                            if (!extractedStack.isEmpty()) {
                                ItemEntity entity = new ItemEntity(beam.level,
                                        hit.getLocation().x,
                                        hit.getLocation().y,
                                        hit.getLocation().z,
                                        extractedStack);
                                Vec3 motion = beam.initLoc.subtract(hit.getLocation()).normalize().scale(0.2);
                                entity.lerpMotion(motion.x, motion.y, motion.z);
                                beam.level.addFreshEntity(entity);
                                break;
                            }
                        }
                    }
                }
            }
        },entity -> {
            if (entity == null) return true;
            return !(entity instanceof ItemEntity);
        });
        beam.getEntitiesInRange().forEach(entity -> {
            if(entity instanceof ItemEntity entity1){
                Vec3 motion = beam.initLoc.subtract(entity1.position()).normalize().scale(1.5);
                entity1.lerpMotion(motion.x, motion.y, motion.z);
            }
        });
    }
}
