package pers.notyourd3.trailoflight.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import pers.notyourd3.trailoflight.block.entity.custom.LaserAssemblyTableEntity;

public class LaserAssemblyTableRenderer implements BlockEntityRenderer<LaserAssemblyTableEntity> {

    public LaserAssemblyTableRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LaserAssemblyTableEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, Vec3 ib) {
        Level level = te.getLevel();
        if (level == null) {
            return;
        }


        double time = level.getGameTime() + partialTicks;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        int items = 0;
        for (int i = 0; i < te.getContainerSize(); i++) {
            if (te.getItem(i).isEmpty()) {
                break;
            } else {
                items++;
            }
        }

        if (items == 0) {
            return;
        }


        float anglePer = 360F / items;
        float totalAngle = 0F;
        float[] angles = new float[te.getContainerSize()];
        for (int i = 0; i < angles.length; i++) {
            angles[i] = totalAngle += anglePer;
        }

        for (int i = 0; i < te.getContainerSize(); i++) {
            ItemStack stack = te.getItem(i);
            if (stack.isEmpty()) {

                if (i < items) continue;
                else break;
            }

            poseStack.pushPose();


            poseStack.translate(0.5D, 1.2D, 0.5D);


            poseStack.mulPose(Axis.YP.rotationDegrees(angles[i] + (float) time));


            poseStack.translate(0.275D, -0.2D, 0.275D);


            poseStack.mulPose(Axis.YP.rotationDegrees(22.5F));


            double bobbing = 0.0375 * Math.sin((time + i * 10) / 5.0D);
            poseStack.translate(0D, bobbing, 0D);


            poseStack.mulPose(Axis.XP.rotationDegrees(180F));

            poseStack.mulPose(Axis.ZP.rotationDegrees(180F));


            itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    multiBufferSource,
                    level,
                    (int) i // item ID/seed is typically an integer, using slot index here
            );

            poseStack.popPose();
        }
    }
}