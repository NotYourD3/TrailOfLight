package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import pers.notyourd3.trailoflight.feature.Beam;

public class LensExtractItem extends AbstractLensItem {
    public LensExtractItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onSpawn(Beam beam) {
        try (Transaction transaction = Transaction.open(null)) {

            beam.fire((HitResult result) -> {
                if (result instanceof BlockHitResult hit) {
                    ResourceHandler<ItemResource> handler = beam.level.getCapability(Capabilities.Item.BLOCK, hit.getBlockPos(), hit.getDirection());
                    if (handler != null) {
                        ItemStack extractedStack;
                        int extractedCount = 0;
                        for (int i = 0; i < handler.size(); i++) {
                            if (!handler.getResource(i).isEmpty()) {
                                extractedCount = handler.extract(
                                        i,
                                        handler.getResource(i),
                                        1,
                                        transaction
                                );
                                if (extractedCount > 0) {
                                    extractedStack = handler.getResource(i).toStack(extractedCount);

                                    ItemEntity entity = new ItemEntity(beam.level,
                                            hit.getLocation().x,
                                            hit.getLocation().y,
                                            hit.getLocation().z,
                                            extractedStack);
                                    Vec3 motion = beam.initLoc.subtract(hit.getLocation()).normalize().scale(0.2);
                                    entity.lerpMotion(motion);
                                    beam.level.addFreshEntity(entity);
                                    break;
                                }
                            }
                        }
                        if (extractedCount > 0) {
                            transaction.commit();
                        }
                    }
                }
            }, entity -> {
                if (entity == null) return true;
                return !(entity instanceof ItemEntity);
            });
        }
        beam.getEntitiesInRange().forEach(entity -> {
            if (entity instanceof ItemEntity entity1) {
                Vec3 motion = beam.initLoc.subtract(entity1.position()).normalize().scale(0.2);
                entity1.lerpMotion(motion);
            }
        });
    }
}
