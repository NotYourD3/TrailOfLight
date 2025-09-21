package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import pers.notyourd3.trailoflight.feature.Beam;
import pers.notyourd3.trailoflight.feature.BeamManager;

import java.awt.*;

public class LaserPointerItem extends Item implements IChargableItem {
    public LaserPointerItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxAlpha() {
        return 40000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) return;
        if (IChargableItem.getAlpha(stack) > 16) {
            Color color = IChargableItem.getBeamColor(stack);
            Beam beam = new Beam(livingEntity.getEyePosition(), livingEntity.getLookAngle(), level, new Color(color.getRed(), color.getGreen(), color.getBlue(), 16));
            beam.entityToSkip = livingEntity;
            BeamManager.INSTANCE.addBeam(beam);
            IChargableItem.decreaseBeam(stack, 16);

        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }
}
