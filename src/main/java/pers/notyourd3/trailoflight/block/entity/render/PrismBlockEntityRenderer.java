package pers.notyourd3.trailoflight.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import pers.notyourd3.trailoflight.block.custom.PrismBlock;
import pers.notyourd3.trailoflight.block.entity.custom.PrismBlockEntity;

public class PrismBlockEntityRenderer implements BlockEntityRenderer<PrismBlockEntity> {
    private static final ResourceLocation PRISM_TEXTURE = ResourceLocation.fromNamespaceAndPath("trailoflight", "textures/block/prism.png");

    public PrismBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PrismBlockEntity prismEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, Vec3 vec3) {
        poseStack.pushPose();

        // 移动到方块中心以进行旋转
        poseStack.translate(0.5, 0.5, 0.5);

        // 修复旋转逻辑
        // 原始旋转: prismEntity.getBlockState().getValue(PrismBlock.FACING).toYRot()
        // 物理逻辑中的旋转 (PrismBlock.java):
        // EAST: 0 deg
        // WEST: 180 deg
        // NORTH: 90 deg
        // SOUTH: 270 deg
        // toYRot() 的值:
        // EAST: 270, WEST: 90, NORTH: 180, SOUTH: 0
        // 转换公式: new_rot = 270.0F - toYRot()
        float rotation = 90.0F + prismEntity.getBlockState().getValue(PrismBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // 移回方块原点
        poseStack.translate(-0.5, -0.5, -0.5);

        VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(PRISM_TEXTURE));
        Matrix4f matrix = poseStack.last().pose();

        // 定义与物理逻辑 (PrismBlock.java) 匹配的顶点
        // a = (0,0,0), b = (1,0,0.5), c = (0,0,1)
        // A = (0,1,0), B = (1,1,0.5), C = (0,1,1)
        float x1 = 0f, y1 = 0f, z1 = 0f;    // a
        float x2 = 1f, y2 = 0f, z2 = 0.5f;  // b
        float x3 = 0f, y3 = 0f, z3 = 1f;    // c
        float x4 = 0f, y4 = 1f, z4 = 0f;    // A
        float x5 = 1f, y5 = 1f, z5 = 0.5f;  // B
        float x6 = 0f, y6 = 1f, z6 = 1f;    // C

        // 定义一个半透明的颜色
        float r = 0.6f, g = 0.8f, b = 1.0f, a = 0.6f;

        // 绘制底部三角形 (a, b, c)
        // 顶点顺序 (a, b, c) -> 法线 (0, -1, 0)
        drawTriangle(consumer, matrix,
                x1, y1, z1, x2, y2, z2, x3, y3, z3,
                r, g, b, a,
                0f, 0f, 1f, 0.5f, 0f, 1f, // UVs
                packedLight, packedOverlay);

        // 绘制顶部三角形 (A, C, B)
        // 顶点顺序 (A, C, B) -> 法线 (0, 1, 0)
        drawTriangle(consumer, matrix,
                x4, y4, z4, x6, y6, z6, x5, y5, z5,
                r, g, b, a,
                0f, 0f, 0f, 1f, 1f, 0.5f, // UVs
                packedLight, packedOverlay);


        // 绘制侧面
        // 背面 (a, c, C, A) - 位于 x=0 平面
        drawQuad(consumer, matrix,
                x1, y1, z1, x3, y3, z3, x6, y6, z6, x4, y4, z4,
                r, g, b, a,
                0f, 1f, 1f, 0f, // UVs
                packedLight, packedOverlay);

        // 斜面 1 (a, A, B, b)
        drawQuad(consumer, matrix,
                x1, y1, z1, x4, y4, z4, x5, y5, z5, x2, y2, z2,
                r, g, b, a,
                0f, 1f, 1f, 0f, // UVs
                packedLight, packedOverlay);

        // 斜面 2 (b, B, C, c)
        drawQuad(consumer, matrix,
                x2, y2, z2, x5, y5, z5, x6, y6, z6, x3, y3, z3,
                r, g, b, a,
                0f, 1f, 1f, 0f, // UVs
                packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void drawTriangle(VertexConsumer consumer, Matrix4f matrix,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float r, float g, float b, float a,
                              float u1, float v1, float u2, float v2, float u3, float v3,
                              int packedLight, int packedOverlay) {
        float dx1 = x2 - x1, dy1 = y2 - y1, dz1 = z2 - z1;
        float dx2 = x3 - x1, dy2 = y3 - y1, dz2 = z3 - z1;
        float nx = dy1 * dz2 - dz1 * dy2;
        float ny = dz1 * dx2 - dx1 * dz2;
        float nz = dx1 * dy2 - dy1 * dx2;
        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0) { nx /= length; ny /= length; nz /= length; }

        consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
    }

    private void drawQuad(VertexConsumer consumer, Matrix4f matrix,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float r, float g, float b, float a,
                          float u1, float v1, float u2, float v2,
                          int packedLight, int packedOverlay) {
        float dx1 = x2 - x1, dy1 = y2 - y1, dz1 = z2 - z1;
        float dx2 = x4 - x1, dy2 = y4 - y1, dz2 = z4 - z1;
        float nx = dy1 * dz2 - dz1 * dy2;
        float ny = dz1 * dx2 - dx1 * dz2;
        float nz = dx1 * dy2 - dy1 * dx2;
        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0) { nx /= length; ny /= length; nz /= length; }

        consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u1, v2).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u2, v2).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x4, y4, z4).setColor(r, g, b, a).setUv(u2, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
    }
}