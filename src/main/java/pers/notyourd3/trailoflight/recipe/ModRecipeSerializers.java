package pers.notyourd3.trailoflight.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.Trailoflight;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipe;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipeSerializer;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Trailoflight.MODID);

    public static final Supplier<RecipeSerializer<BeamRecipe>> BEAM =
            RECIPE_SERIALIZERS.register("beam", BeamRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
