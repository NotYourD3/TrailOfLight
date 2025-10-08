package pers.notyourd3.trailoflight.compat;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import pers.notyourd3.trailoflight.Trailoflight;
import pers.notyourd3.trailoflight.block.ModBlocks;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipe;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class BeamCategory implements IRecipeCategory<BeamRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Trailoflight.MODID, "beam");
    public static final IRecipeType<BeamRecipe> RECIPE_TYPE = IRecipeType.create(UID, BeamRecipe.class);
    @Override
    public int getWidth() {
        return 180;
    }

    @Override
    public int getHeight() {
        return 120;
    }
    @Override
    public IRecipeType<BeamRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.trailoflight.beam");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeamRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> inputs = recipe.getInputItem();
        double slice = 2 * Math.PI / inputs.size();
        for (int i = 0; i < inputs.size(); i++) {
            double angle = slice * i;
            int newX = (int) (82 + 40 * Math.cos(angle));
            int newY = (int) (51 + 40 * Math.sin(angle));
            builder.addSlot(RecipeIngredientRole.INPUT, newX, newY).add(inputs.get(i));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 82, 51).add(recipe.getResult());
    }
    @Override
    public void draw(BeamRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        @FunctionalInterface
        interface ColorBarDrawer {
            void draw(int y, String name, int min, int max, int fullColor);
        }
        int startX = 5;
        int startY = 5;
        int barWidth = 35;
        int barHeight = 4;
        int barSpacing = 3;
        int textStartX = startX + barWidth + 5;
        int minRed = recipe.getColorMin().getRed();
        int minGreen = recipe.getColorMin().getGreen();
        int minBlue = recipe.getColorMin().getBlue();
        int maxRed = recipe.getColorMax().getRed();
        int maxGreen = recipe.getColorMax().getGreen();
        int maxBlue = recipe.getColorMax().getBlue();
        int minAlpha = recipe.getColorMin().getAlpha();
        int maxAlpha = recipe.getColorMax().getAlpha();
        int alpha = recipe.getAlpha();
        ColorBarDrawer drawColorBar = (y, name, min, max, fullColor) -> {
            guiGraphics.fill(startX, y, startX + barWidth, y + barHeight, 0xFF404040);
            int minX = startX + (min * barWidth / 255);
            int maxX = startX + (max * barWidth / 255);
            guiGraphics.fill(minX, y, maxX, y + barHeight, fullColor);
            guiGraphics.drawString(Minecraft.getInstance().font, name, startX - 8, y + 1, fullColor, false);
            guiGraphics.drawString(Minecraft.getInstance().font, String.format("%s: %d-%d", name, min, max), textStartX, y + 1, fullColor, false);
        };
        int currentY = startY;
        drawColorBar.draw(currentY, "R", minRed, maxRed, 0xFFE00000); // Red
        currentY += barHeight + barSpacing;
        drawColorBar.draw(currentY, "G", minGreen, maxGreen, 0xFF00C000); // Green
        currentY += barHeight + barSpacing;
        drawColorBar.draw(currentY, "B", minBlue, maxBlue, 0xFF0000E0); // Blue
        currentY += barHeight + barSpacing;
        drawColorBar.draw(currentY, "A", minAlpha, maxAlpha, Color.white.getRGB());
        guiGraphics.drawString(Minecraft.getInstance().font, String.format("Total Alpha: %d", alpha), textStartX, 95, Color.white.getRGB(), false);
    }
}