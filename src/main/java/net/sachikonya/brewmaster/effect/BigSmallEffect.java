package net.sachikonya.brewmaster.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sachikonya.brewmaster.Brewmaster;

import java.util.Objects;


public class BigSmallEffect extends MobEffect {
    protected BigSmallEffect(MobEffectCategory category, int color, String name, double scaleAmount) {
        super(category, color);

        double speedModifier = 0.0f;
        double jumpModifier = 0.0f;
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
        addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, id(name, "block_reach"), scaleAmount, op);
        addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, id(name, "entity_reach"), scaleAmount, op);
    }

    private static ResourceLocation id(String effect, String attribute) {
        return ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "effect." + effect + "." + attribute);
    }
}