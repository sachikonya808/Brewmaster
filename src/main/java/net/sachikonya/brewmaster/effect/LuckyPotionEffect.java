package net.sachikonya.brewmaster.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class LuckyPotionEffect extends InstantenousMobEffect {
    public LuckyPotionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide() && !livingEntity.isSpectator()) {
            RandomSource randomSource = livingEntity.getRandom();

            Optional<Holder.Reference<MobEffect>> lucky = BuiltInRegistries.MOB_EFFECT.getRandom(randomSource);
            if(lucky.isEmpty()) { return false; }

            Holder<MobEffect> effectHolder = lucky.get();
            int duration = (90 + randomSource.nextInt(405)) * 20;

            if(effectHolder.value().isInstantenous()) {
                effectHolder.value().applyInstantenousEffect(livingEntity, livingEntity, livingEntity, amplifier, 1.0);
            } else {
                livingEntity.addEffect(new MobEffectInstance(effectHolder, duration, amplifier));
            }
        }

        return true;
    }
}
