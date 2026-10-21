package net.sachikonya.brewmaster.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class PowderTrailEffect extends MobEffect {
    protected PowderTrailEffect() { super(MobEffectCategory.HARMFUL, 0x60C953); }
    private int currentTick = 0;

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Level level = livingEntity.level();
        if(!level.isClientSide() && !livingEntity.isSpectator()) {
            ServerLevel serverLevel = (ServerLevel)level;
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    livingEntity.getX(), (livingEntity.getY() + (double)livingEntity.getBbHeight() / 2f), livingEntity.getZ(),
                    5, 0.4,0.5,0.4,0.2);

            if(currentTick == 1) {
                float power = 4.0f + (float) (amplifier + 1);
                serverLevel.explode(null, Explosion.getDefaultDamageSource(serverLevel, livingEntity), null,
                        livingEntity.getX(), (livingEntity.getY() + (double) livingEntity.getBbHeight() / 2f), livingEntity.getZ(),
                        power, false, Level.ExplosionInteraction.NONE);

                ParticleHelpers.spawnPoofExplosion(serverLevel, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), power*2);
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        ServerLevel serverLevel = (ServerLevel)livingEntity.level();
        serverLevel.playSound(null, livingEntity.getX(),livingEntity.getY(),livingEntity.getZ(),
                SoundEvents.TNT_PRIMED, SoundSource.HOSTILE, 1.0F, 1.0F);

        super.onEffectAdded(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        currentTick = duration;
        return true;
    }
}