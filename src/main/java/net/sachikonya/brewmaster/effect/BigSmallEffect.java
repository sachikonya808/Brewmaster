package net.sachikonya.brewmaster.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sachikonya.brewmaster.Brewmaster;

import java.util.Objects;


public class BigSmallEffect extends MobEffect {
    private final String effectName;

    protected BigSmallEffect(MobEffectCategory category, int color, String name, double scaleAmount) {
        super(category, color);
        this.effectName = name;

        double speedModifier;
        double jumpModifier;
        double interactionRangeModifier = scaleAmount / 2;
        if(Objects.equals(name, "big")) {
            speedModifier = scaleAmount / 4;
            jumpModifier = scaleAmount / 4;
        } else {
            speedModifier = scaleAmount / 2;
            jumpModifier = scaleAmount;
        }

        AttributeModifier.Operation op = AttributeModifier.Operation.ADD_MULTIPLIED_BASE;

        addAttributeModifier(Attributes.SCALE, id(name, "scale"), scaleAmount, op);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, id(name, "speed"), speedModifier, op);
        addAttributeModifier(Attributes.JUMP_STRENGTH, id(name, "jump"), jumpModifier * 0.5, op);
        addAttributeModifier(Attributes.STEP_HEIGHT, id(name, "step"), scaleAmount, op);
        addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, id(name, "block_reach"), interactionRangeModifier, op);
        addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, id(name, "entity_reach"), interactionRangeModifier, op);
    }

    private static ResourceLocation id(String effect, String attribute) {
        return ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "effect." + effect + "." + attribute);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        ServerLevel serverLevel = (ServerLevel)livingEntity.level();
        float particleAmplifier = amplifier + 1;

        ParticleHelpers.spawnBigSmallParticles(serverLevel,
                livingEntity.getX(), (livingEntity.getY()+ (double)livingEntity.getBbHeight() / 2f), livingEntity.getZ(),
                particleAmplifier, effectName);

        serverLevel.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                SoundEvents.SHULKER_BULLET_HIT, SoundSource.HOSTILE, 1.0f, 1.0f);

        super.onEffectAdded(livingEntity, amplifier);
    }
}