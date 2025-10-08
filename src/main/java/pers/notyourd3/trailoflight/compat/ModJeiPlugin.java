package pers.notyourd3.trailoflight.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import pers.notyourd3.trailoflight.Trailoflight;
import pers.notyourd3.trailoflight.block.ModBlocks;
import pers.notyourd3.trailoflight.recipe.ModRecipeTypes;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Trailoflight.MODID,"jei_plugin");
    }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BeamCategory());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
        List<BeamRecipe> recipes = recipeManager.getRecipes().stream()
                .filter(recipe -> recipe.value() instanceof BeamRecipe)
                .map(holder -> (BeamRecipe) holder.value())
                .toList();
        registration.addRecipes(BeamCategory.RECIPE_TYPE, recipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        //registration.addGuiContainerHandler(BeamCategory.RECIPE_TYPE,BeamCategory.);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        registration.addCraftingStation(BeamCategory.RECIPE_TYPE, ModBlocks.CHARGER.get(),ModBlocks.LASER_ASSEMBLY_TABLE.get());

    }
}