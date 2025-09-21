package pers.notyourd3.trailoflight.recipe.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import pers.notyourd3.trailoflight.feature.BeamCodec;

public class BeamRecipeSerializer implements RecipeSerializer<BeamRecipe> {
    public static MapCodec<BeamRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BeamCodec.COLOR_CODEC.fieldOf("color_min").forGetter(BeamRecipe::getColorMin),
            BeamCodec.COLOR_CODEC.fieldOf("color_max").forGetter(BeamRecipe::getColorMax),
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(BeamRecipe::getInputItem),
            Codec.INT.fieldOf("alpha").forGetter(BeamRecipe::getAlpha),
            ItemStack.CODEC.fieldOf("result").forGetter(BeamRecipe::getResult)
    ).apply(inst, BeamRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,BeamRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    BeamCodec.COLOR_STREAM_CODEC, BeamRecipe::getColorMin,
                    BeamCodec.COLOR_STREAM_CODEC, BeamRecipe::getColorMax,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), BeamRecipe::getInputItem,
                    ByteBufCodecs.VAR_INT, BeamRecipe::getAlpha,
                    ItemStack.STREAM_CODEC, BeamRecipe::getResult,
                    BeamRecipe::new
            );

    @Override
    public MapCodec<BeamRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BeamRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}