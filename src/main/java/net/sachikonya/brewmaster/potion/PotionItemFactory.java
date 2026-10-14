package net.sachikonya.brewmaster.potion;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.registries.DeferredRegister;

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

            int score = 0;
            for(PotionEffectData effectData : mixture.effectDataList()) {
                score += 1 + effectData.amplifier();
            }

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

            potion.set(DataComponents.RARITY, rarity);
        }

        potion.set(DataComponents.POTION_CONTENTS, contents);

        return potion;
    }
}