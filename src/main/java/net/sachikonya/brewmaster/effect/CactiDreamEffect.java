package net.sachikonya.brewmaster.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class CactiDreamEffect extends MobEffect {
    private static final int ROLL_TABLE = 14;

    protected CactiDreamEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide()) {
            RandomSource randomSource = livingEntity.getRandom();
            int tripRoll = randomSource.nextInt(99);

            if(amplifier>=tripRoll) {
                rollEffect(randomSource, livingEntity);
            }
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (duration%20 == 0);
    }

    public static void rollEffect(RandomSource randomSource, LivingEntity livingEntity) {
        if(!livingEntity.level().isClientSide()) {
            switch (randomSource.nextInt(ROLL_TABLE)) {
                case 0 -> livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION,20+randomSource.nextInt(20), 0));
                case 1 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.MASTER, 1.0f, 1.0f);
                case 2 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.MASTER, 1.0f, 1.0f);
                case 3 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.PHANTOM_AMBIENT, SoundSource.MASTER, 1.0f, 1.0f);
                case 4 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.ENDERMAN_SCREAM, SoundSource.MASTER, 1.0f, 1.0f);
                case 5 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.ENDERMAN_STARE, SoundSource.MASTER, 1.0f, 1.0f);
                case 6 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.PILLAGER_AMBIENT, SoundSource.MASTER, 1.0f, 1.0f);
                case 7 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.STONE_STEP, SoundSource.MASTER, 1.0f, 1.0f);
                case 8 -> ParticleHelpers.spawnBigSmallParticles((ServerLevel) livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, "small");
                case 9 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.MASTER, 1.0f, 1.0f);
                case 10 -> livingEntity.hurt(livingEntity.level().damageSources().generic(), 2.0f);
                case 11 -> livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,5, 4));
                case 12 -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0f, 1.0f);
                case 13 -> livingEntity.addEffect(new MobEffectInstance(MobEffects.LEVITATION,20+randomSource.nextInt(20), 0));
                default -> livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.MASTER, 1.0f, 1.0f);
            }
        }
    }
}
