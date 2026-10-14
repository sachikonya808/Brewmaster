package net.sachikonya.brewmaster.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;
import net.minecraft.world.level.Level;
import net.sachikonya.brewmaster.potion.PotionMixture;

public class BrewingRecipeLookup {
    public static Optional<RecipeHolder<BrewingRecipe>> findRecipe(Level level, ItemStack ingredient) {
        if (ingredient.isEmpty()) {
            return Optional.empty();
        }

        BrewingRecipeInput input = new BrewingRecipeInput(ingredient);

        return level.getRecipeManager().getRecipeFor(ModRecipes.BREWING_TYPE.get(),input,level);
    }

    public static PotionMixture applyRecipe(PotionMixture mixture, BrewingRecipe recipe) {
        PotionMixture result = mixture;

        for (var effect : recipe.effectDataList()) {
            result = result.addEffect(effect);
        }

        if(recipe.durationMultiplierAddition() != 0.0) {
            result = result.addDurationMultiplier(recipe.durationMultiplierAddition());
        }

        return result;
    }
}
