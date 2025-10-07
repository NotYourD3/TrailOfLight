package pers.notyourd3.trailoflight.item.consume;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import pers.notyourd3.trailoflight.item.custom.IChargableItem;

import java.awt.*;

public record EnergyDrinkConsumeEffect(int dummy) implements ConsumeEffect {


    public static StandardColor findClosestStandardColor(Color color) {
        Color[] standardColors = {
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.CYAN,
                Color.MAGENTA,
                Color.YELLOW,
                Color.WHITE
        };
        StandardColor[] standardEnums = {
                StandardColor.RED,
                StandardColor.GREEN,
                StandardColor.BLUE,
                StandardColor.CYAN,
                StandardColor.MAGENTA,
                StandardColor.YELLOW,
                StandardColor.WHITE
        };
        int r1 = color.getRed();
        int g1 = color.getGreen();
        int b1 = color.getBlue();
        double minDistanceSq = Double.MAX_VALUE;
        StandardColor closestColor = StandardColor.WHITE;
        for (int i = 0; i < standardColors.length; i++) {
            Color standardColor = standardColors[i];
            int r2 = standardColor.getRed();
            int g2 = standardColor.getGreen();
            int b2 = standardColor.getBlue();
            double distanceSq = Math.pow(r1 - r2, 2) +
                    Math.pow(g1 - g2, 2) +
                    Math.pow(b1 - b2, 2);
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closestColor = standardEnums[i];
            }
        }

        return closestColor;
    }

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return ModConsumeEffectTypes.ENERGY_DRINK.get();
    }

    @Override
    public boolean apply(Level level, ItemStack itemStack, LivingEntity livingEntity) {
        if (IChargableItem.getAlpha(itemStack) < 10000) return false;
        switch (findClosestStandardColor(IChargableItem.getBeamColor(itemStack))) {
            case RED -> {
                livingEntity.lavaHurt();
                livingEntity.setRemainingFireTicks(40);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 200, 3));
            }
            case GREEN -> {
                BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), livingEntity.level(), livingEntity.getOnPos(), null);
                livingEntity.level().levelEvent(1505, livingEntity.getOnPos(), 15);
            }
            case BLUE -> {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 800, 0));
            }
            case CYAN -> {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 140, 2));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 4));
            }
            case MAGENTA -> {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 2));
            }
            case YELLOW -> {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.HASTE, 200, 3));
            }
        }
        return true;
    }

    public enum StandardColor {
        RED,
        GREEN,
        BLUE,
        CYAN,
        MAGENTA,
        YELLOW,
        WHITE
    }
}
