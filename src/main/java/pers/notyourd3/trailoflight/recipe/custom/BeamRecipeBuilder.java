package pers.notyourd3.trailoflight.recipe.custom;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BeamRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private final List<Ingredient> input;
    private final Color colorMin;
    private final Color colorMax;
    private final int alpha;

    // 构造函数通常接受结果 ItemStack。
    // 另外，也可以使用静态构建器方法。
    public BeamRecipeBuilder(ItemStack result, List<Ingredient> input, Color colorMin, Color colorMax, int alpha) {
        this.result = result;
        this.input = input;
        this.colorMin = colorMin;
        this.colorMax = colorMax;
        this.alpha = alpha;
    }

    // 此方法为配方进度（advancement）添加一个条件（criterion）。
    @Override
    public BeamRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    // 此方法添加一个配方书组。如果你不想使用配方书组，
    // 删除 this.group 字段并让此方法成为无操作（即返回 this）。
    @Override
    public BeamRecipeBuilder group(@Nullable String group) {
        return this;
    }

    // 原版需要一个 Item，而不是 ItemStack。你仍然可以并且应该使用 ItemStack
    // 来序列化配方。
    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        BeamRecipe recipe = new BeamRecipe(colorMin, colorMax, input, alpha, result);
        recipeOutput.accept(resourceKey, recipe, advancement.build(resourceKey.location().withPrefix("recipes/")));
    }
}
