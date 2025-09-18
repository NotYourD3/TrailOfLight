package pers.notyourd3.trailoflight.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

import static net.minecraft.client.renderer.RenderPipelines.ENTITY_SNIPPET;
import static pers.notyourd3.trailoflight.Trailoflight.MODID;


public class LaserRenderer {
    public static final LaserRenderer INSTANCE = new LaserRenderer();

    Set<LaserRenderInfo> lasers = new HashSet<>();
    @SubscribeEvent
    public void load(LevelEvent.Load event){
        lasers.clear();
    }

    @SubscribeEvent
    public void unload(LevelEvent.Unload event){
        lasers.clear();
    }
    @SubscribeEvent
    public void onRenderWorldLast(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        PoseStack ps = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        ps.pushPose();
        ps.translate(-camPos.x, -camPos.y, -camPos.z);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = bufferSource.getBuffer(RenderType.celestial(ResourceLocation.fromNamespaceAndPath(MODID, "textures/misc/laser.png")));
        Matrix4f matrix = ps.last().pose();
        for (LaserRenderInfo laser : lasers) {
            drawLaser(builder, matrix, laser.start, laser.end, laser.color);
        }
        ps.popPose();
    }
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event){
        Iterator<LaserRenderInfo> iterator = lasers.iterator();
        while (iterator.hasNext()) {
            LaserRenderInfo laser = iterator.next();
            if (laser.flag>0) {
                laser.flag--;
            } else {
                iterator.remove();
            }
        }
    }
    private static void drawLaser(VertexConsumer consumer, Matrix4f matrix, Vec3 start, Vec3 end, Color color) {
        int alpha = Math.max(50, color.getAlpha());
        int addColorMin = 30 * alpha/255;
        color = new Color(Math.max(addColorMin, color.getRed()), Math.max(addColorMin, color.getGreen()), Math.max(addColorMin, color.getBlue()), alpha);
        double width = (0.25 * (double) color.getAlpha() / 255D) / 2.0;
        Vec3 d = new Vec3(0, width, 0);
        Vec3 d2 = new Vec3(width, 0, 0);
        Vec3 d3 = new Vec3(0, 0, width);

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();

        float vMin = 0, vMax = 1;
        float uMin = 0, uMax = 1;

        pos(consumer, matrix, start.add(d)).setColor(r, g, b, a).setUv(uMin,vMin);
        pos(consumer, matrix, start.subtract(d)).setColor(r, g, b, a).setUv(uMin,vMax);
        pos(consumer, matrix, end.add(d)).setColor(r, g, b, a).setUv(uMax,vMax);
        pos(consumer, matrix, end.subtract(d)).setColor(r, g, b, a).setUv(uMax,vMin);

        pos(consumer, matrix, start.add(d2)).setColor(r, g, b, a).setUv(uMin,vMin);
        pos(consumer, matrix, start.subtract(d2)).setColor(r, g, b, a).setUv(uMin,vMax);
        pos(consumer, matrix, end.add(d2)).setColor(r, g, b, a).setUv(uMax,vMax);
        pos(consumer, matrix, end.subtract(d2)).setColor(r, g, b, a).setUv(uMax,vMin);

        pos(consumer, matrix, start.add(d3)).setColor(r, g, b, a).setUv(uMin,vMin);
        pos(consumer, matrix, start.subtract(d3)).setColor(r, g, b, a).setUv(uMin,vMax);
        pos(consumer, matrix, end.add(d3)).setColor(r, g, b, a).setUv(uMax,vMax);
        pos(consumer, matrix, end.subtract(d3)).setColor(r, g, b, a).setUv(uMax,vMin);
    }
    private static VertexConsumer pos(VertexConsumer consumer,Matrix4f matrix,Vec3 pos){
        return consumer.addVertex(matrix,(float) pos.x, (float) pos.y, (float) pos.z).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0,1,0);
    }

    public void addLaser(Vec3 start, Vec3 end, Color color) {
        LaserRenderInfo newLaser = new LaserRenderInfo(start, end, color);
        if (lasers.contains(newLaser)) {
            for (LaserRenderInfo laser : lasers) {
                if (laser.equals(newLaser)) {
                    laser.flag = 2;
                    break;
                }
            }
        } else {
            lasers.add(newLaser);
        }
    }
    public void removeLaser(Vec3 start, Vec3 end, Color color) {
        lasers.remove(new LaserRenderInfo(start, end, color));
    }
    public void clearLasers() {
        lasers.clear();
    }
    public static class LaserRenderInfo {
        public Vec3 start, end;
        public Color color;
        public int flag;

        public LaserRenderInfo(Vec3 start, Vec3 end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color == null ? Color.WHITE : color;
            flag = 2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LaserRenderInfo that = (LaserRenderInfo) o;

            if (!start.equals(that.start)) return false;
            if (!end.equals(that.end)) return false;
            return color.equals(that.color);
        }

        @Override
        public int hashCode() {
            int result = start.hashCode();
            result = 31 * result + end.hashCode();
            result = 31 * result + color.hashCode();
            return result;
        }
    }
}