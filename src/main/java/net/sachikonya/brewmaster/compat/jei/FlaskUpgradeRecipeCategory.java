package net.sachikonya.brewmaster.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.item.FlaskTier;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;
import net.sachikonya.brewmaster.recipe.FlaskUpgradeRecipe;
import org.jetbrains.annotations.Nullable;

public class FlaskUpgradeRecipeCategory implements IRecipeCategory<FlaskUpgradeRecipe> {
    public static final RecipeType<FlaskUpgradeRecipe> FLASK_UPGRADE_RECIPE_RECIPE_TYPE =
            RecipeType.create(Brewmaster.MODID, "flask_upgrade", FlaskUpgradeRecipe.class);

    private final IDrawable icon;
    private final IDrawable arrow;

    public FlaskUpgradeRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.MAGIC_RUNE_A.get()));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<FlaskUpgradeRecipe> getRecipeType() { return FLASK_UPGRADE_RECIPE_RECIPE_TYPE; }
    @Override
    public Component getTitle() { return Component.translatable("jei.brewmaster.flask_upgrade_type"); }
    @Override
    public @Nullable IDrawable getIcon() { return icon; }
    @Override
    public int getWidth() { return 108; }
    @Override
    public int getHeight() { return 28; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FlaskUpgradeRecipe recipe, IFocusGroup focuses) {
        ItemStack itemStack = WondrousFlaskItem.withTier(
                new ItemStack(ModItems.WONDROUS_FLASK.get()), FlaskTier.byIndex(recipe.getTargetTier() - 1)
        );

        ItemStack output = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 6).setStandardSlotBackground().addIngredients(recipe.getTemplate());
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 6).setStandardSlotBackground().addItemStack(itemStack);
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).setStandardSlotBackground().addIngredients(recipe.getAddition());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 6).setStandardSlotBackground().addItemStack(output);
    }


    @Override
    public void draw(FlaskUpgradeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 61, 6);
    }
}
