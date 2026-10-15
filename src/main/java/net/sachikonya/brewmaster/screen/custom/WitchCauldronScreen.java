package net.sachikonya.brewmaster.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import net.sachikonya.brewmaster.screen.client.CauldronFluidRender;

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

        renderProgressArrow(guiGraphics, x, y);
        renderBottlingProgressArrow(guiGraphics, x, y);
        renderWaterTiles(guiGraphics, x, y);
        renderRemainingFuel(guiGraphics, x, y);
    }

    private void renderWaterTiles(GuiGraphics guiGraphics, int x, int y){
        int maxHeight = 34;
        int height = maxHeight * menu.getBrewAmount() / WitchCauldronBlockEntity.CAPACITY;
        int color = menu.getFluidType() == 2 ? menu.getPotionMixture().getPotionColor() : CauldronFluidRender.WATER_COLOR;

        CauldronFluidRender.render(guiGraphics, x + 63, y + 48 + (maxHeight - height), height, color);
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
            List<Component> lines = new ArrayList<>();

            switch (menu.getFluidType()) {
                case 0 -> lines.add(Component.translatable("tooltip.brewmaster.empty_fluid"));
                case 1 -> lines.add(Component.translatable("block.minecraft.water"));
                case 2 -> {
                    if (menu.getPotionMixture().isPotionEmpty()) {
                        lines.add(Component.translatable("item.minecraft.potion.effect.awkward"));
                        lines.add(Component.translatable("effect.none").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        lines.add(Component.translatable("item.brewmaster.mixed_potion").withStyle(ChatFormatting.ITALIC));
                        lines.addAll(menu.getPotionMixture().getTooltipLines());
                    }
                }
                default -> lines.add(Component.literal("!!! Unknown Fluid !!!").withStyle(ChatFormatting.DARK_RED));
            }
            lines.add(Component.translatable("tooltip.brewmaster.amount", menu.getBrewAmount(), WitchCauldronBlockEntity.CAPACITY).withStyle(ChatFormatting.GRAY));

            guiGraphics.renderComponentTooltip(font, lines, pMouseX, pMouseY);
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
