package net.sachikonya.brewmaster.recipe;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

public class MixedPotionContainerRecipe implements IBrewingRecipe {

    private final ItemStack ingredient;
    private final ItemStack output;

    public MixedPotionContainerRecipe(ItemStack ingredient, ItemStack output) {
        this.ingredient = ingredient;
        this.output = output;
    }

    private boolean isOurMixedPotion(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);

        if(contents == null) {
            return false;
        }

        return contents.potion().filter(Potions.AWKWARD::equals).isPresent() && !contents.customEffects().isEmpty();
    }

    @Override
    public boolean isInput(ItemStack itemStack) {
        boolean correctBottle = itemStack.is(Items.POTION) && isOurMixedPotion(itemStack);
        boolean correctSplash = itemStack.is(Items.SPLASH_POTION) && isOurMixedPotion(itemStack);

        return correctBottle || correctSplash;
    }

    @Override
    public boolean isIngredient(ItemStack itemStack) {
        return ItemStack.isSameItem(itemStack, ingredient);
    }

    @Override
    public ItemStack getOutput(ItemStack itemStack, ItemStack itemStack1) {
        if(!isInput(itemStack) || !isIngredient(itemStack1)) {
            return ItemStack.EMPTY;
        }

        ItemStack result;

        if(itemStack.is(Items.POTION) && this.ingredient.is(Items.GUNPOWDER)) {
            result = new ItemStack(Items.SPLASH_POTION);
        } else if (itemStack.is(Items.SPLASH_POTION) && this.ingredient.is(Items.DRAGON_BREATH)) {
            result = new ItemStack((Items.LINGERING_POTION));
        } else {
            return ItemStack.EMPTY;
        }

        result.applyComponents(itemStack.getComponents());
        return result;
    }
}
