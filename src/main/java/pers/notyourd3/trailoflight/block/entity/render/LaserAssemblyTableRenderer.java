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
        // 通常用于获取 ItemRenderer, TextureManager 等渲染上下文对象
    }

    @Override
    public void render(LaserAssemblyTableEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, Vec3 ib) {
        Level level = te.getLevel();
        if (level == null) {
            return;
        }

        // 计算当前游戏时间，包含分Tick平滑
        // time = ClientTickHandler.getTicksInGame() + partialTicks;
        double time = level.getGameTime() + partialTicks;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        // 1. 确定有多少非空物品需要渲染
        int items = 0;
        for (int i = 0; i < te.getContainerSize(); i++) {
            if (te.getItem(i).isEmpty()) {
                // 片段1的逻辑是遇到第一个空槽位就停止计数
                break;
            } else {
                items++;
            }
        }

        if (items == 0) {
            return;
        }

        // 2. 计算每个物品的固定角度
        float anglePer = 360F / items;
        float totalAngle = 0F;
        float[] angles = new float[te.getContainerSize()];
        for (int i = 0; i < angles.length; i++) {
            angles[i] = totalAngle += anglePer;
        }

        // 3. 渲染循环
        for (int i = 0; i < te.getContainerSize(); i++) {
            ItemStack stack = te.getItem(i);
            if (stack.isEmpty()) {
                // 确保只渲染前面被计数的物品
                if (i < items) continue;
                else break;
            }

            poseStack.pushPose(); // GlStateManager.pushMatrix();

            // 初始平移到方块中心略高处
            // GlStateManager.translate(0.5F, 0.575F, 0.5F);
            poseStack.translate(0.5D, 1.2D, 0.5D);

            // 绕Y轴旋转 (公转)
            // GlStateManager.rotate(angles[i] + (float) time, 0F, 1F, 0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angles[i] + (float) time));

            // 平移到轨道位置 (径向偏移)
            // GlStateManager.translate(0.275F, -0.2F, 0.275F);
            poseStack.translate(0.275D, -0.2D, 0.275D);

            // 物品的固定自转角度 (使其面向外侧)
            // GlStateManager.rotate(22.5F, 0F, 1F, 0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(22.5F));

            // 悬浮/上下浮动 (正弦波运动)
            // GlStateManager.translate(0D, 0.0375 * Math.sin((time + i * 10) / 5D), 0F);
            double bobbing = 0.0375 * Math.sin((time + i * 10) / 5.0D);
            poseStack.translate(0D, bobbing, 0D);

            // 修正物品方向（使其看起来像在地上）
            // GlStateManager.rotate(180F, 1F, 0F, 0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180F));
            // GlStateManager.rotate(180F, 0F, 0F, 1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180F));

            // 渲染物品，使用 ItemDisplayContext.GROUND 模拟掉落物品的渲染效果
            // Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GROUND, // 对应 ItemCameraTransforms.TransformType.GROUND
                    packedLight,
                    packedOverlay,
                    poseStack,
                    multiBufferSource,
                    level,
                    (int) i // item ID/seed is typically an integer, using slot index here
            );

            poseStack.popPose(); // GlStateManager.popMatrix();
        }
    }
}