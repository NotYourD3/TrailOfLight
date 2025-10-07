package pers.notyourd3.trailoflight.item.custom;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
    public void onSpawn(Beam beam,ItemStack stack) {
        beam.fire(hitResult -> {
            if (hitResult instanceof BlockHitResult hit) {
                if(!beam.level.getBlockState(hit.getBlockPos()).is(Blocks.AIR)){
                FakePlayer player = new FakePlayer((ServerLevel) beam.level,new GameProfile(UUID.randomUUID(), "JMComic350234"));
                player.setGameMode(GameType.SURVIVAL);
                player.setItemInHand(InteractionHand.MAIN_HAND,stack);
                ServerPlayerGameMode mode = player.gameMode;
                mode.destroyBlock(hit.getBlockPos());
               // beam.level.destroyBlock(hit.getBlockPos(), true, player);
                }
            }
        });
    }
    @Override
    public Pair<Color, Color> getColorRange() {
        return new Pair<>(new Color(0,0,0,0),new Color(255,255,255,255));
    }
}
