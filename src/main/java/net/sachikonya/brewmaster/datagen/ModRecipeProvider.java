package net.sachikonya.brewmaster.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.sachikonya.brewmaster.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.WITCH_CAULDRON.get())
                .pattern("B B")
                .pattern("BAB")
                .pattern("BBB")
                .define('B', Items.IRON_INGOT.asItem())
                .define('A', Items.STICK.asItem())
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(recipeOutput);


    }
}
