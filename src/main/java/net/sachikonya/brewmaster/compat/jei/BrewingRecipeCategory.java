package net.sachikonya.brewmaster.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.recipe.BrewingRecipe;
import net.sachikonya.brewmaster.recipe.ModRecipes;
import net.sachikonya.brewmaster.screen.client.CauldronFluidRender;

public class BrewingRecipeCategory implements IRecipeCategory<RecipeHolder<BrewingRecipe>> {
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "textures/gui/witch_cauldron/witch_cauldron_jei_gui.png");
    private static final ResourceLocation BUBBLE_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/brewing_stand/bubbles.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/brewing_stand/brew_progress.png");

    public static final RecipeType<RecipeHolder<BrewingRecipe>> BREWING_RECIPE_RECIPE_TYPE =
            RecipeType.createFromVanilla(ModRecipes.BREWING_TYPE.get());

    private final int WIDTH = 176;
    private final int HEIGHT = 105;
    private final int TANK_X = 63, TANK_Y = 48, TANK_H = 34, TANK_W = 52;

    private final IDrawable background;
    private final IDrawable icon;
    private IDrawableAnimated arrow;

    public BrewingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.WITCH_CAULDRON.get()));
        this.arrow = helper.drawableBuilder(ARROW_TEXTURE, 0, 0, 9, 28)
                .setTextureSize(9, 28)
                .buildAnimated(72, IDrawableAnimated.StartDirection.TOP,false);
    }

    private static final int[] BUBBLE_HEIGHTS = {0, 6, 11, 16, 20, 24, 29};

    private void drawBubbles(GuiGraphics g, int x, int y) {
        long tick = Util.getMillis() / 50L;
        int h = BUBBLE_HEIGHTS[(int) ((tick / 2) % 7)];
        if (h > 0) {
            g.blit(BUBBLE_TEXTURE, x, y + 29 - h, 0, 29 - h, 12, h, 12, 29);
        }
    }

    @Override
    public RecipeType<RecipeHolder<BrewingRecipe>> getRecipeType() {
        return BREWING_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.brewmaster.brewing_recipe_type");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<BrewingRecipe> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        BrewingRecipe brewingRecipe = recipe.value();
        double extra = brewingRecipe.durationMultiplierAddition();

        if(mouseX >= TANK_X && mouseX < TANK_X+TANK_W && mouseY >= TANK_Y && mouseY < TANK_Y+TANK_H) {
            tooltip.add(Component.translatable("item.brewmaster.mixed_potion").withStyle(ChatFormatting.ITALIC));
            if(extra != 0){
                Component text = Component.translatable("jei.brewmaster.duration_bonus", (extra > 0 ? "+" : "-") + Math.round(extra * 100) + "%");
                tooltip.add(text);
            } else {
                tooltip.addAll(new PotionMixture(brewingRecipe.effectDataList(), 1.0, 1).getTooltipLines());
            }
            Component catalyst = Component.translatable("jei.brewmaster.awkward_potion_required",Component.translatable("item.minecraft.potion.effect.awkward")).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC);
            tooltip.add(catalyst);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BrewingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 81, 19).addIngredients(recipe.value().inputItem());
    }

    @Override
    public void draw(RecipeHolder<BrewingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        arrow.draw(guiGraphics,99,18);
        drawBubbles(guiGraphics, 65, 17);

        BrewingRecipe brewingRecipe = recipe.value();
        int color = new PotionMixture(brewingRecipe.effectDataList(),1.0, 1).getPotionColor();
        CauldronFluidRender.render(guiGraphics, TANK_X, TANK_Y, TANK_H, color);
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }
    @Override
    public int getWidth() { return WIDTH; }
}
