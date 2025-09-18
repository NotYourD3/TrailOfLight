package pers.notyourd3.trailoflight.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import pers.notyourd3.trailoflight.block.entity.IBeamHandler;
import pers.notyourd3.trailoflight.network.PacketLaserFX;

import java.awt.*;


public class Beam {
    private final double range = 200;
    public static final int MAX_BOUNCES = 8; // 最大反弹次数
    public Vec3 initLoc;
    public Vec3 endLoc;
    public Vec3 rotation;
    public Level level;
    public Color color;
    public int life = 0;
    public HitResult trace;
    public Entity entityToSkip;

    public Beam(Vec3 start, Vec3 rotation, Level level) {
        this(start, rotation, level, Color.WHITE);
    }

    public Beam(Vec3 start, Vec3 rotation, Level level, Color color) {
        this.initLoc = start;
        this.rotation = rotation;
        this.level = level;
        this.color = color;
    }

    public Beam setAlpha(int alpha) {
        this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        return this;
    }

    public Beam increaseLife() {
        life++;
        return this;
    }

    public void spawn() {
        if (level.isClientSide()) return;
        if (life >= MAX_BOUNCES) return;
        trace = new RayTrace(level, rotation, initLoc, range)
                .setEntityFilter(entity -> {
                    if (entity == null) return true;
                    if (entityToSkip == null) return true;
                    return entity.getUUID() != entityToSkip.getUUID();
                })
                .trace();
        if (trace == null) return;
        this.endLoc = trace.getLocation();
        BlockPos pos = BlockPos.containing(endLoc);
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IBeamHandler) {
            ((IBeamHandler) state.getBlock()).onBeam(level, pos, this);
        }
        PacketDistributor.sendToAllPlayers(new PacketLaserFX(initLoc, endLoc, color));
    }

    public Beam createSimilarBeam(Vec3 slope) {
        return new Beam(endLoc, slope, level, color).increaseLife();
    }

}
