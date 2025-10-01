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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import pers.notyourd3.trailoflight.block.entity.custom.ChargerEntity;
import pers.notyourd3.trailoflight.block.entity.render.state.ChargerRenderState;

import javax.annotation.Nullable;

public class ChargerRenderer implements BlockEntityRenderer<ChargerEntity, ChargerRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ChargerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    /*
    OLD RENDERING CODE
    @Override
    public void render(ChargerEntity chargerEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1, Vec3 vec3) {
        ItemStack stack = chargerEntity.getStack();
        Level level = chargerEntity.getLevel();
        long gametime = level != null ? level.getGameTime() : 0;
        float rotation = gametime % 360;
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.2, 0.5);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            //BakedModel bakedModel = itemRenderer.getModel(stack,level,null,0);
            //itemRenderer.render(stack, ItemDisplayContext.FIXED,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,bakedModel);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, i, i1, poseStack, multiBufferSource, chargerEntity.getLevel(), 1);
            poseStack.popPose();
        }
    }
    */
    @Override
    public ChargerRenderState createRenderState() {
        return new ChargerRenderState();
    }

    @Override
    public void extractRenderState(ChargerEntity blockEntity, ChargerRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);
        renderState.stack = blockEntity.getStack();
        renderState.level = blockEntity.getLevel();
        renderState.partialTicks = partialTick;
    }

    @Override
    public void submit(ChargerRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        ItemStack stack = renderState.stack;
        Level level = renderState.level;
        float gametime = level != null ? level.getGameTime() + renderState.partialTicks : 0;
        float rotation = gametime % 360;
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.2, 0.5);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            ItemStackRenderState state = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(state, stack, ItemDisplayContext.FIXED, level, null, 0);
            state.submit(poseStack, submitNodeCollector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
