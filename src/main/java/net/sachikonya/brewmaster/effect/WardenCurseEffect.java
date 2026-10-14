package net.sachikonya.brewmaster.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WardenCurseEffect extends MobEffect {
    protected WardenCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        ServerLevel serverLevel = (ServerLevel)livingEntity.level();
        serverLevel.playSound(null, livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE,1.0f, 1.0f);
        super.onEffectAdded(livingEntity, amplifier);
    }
}
