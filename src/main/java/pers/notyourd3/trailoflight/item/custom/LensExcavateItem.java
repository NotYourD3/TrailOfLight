package pers.notyourd3.trailoflight.item.custom;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import pers.notyourd3.trailoflight.feature.Beam;

import java.awt.*;
import java.util.UUID;

public class LensExcavateItem extends AbstractLensItem {
    public LensExcavateItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onSpawn(Beam beam, ItemStack stack) {
        beam.fire(hitResult -> {
            if (hitResult instanceof BlockHitResult hit) {
                if (!beam.level.getBlockState(hit.getBlockPos()).is(Blocks.AIR)) {
                    float hardness = beam.level.getBlockState(hit.getBlockPos()).getDestroySpeed(beam.level, hit.getBlockPos());
                    if (hardness < 0.001F) {
                        return;
                    }
                    float A = 1+(float)stack.getEnchantmentLevel(beam.level.registryAccess().get(Enchantments.EFFICIENCY).orElseThrow());
                    float successChance = (5.0F * (float) beam.color.getAlpha()) / (hardness * 20.0F)/15*A;
                    successChance = Math.min(1.0F, successChance);
                    if (Math.random() > successChance) {
                        return;
                    }
                    FakePlayer player = new FakePlayer((ServerLevel) beam.level, new GameProfile(UUID.randomUUID(), "JMComic350234"));
                    player.setGameMode(GameType.SURVIVAL);
                    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                    ServerPlayerGameMode mode = player.gameMode;
                    mode.destroyBlock(hit.getBlockPos());
                }
            }
        });
    }

    @Override
    public Pair<Color, Color> getColorRange() {
        return new Pair<>(new Color(0, 0, 0, 0), new Color(255, 255, 255, 255));
    }

}
