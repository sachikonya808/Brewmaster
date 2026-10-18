package net.sachikonya.brewmaster.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.item.custom.SeaBlessedGourdItem;
import net.sachikonya.brewmaster.screen.client.CauldronFluidRender;

public class SeaBlessedGourdScreen extends AbstractContainerScreen<SeaBlessedGourdMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "textures/gui/gourd_gui.png");
    private static final ResourceLocation DISTILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "textures/gui/bottling_progress.png");

    public SeaBlessedGourdScreen(SeaBlessedGourdMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 204;

        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
        renderWaterTiles(guiGraphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isDistilling()) {
            guiGraphics.blit(DISTILL_TEXTURE, x + 74, y + 52, 0, 0, menu.getScaledDistillProgress(), 9, 28, 9);
        }
    }

    private void renderWaterTiles(GuiGraphics guiGraphics, int x, int y) {
        int maxHeight = 32;
        int height = maxHeight * menu.getBrewAmount() / menu.getCapacity();
        int color = menu.getFluidType() == 2 ? menu.getPotionColor() : CauldronFluidRender.WATER_COLOR;

        CauldronFluidRender.render(guiGraphics, x + 104, y + 30 + (maxHeight - height), height, 16, color);
    }
}
