package net.sachikonya.brewmaster.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BrittleEffect extends MobEffect {
    protected BrittleEffect() { super(MobEffectCategory.HARMFUL, 0xA52A2A); }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        super.onEffectStarted(livingEntity, amplifier);

        MobEffectInstance effectInstance = livingEntity.getEffect(MobEffects.DAMAGE_RESISTANCE);
        if(effectInstance == null) { return; }

        livingEntity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
    }
}
