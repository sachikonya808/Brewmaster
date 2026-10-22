package net.sachikonya.brewmaster.villager;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.sachikonya.brewmaster.recipe.BrewingRecipe;
import net.sachikonya.brewmaster.recipe.ModRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RandomBrewingRecipeListing implements VillagerTrades.ItemListing {
    private final int emeraldCost;
    private final int maxUses;
    private final int xp;

    public RandomBrewingRecipeListing(int emeraldCost, int maxUses, int xp) {
        this.emeraldCost = emeraldCost;
        this.maxUses = maxUses;
        this.xp = xp;
    }

    @Override
    public @Nullable MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
        List<RecipeHolder<BrewingRecipe>> recipeHolderList = entity.level().getRecipeManager().getAllRecipesFor(ModRecipes.BREWING_TYPE.get());
        if (recipeHolderList.isEmpty()) { return null; }

        BrewingRecipe brewingRecipe = recipeHolderList.get(randomSource.nextInt(recipeHolderList.size())).value();
        ItemStack[] options = brewingRecipe.inputItem().getItems();
        if(options.length == 0) { return null; }

        ItemStack result = options[randomSource.nextInt(options.length)].copy();
        return new MerchantOffer(new ItemCost(Items.EMERALD, emeraldCost), result, maxUses, xp, 0.05f);
    }
}
