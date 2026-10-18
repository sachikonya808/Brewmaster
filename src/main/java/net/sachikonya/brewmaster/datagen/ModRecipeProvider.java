package net.sachikonya.brewmaster.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModBlocks.WITCH_CAULDRON.get())
                .pattern("B B")
                .pattern("BAB")
                .pattern("BBB")
                .define('B', Items.IRON_INGOT.asItem())
                .define('A', Items.AMETHYST_SHARD.asItem())
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.WONDROUS_FLASK.get())
                .pattern("ABA")
                .pattern(" C ")
                .define('A', Items.COPPER_INGOT.asItem())
                .define('B', Items.GLASS_BOTTLE.asItem())
                .define('C', ItemTags.SAPLINGS)
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);


    }
}
