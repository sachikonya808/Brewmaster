package net.sachikonya.brewmaster.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.recipe.FlaskUpgradeRecipe;
import net.sachikonya.brewmaster.recipe.ModRecipes;
import net.sachikonya.brewmaster.screen.custom.WitchCauldronScreen;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIBrewmasterPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BrewingRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));

        registration.addRecipeCategories(new FlaskUpgradeRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level.getRecipeManager());

        registration.addIngredientInfo(new ItemStack(Items.NETHER_WART), VanillaTypes.ITEM_STACK, Component.translatable("item.minecraft.potion.effect.awkward"));
        registration.addRecipes(BrewingRecipeCategory.BREWING_RECIPE_RECIPE_TYPE, recipeManager.getAllRecipesFor(ModRecipes.BREWING_TYPE.get()));

        List<FlaskUpgradeRecipe> flaskUpgradeRecipes = recipeManager
                .getAllRecipesFor(RecipeType.SMITHING).stream()
                .map(RecipeHolder::value)
                .filter(FlaskUpgradeRecipe.class::isInstance)
                .map(FlaskUpgradeRecipe.class::cast)
                .toList();
        registration.addRecipes(FlaskUpgradeRecipeCategory.FLASK_UPGRADE_RECIPE_RECIPE_TYPE, flaskUpgradeRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WITCH_CAULDRON.get()), BrewingRecipeCategory.BREWING_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.SMITHING_TABLE), FlaskUpgradeRecipeCategory.FLASK_UPGRADE_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(WitchCauldronScreen.class, 99, 18, 9, 28,
                BrewingRecipeCategory.BREWING_RECIPE_RECIPE_TYPE);
    }
}