package net.sachikonya.brewmaster.screen.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import net.sachikonya.brewmaster.potion.PotionMixture;

import java.util.ArrayList;
import java.util.List;

public class CauldronTooltipRender {
    public static void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int fluidType, int brewAmount, PotionMixture potionMixture, Font font) {
        List<Component> lines = new ArrayList<>();

        switch (fluidType) {
            case 0 -> lines.add(Component.translatable("tooltip.brewmaster.empty_fluid"));
            case 1 -> lines.add(Component.translatable("block.minecraft.water"));
            case 2 -> {
                if (potionMixture.isPotionEmpty()) {
                    lines.add(Component.translatable("item.minecraft.potion.effect.awkward"));
                    lines.add(Component.translatable("effect.none").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_GRAY));
                } else {
                    lines.add(Component.translatable("item.brewmaster.mixed_potion").withStyle(ChatFormatting.ITALIC));
                    lines.addAll(potionMixture.getTooltipLines());
                }
            }
            default -> lines.add(Component.literal("!!! Unknown Fluid !!!").withStyle(ChatFormatting.DARK_RED));
        }
        lines.add(Component.translatable("tooltip.brewmaster.amount", brewAmount, WitchCauldronBlockEntity.CAPACITY).withStyle(ChatFormatting.GRAY));

        guiGraphics.renderComponentTooltip(font, lines, pMouseX, pMouseY);
    }
}
