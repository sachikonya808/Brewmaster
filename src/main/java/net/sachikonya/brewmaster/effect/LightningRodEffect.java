package net.sachikonya.brewmaster.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class LightningRodEffect  extends MobEffect {
    protected LightningRodEffect() {
        super(MobEffectCategory.HARMFUL, 0x6FFFFF);
    }

    private boolean isRaining = false;

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof LivingEntity entity) {
            if(!livingEntity.isSpectator()) {
                zapThisBitch(entity);
                isRaining = entity.level().isRainingAt(entity.blockPosition());
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    public void zapThisBitch(Entity target){
        if(!(target.level() instanceof ServerLevel level)) {
            return;
        }

        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);

        if(lightningBolt == null) {
            return;
        }

        lightningBolt.setPos(target.getX(), target.getY(), target.getZ());

        level.addFreshEntity(lightningBolt);

        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                target.getX(), target.getY(), target.getZ(),
                40, 0.5, 0.6, 0.5, 0.15);
        level.sendParticles(ParticleTypes.CRIT,
                target.getX(), target.getY(), target.getZ(),
                15, 0.4, 0.5, 0.4, 0.2);
    }



    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        if(duration%20 == 0){
            Random random = new Random();
            int randomInt = random.nextInt(100);
            int chance = 5 * (amplifier+1) * (isRaining ? 2 : 1);

            return randomInt <= chance;
        }

        return false;
    }
}
