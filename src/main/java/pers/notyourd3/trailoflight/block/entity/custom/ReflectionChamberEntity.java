package pers.notyourd3.trailoflight.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import pers.notyourd3.trailoflight.block.entity.ModBlockEntities;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;
import pers.notyourd3.trailoflight.feature.RotationHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectionChamberEntity extends BlockEntity {
    public Set<Beam> toReflect = new HashSet<>();

    public ReflectionChamberEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFLECTION_CHAMBER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ReflectionChamberEntity entity) {
        Set<Beam> beams = entity.toReflect;
        if (beams.isEmpty()) return;
        List<Vec3> angles = new ArrayList<>();
        angles.addAll(beams.stream().map(beam -> beam.rotation).toList());
        int red = 0, green = 0, blue = 0, alpha = 0;
        for (Beam beam : beams) {
            Color color = beam.color;
            int colorCount = 0;
            if (color.getRed() > 0) colorCount++;
            if (color.getGreen() > 0) colorCount++;
            if (color.getBlue() > 0) colorCount++;
            if (colorCount < 0) continue;

            red += color.getRed() * color.getAlpha() / 255 / colorCount;
            green += color.getGreen() * color.getAlpha() / 255 / colorCount;
            blue += color.getBlue() * color.getAlpha() / 255 / colorCount;
            alpha += color.getAlpha();
        }
        red = Math.min(red / beams.size(), 255);
        green = Math.min(green / beams.size(), 255);
        blue = Math.min(blue / beams.size(), 255);
        float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
        Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));
        Beam beam = new Beam(pos.getCenter(), RotationHelper.averageDirection(angles), level, color);
        BeamManager.INSTANCE.addBeam(beam);
        entity.toReflect.clear();
    }

    public void onBeam(Beam beam) {
        toReflect.add(beam);
    }
}
