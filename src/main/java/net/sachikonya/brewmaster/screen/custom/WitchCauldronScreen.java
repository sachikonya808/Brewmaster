package net.sachikonya.brewmaster.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import net.sachikonya.brewmaster.screen.client.CauldronFluidRender;
import net.sachikonya.brewmaster.screen.client.CauldronTooltipRender;

import java.util.ArrayList;
import java.util.List;

public class WitchCauldronScreen extends AbstractContainerScreen<WitchCauldronMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "textures/gui/witch_cauldron/witch_cauldron_gui.png");
    private static final ResourceLocation BUBBLE_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/brewing_stand/bubbles.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/brewing_stand/brew_progress.png");
    private static final ResourceLocation BOTTLING_ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "textures/gui/bottling_progress.png");
    private static final ResourceLocation FUEL_LENGTH =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/brewing_stand/fuel_length.png");
    private static final ResourceLocation UPGRADE_SLOTS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "upgrade_slots");

    private static final ResourceLocation RECIPE_BOOK_NORMAL = ResourceLocation.withDefaultNamespace("recipe_book/button");
    private static final ResourceLocation RECIPE_BOOK_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("recipe_book/button_highlighted");
    private static final WidgetSprites RECIPE_BOOk = new WidgetSprites(RECIPE_BOOK_HIGHLIGHTED, RECIPE_BOOK_NORMAL, RECIPE_BOOK_HIGHLIGHTED, RECIPE_BOOK_NORMAL);

    public WitchCauldronScreen(WitchCauldronMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        //Custom Menu Size
        this.imageWidth = 176;
        this.imageHeight = 188;

        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if(menu.getCurrentPage() == 1) {
            guiGraphics.blitSprite(UPGRADE_SLOTS_TEXTURE, x + 176, y + 14, 24, 64);
        }

        renderProgressArrow(guiGraphics, x, y);
        renderBottlingProgressArrow(guiGraphics, x, y);
        renderWaterTiles(guiGraphics, x, y);
        renderRemainingFuel(guiGraphics, x, y);
    }

    private StateSwitchingButton upgradesButton;
    @Override
    protected void init() {
        super.init();
        upgradesButton = new StateSwitchingButton(leftPos + 5, topPos + 72, 20, 18, menu.getCurrentPage() == 1) {
            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        };

        upgradesButton.initTextureValues(RECIPE_BOOk);
        addRenderableWidget(upgradesButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        upgradesButton.setStateTriggered(menu.getCurrentPage() == 1);
    }

    private void renderWaterTiles(GuiGraphics guiGraphics, int x, int y){
        int maxHeight = 34;
        int height = maxHeight * menu.getBrewAmount() / WitchCauldronBlockEntity.CAPACITY;
        int color = menu.getFluidType() == 2 ? menu.getPotionMixture().getPotionColor() : CauldronFluidRender.WATER_COLOR;

        CauldronFluidRender.render(guiGraphics, x + 63, y + 48 + (maxHeight - height), height, 52, color);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(ARROW_TEXTURE,x + 99, y + 18, 0, 0, 9, menu.getScaledArrowProgress(), 9, 28);
            guiGraphics.blit(BUBBLE_TEXTURE,x + 65,y + 17 + 29 - menu.getScaledBubbles(), 0, 29 - menu.getScaledBubbles(), 12, menu.getScaledBubbles(), 12, 29);
        }
    }

    private void renderBottlingProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isBottling()) {
            guiGraphics.blit(BOTTLING_ARROW_TEXTURE, x + 120, y + 73, 0, 0, menu.getScaledBottlingArrowProgress(), 9, 28, 9);
        }
    }

    private void renderRemainingFuel(GuiGraphics guiGraphics, int x, int y) {
        if(menu.getRemainingFuel() > 0) {
            guiGraphics.blit(FUEL_LENGTH, x + 80, y + 85, 0, 0, menu.getScaledRemainingFuel(), 4, 18, 4);
        }
    }

    private void renderFluidInsideTankTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        if (isHovering(63, 48, 52, 34, pMouseX, pMouseY)) {
            CauldronTooltipRender.render(guiGraphics, pMouseX, pMouseY, menu.getFluidType(), menu.getBrewAmount(), menu.getPotionMixture(), font);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(font, menu.getIngredientsUsed() + " / " + menu.getMaxIngredients(), 124, this.imageHeight - 164, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        renderFluidInsideTankTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
