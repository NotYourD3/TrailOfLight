package pers.notyourd3.trailoflight.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import pers.notyourd3.trailoflight.block.entity.custom.MirrorEntity;
import pers.notyourd3.trailoflight.block.entity.render.state.MirrorRenderState;

// 更改泛型参数以使用新的 Render State 类
public class MirrorRenderer implements BlockEntityRenderer<MirrorEntity, MirrorRenderState> {
    private static final ResourceLocation MIRROR_TEXTURE = ResourceLocation.fromNamespaceAndPath("trailoflight", "textures/block/mirror_face.png");

    public MirrorRenderer(BlockEntityRendererProvider.Context context) {
    }


    @Override
    public MirrorRenderState createRenderState() {
        return new MirrorRenderState();
    }


    @Override
    public void extractRenderState(MirrorEntity blockEntity, MirrorRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.rotY = blockEntity.getRotY();
        renderState.rotX = blockEntity.getRotX();
    }


    @Override
    public void submit(MirrorRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        float rotY = renderState.rotY;
        float rotX = renderState.rotX;
        int packedLight = renderState.lightCoords;
        int packedOverlay = OverlayTexture.NO_OVERLAY;

        poseStack.translate(0.5, 0.5, 0.5);

        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));

        poseStack.translate(-0.5, -0.5, -0.5);


        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(MIRROR_TEXTURE));
        Matrix4f matrix = poseStack.last().pose();

        float size = 0.4375f; // 7/16，留出边框
        float depth = 0.01f;  // 镜子厚度

        // back
        drawQuad(consumer, matrix,
                0.5f - size, 0.5f - size, 0.5f + depth, // 左下后
                0.5f + size, 0.5f - size, 0.5f + depth, // 右下后
                0.5f + size, 0.5f + size, 0.5f + depth, // 右上后
                0.5f - size, 0.5f + size, 0.5f + depth, // 左上后
                0.2f, 0.2f, 0.2f, 1.0f,
                0.0f, 0.5f, 1.0f, 1.0f, // UV坐标
                packedLight, packedOverlay);

        // front
        drawQuad(consumer, matrix,
                0.5f - size, 0.5f - size, 0.5f - depth, // 左下前
                0.5f - size, 0.5f + size, 0.5f - depth, // 左上前
                0.5f + size, 0.5f + size, 0.5f - depth, // 右上前
                0.5f + size, 0.5f - size, 0.5f - depth, // 右下前
                1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.5f,
                packedLight, packedOverlay);

        poseStack.popPose();
    }


    private void drawQuad(VertexConsumer consumer, Matrix4f matrix,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float r, float g, float b, float a,
                          float u1, float v1, float u2, float v2,
                          int packedLight, int packedOverlay) {

        // 计算法线向量（用于光照）
        float dx1 = x2 - x1;
        float dy1 = y2 - y1;
        float dz1 = z2 - z1;
        float dx2 = x4 - x1;
        float dy2 = y4 - y1;
        float dz2 = z4 - z1;

        float nx = dy1 * dz2 - dz1 * dy2;
        float ny = dz1 * dx2 - dx1 * dz2;
        float nz = dx1 * dy2 - dy1 * dx2;

        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0) {
            nx /= length;
            ny /= length;
            nz /= length;
        }

        // 绘制四个顶点（两个三角形组成一个四边形）
        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(r, g, b, a)
                .setUv(u1, v1)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(nx, ny, nz);

        consumer.addVertex(matrix, x2, y2, z2)
                .setColor(r, g, b, a)
                .setUv(u2, v1)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(nx, ny, nz);

        consumer.addVertex(matrix, x3, y3, z3)
                .setColor(r, g, b, a)
                .setUv(u2, v2)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(nx, ny, nz);

        consumer.addVertex(matrix, x4, y4, z4)
                .setColor(r, g, b, a)
                .setUv(u1, v2)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(nx, ny, nz);
    }
}