package pers.notyourd3.trailoflight.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import pers.notyourd3.trailoflight.block.entity.IBeamHandler;
import pers.notyourd3.trailoflight.network.PacketLaserFX;
import pers.notyourd3.trailoflight.recipe.ModRecipeTypes;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipe;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipeInput;

import java.awt.*;
import java.util.Collections;
import java.util.Optional;


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
    public Boolean isInRange(Color colorMin, Color colorMax){
        return color.getRed() >= colorMin.getRed() && color.getRed() <= colorMax.getRed() &&
                color.getGreen() >= colorMin.getGreen() && color.getGreen() <= colorMax.getGreen() &&
                color.getBlue() >= colorMin.getBlue() && color.getBlue() <= colorMax.getBlue() &&
                color.getAlpha() >= colorMin.getAlpha() && color.getAlpha() <= colorMax.getAlpha();
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
        if(trace instanceof BlockHitResult){
            BlockPos pos = BlockPos.containing(endLoc);
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof IBeamHandler) {
                ((IBeamHandler) state.getBlock()).onBeam(level, pos, this);
            }
        }
        if (trace instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof ItemEntity itemEntity) {
                BeamRecipeInput input = new BeamRecipeInput(this,
                        Collections.singletonList(itemEntity.getItem()),
                        this.color.getAlpha());
                Optional<RecipeHolder<BeamRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(
                        ModRecipeTypes.BEAM_TYPE.get(), input, level
                );
                ItemStack result = optional.map(RecipeHolder::value)
                        .map(e-> e.assemble(input,level.registryAccess()))
                        .orElse(ItemStack.EMPTY);
                if (!result.isEmpty()) {
                    ItemEntity entity2 = new ItemEntity(level,
                            endLoc.x, endLoc.y, endLoc.z, result);
                    level.addFreshEntity(entity2);
                    itemEntity.setItem(itemEntity.getItem().consumeAndReturn(itemEntity.getItem().getCount()-1,null));
                }
            }
        }
        PacketDistributor.sendToAllPlayers(new PacketLaserFX(initLoc, endLoc, color));
    }

    public Beam createSimilarBeam(Vec3 slope) {
        return new Beam(endLoc, slope, level, color).increaseLife();
    }

}
