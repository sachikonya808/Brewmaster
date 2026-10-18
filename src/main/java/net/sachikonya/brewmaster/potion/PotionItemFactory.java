package net.sachikonya.brewmaster.potion;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.sachikonya.brewmaster.fluid.PotionFlaskFluidHandler;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionItemFactory {

    public static ItemStack createPotion(PotionMixture mixture) {
        List<MobEffectInstance> effects = new ArrayList<>();

        for (PotionEffectData effectData : mixture.effectDataList()) {
            int duration = mixture.getFinalDuration(effectData);

            effects.add(new MobEffectInstance(
                    effectData.effect(),
                    duration,
                    effectData.amplifier()));
        }

        PotionContents contents = new PotionContents(
                Optional.of(Potions.AWKWARD),
                Optional.empty(),
                effects
        );

        ItemStack potion = new ItemStack(Items.POTION);

        if(!mixture.effectDataList().isEmpty()) {
            potion.set(DataComponents.CUSTOM_NAME, Component.translatable("item.brewmaster.mixed_potion"));
            potion.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            Rarity rarity = getRarity(mixture);

            potion.set(DataComponents.RARITY, rarity);
        }

        potion.set(DataComponents.POTION_CONTENTS, contents);

        return potion;
    }

    public static Rarity getRarity(PotionMixture mixture) {
        int score = mixture.ingredientsAdded();

        Rarity rarity;
        if(score >= 8) {
            rarity = Rarity.EPIC;
        } else if(score >= 5) {
            rarity = Rarity.RARE;
        } else if(score >= 2) {
            rarity = Rarity.UNCOMMON;
        } else {
            rarity = Rarity.COMMON;
        }

        return rarity;
    }


    public static ItemStack fillFlask(ItemStack input, FluidStack portion) {
        ItemStack output = input.copyWithCount(1);

        portion.setAmount(PotionFlaskFluidHandler.DOSE);

        IFluidHandlerItem flaskHandler = output.getCapability(Capabilities.FluidHandler.ITEM);
        if(flaskHandler == null) { return null; }

        int accepted = flaskHandler.fill(portion, IFluidHandler.FluidAction.SIMULATE);
        if(accepted != PotionFlaskFluidHandler.DOSE) { return null; }

        flaskHandler.fill(portion, IFluidHandler.FluidAction.EXECUTE);
        return flaskHandler.getContainer();
    }
}