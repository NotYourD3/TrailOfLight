package pers.notyourd3.trailoflight.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import pers.notyourd3.trailoflight.block.ModBlocks;
import pers.notyourd3.trailoflight.item.ModItems;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipeBuilder;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
        new BeamRecipeBuilder(new ItemStack(ModBlocks.LASER_ASSEMBLY_TABLE.get()),
                Collections.singletonList(Ingredient.of(ModBlocks.CHARGER)),
                new Color(0, 0, 0, 0),
                new Color(255, 0, 255, 255),
                32000).save(output);
        new BeamRecipeBuilder(new ItemStack(ModItems.GLITTERING_INGOT.get()),
                Collections.singletonList(Ingredient.of(Items.COPPER_INGOT)),
                new Color(0, 0, 0, 0),
                new Color(255, 255, 255, 255),
                5000).save(output);
        new BeamRecipeBuilder(new ItemStack(ModBlocks.PRISM),
                Stream.of(ModItems.GLITTERING_INGOT,Items.GLASS,Items.GLASS,Items.GLASS,Items.GLASS,Items.GLASS).map(Ingredient::of).toList(),
                new Color(0, 0, 0, 0),
                new Color(255, 255, 255, 255),
                6400).save(output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModBlocks.LASER_GENERATOR.asItem())
                .pattern("GOG")
                .pattern("SIS")
                .pattern("GOG")
                .define('G', Items.RED_CONCRETE)
                .define('O', Items.GLOWSTONE)
                .define('S', Items.COAL_BLOCK)
                .define('I', Items.DISPENSER).unlockedBy("has_item", has(Items.GLOWSTONE)).save(output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.SCREW_DRIVER)
                .pattern("  D")
                .pattern("AD ")
                .pattern("BC ")
                .define('A', Items.HONEYCOMB)
                .define('B', Items.STICK)
                .define('C', Items.GREEN_DYE)
                .define('D', Items.IRON_INGOT)
                .unlockedBy("has_item", has(Items.HONEYCOMB))
                .save(output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModBlocks.CHARGER.asItem())
                .pattern("   ")
                .pattern("GGG")
                .pattern("GAG")
                .define('G', Items.GRAY_CONCRETE)
                .define('A', ModItems.LAMBENT_CRYSTAL_DUST)
                .unlockedBy("has_item", has(ModItems.LAMBENT_CRYSTAL_DUST))
                .save(output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModBlocks.MIRROR.asItem())
                .pattern("GOG")
                .pattern("GOG")
                .pattern("A A")
                .define('G', ModItems.GLITTERING_INGOT)
                .define('A', Items.BLACK_CONCRETE)
                .define('O', Tags.Items.GLASS_PANES)
                .unlockedBy("has_item", has(ModItems.GLITTERING_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.LIGHT_SABER)
                .pattern(" G ")
                .pattern(" G ")
                .pattern(" A ")
                .define('G', ModItems.GLITTERING_INGOT)
                .define('A', Items.BLAZE_ROD)
                .unlockedBy("has_item", has(ModItems.GLITTERING_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.LASER_POINTER)
                .pattern(" G ")
                .pattern("GGG")
                .pattern(" A ")
                .define('G', ModItems.GLITTERING_INGOT)
                .define('A', Items.IRON_INGOT)
                .unlockedBy("has_item", has(ModItems.GLITTERING_INGOT))
                .save(output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModBlocks.REFLECTION_CHAMBER.asItem())
                .pattern("GGG")
                .pattern("G G")
                .pattern("GGG")
                .define('G', ModItems.GLITTERING_INGOT)
                .unlockedBy("has_item", has(Items.GLOWSTONE_DUST))
                .save(output);

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
