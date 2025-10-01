package pers.notyourd3.trailoflight.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.block.entity.custom.LaserAssemblyTableEntity;
import pers.notyourd3.trailoflight.block.entity.render.state.LaserAssemblyTableRenderState;


public class LaserAssemblyTableRenderer implements BlockEntityRenderer<LaserAssemblyTableEntity, LaserAssemblyTableRenderState> {
    private final ItemModelResolver itemModelResolver;

    public LaserAssemblyTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public LaserAssemblyTableRenderState createRenderState() {
        return new LaserAssemblyTableRenderState();
    }

    @Override
    public void extractRenderState(LaserAssemblyTableEntity blockEntity, LaserAssemblyTableRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

        NonNullList<ItemStack> stacks = NonNullList.create();
        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            stacks.add(blockEntity.getItem(i).copy());
        }

        renderState.stacks = stacks;
        renderState.level = blockEntity.getLevel();
        renderState.partialTicks = partialTick;
    }
    @Override
    public void submit(LaserAssemblyTableRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {

        Level level = renderState.level;
        if (level == null) {
            return;
        }

        NonNullList<ItemStack> stacks = renderState.stacks;
        float partialTicks = renderState.partialTicks;


        double time = level.getGameTime() + partialTicks;

        int items = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                items++;
            }
        }

        if (items == 0) return;

        float anglePer = 360F / items;
        float totalAngle = 0F;
        float[] angles = new float[stacks.size()];
        for (int i = 0; i < angles.length; i++) {

            angles[i] = totalAngle += anglePer;
        }

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);

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
            poseStack.scale(0.35F, 0.35F, 0.35F);

            ItemStackRenderState itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.FIXED, level, null, 0);
            itemState.submit(
                    poseStack,
                    submitNodeCollector,
                    renderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );

            poseStack.popPose();
        }
    }
}