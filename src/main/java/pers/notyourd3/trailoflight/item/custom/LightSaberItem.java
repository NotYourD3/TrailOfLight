package pers.notyourd3.trailoflight.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import pers.notyourd3.trailoflight.item.ModDataComponents;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;

public class LightSaberItem extends Item implements IChargableItem{

    public LightSaberItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxAlpha() {
        return 40000;
    }
    @Override
    public boolean isFoil(ItemStack stack){
        return getEnabled(stack);
    }
    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(getEnabled(stack)){
            IChargableItem.decreaseBeam(stack,400);
        }
    }
    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        return getEnabled(Objects.requireNonNull(damageSource.getWeaponItem()))?10.0F:0F;
    }
    private static Boolean getEnabled(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.IS_ACTIVE.get(), new ModDataComponents.isActive(false)).b();
    }

    public void changeState(ItemStack stack,boolean bool){
        stack.set(ModDataComponents.IS_ACTIVE.get(),new ModDataComponents.isActive(bool));

    }
    public void changeState(ItemStack stack) {
        changeState(stack,!getEnabled(stack));
    }
    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide()) {
            changeState(itemstack);
        }
        return InteractionResult.SUCCESS;
    }
}