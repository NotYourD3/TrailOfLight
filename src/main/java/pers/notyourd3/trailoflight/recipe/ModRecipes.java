package pers.notyourd3.trailoflight.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import pers.notyourd3.trailoflight.item.ModItems;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipeBuilder;

import java.awt.*;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ModRecipes extends RecipeProvider {

    protected ModRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        new BeamRecipeBuilder(new ItemStack(ModItems.LAMBENT_CRYSTAL_DUST.get()),
                Collections.singletonList(Ingredient.of(Items.GLOWSTONE_DUST)),
                new Color(0, 0, 0, 0),
                new Color(255, 255, 255, 255),
                16).save(output);
    }
    // The runner to add to the data generator
    public static class Runner extends RecipeProvider.Runner {
        // Get the parameters from the `GatherDataEvent`s.
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new ModRecipes(provider, output);
        }

        @Override
        public String getName() {
            return "";
        }
    }


}
