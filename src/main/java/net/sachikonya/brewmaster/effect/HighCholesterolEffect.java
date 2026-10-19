package net.sachikonya.brewmaster.effect;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.sachikonya.brewmaster.registry.ModDamageTypes;

public class HighCholesterolEffect extends MobEffect {
    protected HighCholesterolEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide() && !livingEntity.isSpectator()) {
            RandomSource randomSource = livingEntity.getRandom();
            int heartAttackRoll = randomSource.nextInt((100000/(amplifier+1)-1));

            if(amplifier>=heartAttackRoll) {
                livingEntity.hurt(ModDamageTypes.of(livingEntity.level(), ModDamageTypes.HEART_ATTACK, livingEntity), Float.MAX_VALUE);
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide() && !livingEntity.isSpectator()) {
            if(!livingEntity.level().isClientSide() && livingEntity.getServer() != null) {
                MobEffectInstance effectInstance = livingEntity.getEffect(ModEffects.HIGH_CHOLESTEROL);

                if(effectInstance != null) {
                    int ticksLeft = effectInstance.getDuration();
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.SATURATION, ticksLeft, amplifier), livingEntity);
                }
            }
        }

        super.onEffectStarted(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (duration%20 == 0);
    }
}