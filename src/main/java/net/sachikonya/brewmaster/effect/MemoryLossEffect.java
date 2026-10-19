package net.sachikonya.brewmaster.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class MemoryLossEffect extends MobEffect {
    protected MemoryLossEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if(!livingEntity.level().isClientSide() && !livingEntity.isSpectator()) {
            RandomSource randomSource = livingEntity.getRandom();
            int teleportRoll = randomSource.nextInt(99);

            if(amplifier>=teleportRoll){
                Level level = livingEntity.level();
                for(int i = 0 ; i < 16 ; i++) {
                    double pX = livingEntity.getX() + (randomSource.nextDouble() - 0.5) * (16.0*(amplifier+1));
                    double pY = Mth.clamp(
                            livingEntity.getY() + (double)(randomSource.nextInt(16) - 8),
                            level.getMinBuildHeight(),
                            level.getMinBuildHeight() + ((ServerLevel) level).getLogicalHeight() -1
                    );
                    double pZ = livingEntity.getZ() + (randomSource.nextDouble() - 0.5) * (16.0*(amplifier+1));
                    if(livingEntity.isPassenger()) {
                        livingEntity.stopRiding();
                    }

                    Vec3 vec3 = livingEntity.position();

                    EntityTeleportEvent.ChorusFruit event = EventHooks.onChorusFruitTeleport(livingEntity, pX, pY, pZ);
                    if(event.isCanceled()) { return false; }
                    if(livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event. getTargetZ(), true)) {
                        level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(livingEntity));
                        livingEntity.resetFallDistance();
                        break;
                    }
                }
            }
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (duration%20 == 0);
    }
}
