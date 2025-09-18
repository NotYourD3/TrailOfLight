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
import pers.notyourd3.trailoflight.block.entity.custom.ChargerEntity;

public class ChargerEntityRenderer implements BlockEntityRenderer<ChargerEntity> {
    public ChargerEntityRenderer(BlockEntityRendererProvider.Context context) {
    }
    @Override
    public void render(ChargerEntity chargerEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1, Vec3 vec3) {
        ItemStack stack = chargerEntity.getStack();
        Level level = chargerEntity.getLevel();
        long gametime = level != null ? level.getGameTime() : 0;
        float rotation = gametime % 360;
        if(!stack.isEmpty()){
            poseStack.pushPose();
            poseStack.translate(0.5,1.2,0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            /*
            BakedModel bakedModel = itemRenderer.getModel(stack,level,null,0);
            itemRenderer.render(stack, ItemDisplayContext.FIXED,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,bakedModel);
             */
            itemRenderer.renderStatic(stack,ItemDisplayContext.FIXED,i,i1,poseStack,multiBufferSource,chargerEntity.getLevel(),1);
            poseStack.popPose();
        }
    }
}
