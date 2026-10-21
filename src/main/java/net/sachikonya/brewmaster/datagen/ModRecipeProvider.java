package net.sachikonya.brewmaster.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.effect.ModEffects;
import net.sachikonya.brewmaster.item.FlaskTier;
import net.sachikonya.brewmaster.item.FlaskUpgradeData;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.registry.ModDataComponents;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModBlocks.WITCH_CAULDRON.get())
                .pattern("BCB")
                .pattern("BAB")
                .pattern("BBB")
                .define('C', Items.STICK.asItem())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.SEA_BLESSED_GOURD.get())
                .pattern("BA ")
                .pattern("CD ")
                .define('A', Items.OMINOUS_BOTTLE.asItem())
                .define('B', DataComponentIngredient.of(
                        false,
                        ModDataComponents.FLASK_UPGRADE.get(),
                        new FlaskUpgradeData(FlaskTier.DIAMOND.ordinal()),
                        ModItems.WONDROUS_FLASK.get()))
                .define('C', Items.HEART_OF_THE_SEA.asItem())
                .define('D', Items.PUMPKIN.asItem())
                .unlockedBy("has_wondrous_flask", has(ModItems.WONDROUS_FLASK.get()))
                .save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.IRON_AMETHYST.get())
                .pattern("ABA")
                .pattern("ACA")
                .pattern("AAA")
                .define('A', Items.IRON_INGOT.asItem())
                .define('B', ModItems.MAGIC_RUNE_A.get())
                .define('C', Items.AMETHYST_SHARD.asItem())
                .unlockedBy("has_wondrous_flask", has(ModItems.WONDROUS_FLASK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.GOLD_AMETHYST.get())
                .pattern("ABA")
                .pattern("ACA")
                .pattern("AAA")
                .define('A', Items.GOLD_INGOT.asItem())
                .define('B', ModItems.MAGIC_RUNE_B.get())
                .define('C', Items.AMETHYST_SHARD.asItem())
                .unlockedBy("has_wondrous_flask", has(ModItems.WONDROUS_FLASK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.DIAMOND_AMETHYST.get())
                .pattern("ABA")
                .pattern("ACA")
                .pattern("AAA")
                .define('A', Items.DIAMOND.asItem())
                .define('B', ModItems.MAGIC_RUNE_C.get())
                .define('C', Items.AMETHYST_SHARD.asItem())
                .unlockedBy("has_wondrous_flask", has(ModItems.WONDROUS_FLASK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.NETHERITE_AMETHYST.get())
                .pattern("A  ")
                .pattern("B  ")
                .define('A', Items.NETHERITE_INGOT.asItem())
                .define('B', Items.AMETHYST_SHARD.asItem())
                .unlockedBy("has_wondrous_flask", has(ModItems.WONDROUS_FLASK.get()))
                .save(recipeOutput);

        //Datagen for Brewing Recipes
        BrewingRecipeBuilder.brewingRecipeBuilder(Items.SUSPICIOUS_STEW)
                .effect(MobEffects.CONFUSION, 0, 1800)
                .save(recipeOutput, "suspicious_stew");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.ROTTEN_FLESH)
                .effect(MobEffects.HUNGER, 0, 1800)
                .save(recipeOutput, "rotten_flesh");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.WITHER_ROSE)
                .effect(MobEffects.WITHER, 0, 1800)
                .save(recipeOutput, "wither_rose");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.CAKE)
                .effect(ModEffects.HIGH_CHOLESTEROL, 0, 1800)
                .save(recipeOutput, "cake");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.CHORUS_FRUIT)
                .effect(ModEffects.MEMORY_LOSS, 0, 1800)
                .save(recipeOutput, "chorus_fruit");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.HONEY_BOTTLE)
                .effect(MobEffects.SATURATION, 5, 1800)
                .save(recipeOutput, "honey_bottle");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GOLD_BLOCK)
                .effect(ModEffects.LUCKY_POTION, 0, 0)
                .save(recipeOutput, "gold_block");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.ANVIL)
                .effect(ModEffects.FALLING_ANVIL, 0, 100)
                .save(recipeOutput, "anvil");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.BLAZE_POWDER)
                .effect(MobEffects.DAMAGE_BOOST, 0, 1800)
                .save(recipeOutput, "blaze_powder");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.BREEZE_ROD)
                .effect(MobEffects.WIND_CHARGED, 0, 1800)
                .save(recipeOutput, "breeze_rod");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.COBWEB)
                .effect(MobEffects.WEAVING, 0, 1800)
                .save(recipeOutput, "cobweb");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.COCOA_BEANS)
                .effect(MobEffects.DIG_SPEED, 0, 1800)
                .save(recipeOutput, "cocoa_beans");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.CRIMSON_FUNGUS)
                .effect(MobEffects.HARM, 0, 0)
                .save(recipeOutput, "crimson_fungus");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.ECHO_SHARD)
                .effect(ModEffects.WARDEN_CURSE, 0, 1800)
                .save(recipeOutput, "echo_shard");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.EXPERIENCE_BOTTLE)
                .effect(ModEffects.BIG, 0, 1800)
                .save(recipeOutput, "experience_bottle");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.FERMENTED_SPIDER_EYE)
                .effect(MobEffects.WEAKNESS, 0, 1800)
                .save(recipeOutput, "fermented_spider_eye");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GHAST_TEAR)
                .effect(MobEffects.REGENERATION, 0, 1800)
                .save(recipeOutput, "ghast_tear");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GLASS)
                .effect(MobEffects.INVISIBILITY, 0, 1800)
                .save(recipeOutput, "glass");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GLISTERING_MELON_SLICE)
                .effect(MobEffects.HEAL, 0, 0)
                .save(recipeOutput, "glistering_melon_slice");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GLOWSTONE_DUST)
                .effect(MobEffects.GLOWING, 0, 1800)
                .save(recipeOutput, "glowstone_dust");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GOLDEN_APPLE)
                .effect(MobEffects.ABSORPTION, 0, 1800)
                .save(recipeOutput, "golden_apple");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GOLDEN_CARROT)
                .effect(MobEffects.NIGHT_VISION, 0, 1800)
                .save(recipeOutput, "golden_carrot");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GUNPOWDER)
                .effect(ModEffects.POWDER_TRAIL, 0, 100)
                .save(recipeOutput, "gunpowder");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.IRON_BLOCK)
                .effect(MobEffects.DAMAGE_RESISTANCE, 0, 1800)
                .ingredientWeight(2)
                .save(recipeOutput, "iron_block");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.LIGHTNING_ROD)
                .effect(ModEffects.LIGHTNING_ROD, 0, 1800)
                .save(recipeOutput, "lightning_rod");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.MAGMA_CREAM)
                .effect(MobEffects.FIRE_RESISTANCE, 0, 1800)
                .save(recipeOutput, "magma_cream");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.PHANTOM_MEMBRANE)
                .effect(MobEffects.SLOW_FALLING, 0, 1800)
                .save(recipeOutput, "phantom_membrane");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.PUFFERFISH)
                .effect(MobEffects.WATER_BREATHING, 0, 1800)
                .save(recipeOutput, "pufferfish");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.RABBIT_FOOT)
                .effect(MobEffects.JUMP, 0, 1800)
                .save(recipeOutput, "rabbit_foot");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.RABBIT_HIDE)
                .effect(ModEffects.SMALL, 0, 1800)
                .save(recipeOutput, "rabbit_hide");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.REDSTONE)
                .durationBonus(5.5f)
                .save(recipeOutput, "redstone");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.SLIME_BLOCK)
                .effect(MobEffects.OOZING, 0, 1800)
                .save(recipeOutput, "slime_block");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.SNOW_BLOCK)
                .effect(MobEffects.MOVEMENT_SLOWDOWN, 0, 1800)
                .save(recipeOutput, "snow_block");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.SPIDER_EYE)
                .effect(MobEffects.POISON, 0, 1800)
                .save(recipeOutput, "spider_eye");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.STONE)
                .effect(MobEffects.INFESTED, 0, 1800)
                .save(recipeOutput, "stone");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.SUGAR)
                .effect(MobEffects.MOVEMENT_SPEED, 0, 1800)
                .save(recipeOutput, "sugar");
    }
}
