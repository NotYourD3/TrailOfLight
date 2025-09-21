package pers.notyourd3.trailoflight.recipe.custom;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import pers.notyourd3.trailoflight.feature.Beam;

import java.util.List;

public record BeamRecipeInput(Beam beam, List<ItemStack> items, int alpha) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }
}
