package net.sachikonya.brewmaster.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum WitchCauldronComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if(!(blockAccessor.getBlockEntity() instanceof  WitchCauldronBlockEntity blockEntity)) {
            return;
        }

        CompoundTag data = blockAccessor.getServerData();
        if(data.contains("Fuel")) {
            IElementHelper elements = IElementHelper.get();
            IElement icon = elements.item(new ItemStack(Items.BLAZE_POWDER), 0.5f);
            iTooltip.add(icon);
            iTooltip.append(Component.translatable("jade.brewmaster.fuel", blockEntity.getFuel()).withStyle(ChatFormatting.WHITE));
        }

        switch(blockEntity.getFluidTypeID()) {
            case 0, 1 -> {}
            case 2 -> {
                for(Component line : blockEntity.getPotionMixture().getTooltipLines()) {
                    iTooltip.add(line);
                }
            }

        }

        if(data.contains("Max")) {
            iTooltip.add(Component.translatable("jade.brewmaster.ingredients", data.getInt("Used"), data.getInt("Max")).withStyle(ChatFormatting.ITALIC)
                    .withStyle((data.getInt("Max") == data.getInt("Used") ? ChatFormatting.RED : ChatFormatting.WHITE)));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadeBrewmasterPlugin.WITCH_CAULDRON;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if(blockAccessor.getBlockEntity() instanceof WitchCauldronBlockEntity blockEntity) {
            compoundTag.putInt("Fuel", blockEntity.getFuel());
            compoundTag.putInt("Used", blockEntity.getIngredientsUsed());
            compoundTag.putInt("Max", blockEntity.getFinalMaxIngredients());
        }
    }
}
