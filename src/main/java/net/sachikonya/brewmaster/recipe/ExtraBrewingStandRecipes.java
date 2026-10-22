package net.sachikonya.brewmaster.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public class ExtraBrewingStandRecipes {

    public static void register(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addRecipe(new MixedPotionRecipeHelper(new ItemStack(Items.GUNPOWDER), new ItemStack(Items.SPLASH_POTION)));
        builder.addRecipe(new MixedPotionRecipeHelper(new ItemStack(Items.DRAGON_BREATH), new ItemStack(Items.LINGERING_POTION)));
    }
}
