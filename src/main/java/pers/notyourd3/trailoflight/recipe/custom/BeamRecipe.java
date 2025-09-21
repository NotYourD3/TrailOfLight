package pers.notyourd3.trailoflight.recipe.custom;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import pers.notyourd3.trailoflight.recipe.ModRecipeSerializers;
import pers.notyourd3.trailoflight.recipe.ModRecipeTypes;

import java.awt.*;
import java.util.List;


public class BeamRecipe implements Recipe<BeamRecipeInput> {
    private final Color colorMin;
    private final Color colorMax;
    private final List<Ingredient> inputItem;
    private final int alpha;
    private final ItemStack result;

    public BeamRecipe(Color colorMin, Color colorMax, List<Ingredient> inputItem, int alpha, ItemStack result) {
        this.colorMin = colorMin;
        this.colorMax = colorMax;
        this.inputItem = inputItem;
        this.alpha = alpha;
        this.result = result;
    }

    public List<Ingredient> getInputItem() {
        return inputItem;
    }

    public Color getColorMin() {
        return colorMin;
    }

    public Color getColorMax() {
        return colorMax;
    }

    public int getAlpha() {
        return alpha;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public boolean matches(BeamRecipeInput input, Level level) {
        if (input.size() != inputItem.size()) return false;
        return input.beam().isInRange(colorMin, colorMax) && RecipeMatcher.findMatches(input.items(), this.inputItem) != null && input.alpha() >= alpha;
    }

    @Override
    public ItemStack assemble(BeamRecipeInput beamRecipeInput, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<BeamRecipeInput>> getSerializer() {
        return ModRecipeSerializers.BEAM.get();
    }

    @Override
    public RecipeType<? extends Recipe<BeamRecipeInput>> getType() {
        return ModRecipeTypes.BEAM_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return null;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }
}
