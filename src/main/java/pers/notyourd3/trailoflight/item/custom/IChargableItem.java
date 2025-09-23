package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.world.item.ItemStack;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.item.ModDataComponents;

import java.awt.*;

public interface IChargableItem {


    static void addBeam(ItemStack stack, Beam beam) {
        int maxAlpha;
        if (stack.getItem() instanceof IChargableItem chargableItem) {
            maxAlpha = chargableItem.getMaxAlpha();
        } else {
            maxAlpha = 255;
        }

        stack.update(
                ModDataComponents.BEAM_STORAGE_INFO.get(),
                new ModDataComponents.BeamStorageInfoRecord(new Color(0, 0, 0, 0), 0, maxAlpha),
                data -> {
                    int newAlpha = data.alpha() + beam.color.getAlpha();
                    if (newAlpha == 0) {
                        return new ModDataComponents.BeamStorageInfoRecord(new Color(0, 0, 0, 0), 0, maxAlpha);
                    }
                    int newRed = (int) (((long) data.alpha() * data.color().getRed() + (long) beam.color.getAlpha() * beam.color.getRed()) / newAlpha);
                    int newGreen = (int) (((long) data.alpha() * data.color().getGreen() + (long) beam.color.getAlpha() * beam.color.getGreen()) / newAlpha);
                    int newBlue = (int) (((long) data.alpha() * data.color().getBlue() + (long) beam.color.getAlpha() * beam.color.getBlue()) / newAlpha);
                    if (newAlpha > data.maxAlpha()) {
                        newAlpha = data.maxAlpha();
                    }
                    float[] hsbvals = Color.RGBtoHSB(newRed, newGreen, newBlue, null);
                    Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
                    Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
                    return new ModDataComponents.BeamStorageInfoRecord(newColor, newAlpha, data.maxAlpha());
                }
        );
    }

    static void decreaseBeam(ItemStack stack, int alphaDecrease) {
        int maxAlpha = 255; // 默认值
        if (stack.getItem() instanceof IChargableItem chargableItem) {
            maxAlpha = chargableItem.getMaxAlpha();
        }

        stack.update(
                ModDataComponents.BEAM_STORAGE_INFO.get(),
                new ModDataComponents.BeamStorageInfoRecord(Color.WHITE, 0, maxAlpha),
                data -> {
                    int newAlpha = data.alpha() - alphaDecrease;
                    if (newAlpha < 0) {
                        newAlpha = 0;
                    }
                    return new ModDataComponents.BeamStorageInfoRecord(data.color(), newAlpha, data.maxAlpha());
                }
        );
    }

    static Color getBeamColor(ItemStack stack) {
        ModDataComponents.BeamStorageInfoRecord data = stack.get(ModDataComponents.BEAM_STORAGE_INFO.get());
        if (data == null) {
            return Color.WHITE;
        }
        return data.color();
    }

    static int getAlpha(ItemStack stack) {
        ModDataComponents.BeamStorageInfoRecord data = stack.get(ModDataComponents.BEAM_STORAGE_INFO.get());
        if (data == null) {
            return 0;
        }
        return data.alpha();
    }

    int getMaxAlpha();

}