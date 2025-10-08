package pers.notyourd3.trailoflight.item.custom;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import pers.notyourd3.trailoflight.feature.Beam;

import java.awt.*;
import java.util.Optional;

public class LensSmeltingItem extends AbstractLensItem {
    public LensSmeltingItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onSpawn(Beam beam, ItemStack stack) {
        beam.fire(hitResult -> {
            if (hitResult instanceof BlockHitResult hit && Math.random() < 0.0005 * beam.color.getAlpha()) {
                if (beam.level.getBlockState(hit.getBlockPos()).is(Tags.Blocks.OBSIDIANS) ||
                        beam.level.getBlockState(hit.getBlockPos()).is(Tags.Blocks.STONES)) {
                    beam.level.setBlockAndUpdate(hit.getBlockPos(), Blocks.LAVA.defaultBlockState());
                }
            }
        }, entity -> !(entity instanceof ItemEntity));
        beam.getEntitiesInRange().stream()
                .filter(entity -> entity instanceof ItemEntity)
                .map(entity -> (ItemEntity) entity)
                .forEach(itemEntity -> {
                    if (Math.random() < 0.0005 * beam.color.getAlpha()) {
                        ItemStack originalStack = itemEntity.getItem();
                        SingleRecipeInput input = new SingleRecipeInput(originalStack);
                        Optional<ItemStack> smokedResult = beam.level.getServer().getRecipeManager()
                                .getRecipeFor(RecipeType.SMOKING, input, beam.level)
                                .map(holder -> new ItemStack(Items.CHARCOAL));
                        ItemStack smeltedResult = smokedResult.orElseGet(() ->
                                beam.level.getServer().getRecipeManager()
                                        .getRecipeFor(RecipeType.BLASTING, input, beam.level)
                                        .map(holder -> holder.value().assemble(input, beam.level.registryAccess()).copy()).orElse(ItemStack.EMPTY)
                        );
                        if (!smeltedResult.isEmpty()) {
                            ItemEntity newEntity = new ItemEntity(
                                    beam.level,
                                    itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                                    smeltedResult
                            );
                            newEntity.setDefaultPickUpDelay();
                            beam.level.addFreshEntity(newEntity);
                            originalStack.consume(1, null);
                            ParticleUtils.spawnParticles(beam.level, itemEntity.getOnPos(), 15, 0.5, 0.5, false, ParticleTypes.FLAME);
                        }
                    }
                });
    }

    @Override
    public Pair<Color, Color> getColorRange() {
        return new Pair<>(new Color(255, 0, 0, 32), new Color(255, 50, 50, 255));
    }
}
