package net.sachikonya.brewmaster.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class WardenCurseEffect extends MobEffect {
    protected WardenCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    private int startDuration = 0;

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide() && livingEntity.getServer() != null) {
            ServerLevel serverLevel = (ServerLevel) livingEntity.level();
            serverLevel.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.0f, 1.0f);
        }
        super.onEffectAdded(livingEntity, amplifier);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide() && livingEntity.getServer() != null) {
            MobEffectInstance effectInstance = livingEntity.getEffect(ModEffects.WARDEN_CURSE);
            if(effectInstance != null) {
                int ticksLeft = effectInstance.getDuration();
                startDuration = ticksLeft;
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, ticksLeft, amplifier), livingEntity);
            }
        }
        super.onEffectStarted(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (duration == startDuration - 20);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide() && livingEntity.getServer() != null) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, startDuration-20, amplifier), livingEntity);
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }
}
