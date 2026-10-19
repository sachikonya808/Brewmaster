package net.sachikonya.brewmaster.datagen;

import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.potion.PotionEffectData;
import net.sachikonya.brewmaster.recipe.BrewingRecipe;

import java.util.ArrayList;
import java.util.List;

public class BrewingRecipeBuilder {
    private final Ingredient ingredient;
    private final List<PotionEffectData> effects = new ArrayList<>();
    private float durationAddition = 0f;

    private BrewingRecipeBuilder(Ingredient ingredient) { this.ingredient = ingredient; }

    public static BrewingRecipeBuilder brewingRecipeBuilder(ItemLike item) {
        return new BrewingRecipeBuilder(Ingredient.of(item));
    }

    public BrewingRecipeBuilder effect(Holder<MobEffect> effect, int amplifier, int duration) {
        effects.add(new PotionEffectData(effect, amplifier, duration));
        return this;
    }

    public BrewingRecipeBuilder durationBonus(float amount) {
        this.durationAddition = amount;
        return this;
    }

    public void save(RecipeOutput out, String name) {
        out.accept(ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "brewing/" + name),
                new BrewingRecipe(ingredient, List.copyOf(effects), durationAddition), null);
    }
}
