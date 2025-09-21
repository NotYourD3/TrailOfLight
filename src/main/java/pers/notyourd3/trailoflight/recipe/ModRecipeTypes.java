package pers.notyourd3.trailoflight.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import pers.notyourd3.trailoflight.Trailoflight;
import pers.notyourd3.trailoflight.recipe.custom.BeamRecipe;

import java.util.function.Supplier;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Trailoflight.MODID);

    public static final Supplier<RecipeType<BeamRecipe>> BEAM_TYPE =
            RECIPE_TYPES.register(
                    "beam_type",
                    RecipeType::simple
            );
    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
