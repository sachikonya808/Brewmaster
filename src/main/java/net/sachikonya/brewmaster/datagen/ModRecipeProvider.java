package net.sachikonya.brewmaster.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.effect.ModEffects;
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


        //Datagen for Brewing Recipes
        BrewingRecipeBuilder.brewingRecipeBuilder(Items.GOLD_BLOCK)
                .effect(ModEffects.LUCKY_POTION, 0, 0)
                .save(recipeOutput, "gold_block");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.ANVIL)
                .effect(ModEffects.FALLING_ANVIL_EFFECT, 0, 100)
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
                .effect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModEffects.POWDER_TRAIL_EFFECT.get()), 0, 100)
                .save(recipeOutput, "gunpowder");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.IRON_BLOCK)
                .effect(MobEffects.DAMAGE_RESISTANCE, 0, 1800)
                .save(recipeOutput, "iron_block");

        BrewingRecipeBuilder.brewingRecipeBuilder(Items.LIGHTNING_ROD)
                .effect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModEffects.LIGHTNING_ROD_EFFECT.get()), 0, 1800)
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
