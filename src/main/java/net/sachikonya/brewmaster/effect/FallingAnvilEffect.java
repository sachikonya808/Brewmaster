package net.sachikonya.brewmaster.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.sachikonya.brewmaster.entity.custom.AnvilEntity;

// Anvil Entity + Behavior by Xylonity, from their Companions Mod: https://github.com/Xylonity/Companions
public class FallingAnvilEffect extends MobEffect {
    public FallingAnvilEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof ServerPlayer serverPlayer){
            if(!livingEntity.isSpectator()) {
                ServerLevel serverLevel = serverPlayer.serverLevel();
                final AnvilEntity anvil = AnvilEntity.create(serverLevel,
                        serverPlayer.getX(),
                        serverPlayer.getY() + 20,
                        serverPlayer.getZ(),
                        serverPlayer.getY(),
                        amplifier+1);

                anvil.disableDrop();
                serverLevel.addFreshEntity(anvil);
            }
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (duration == 40);
    }
}
