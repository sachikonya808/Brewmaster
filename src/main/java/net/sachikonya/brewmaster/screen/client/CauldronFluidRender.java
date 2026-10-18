package net.sachikonya.brewmaster.screen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;

public class CauldronFluidRender {
    public static final int WATER_COLOR = 0x3F76E4;

    public static void render(GuiGraphics guiGraphics, int x, int y, int height, int width, int rgb) {
        if(height <= 0) { return; }

        TextureAtlasSprite water = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ResourceLocation.withDefaultNamespace("block/water_still"));

        guiGraphics.setColor(FastColor.ARGB32.red(rgb) / 255f, FastColor.ARGB32.green(rgb) / 255f, FastColor.ARGB32.blue(rgb) / 255f, 1f);
        guiGraphics.blit(x, y, 0, width, height, water);
        guiGraphics.setColor(1f,1f,1f,1f);
    }
}
