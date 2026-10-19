package net.sachikonya.brewmaster.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.effect.MobEffect;
import net.sachikonya.brewmaster.Brewmaster;

import java.util.function.Supplier;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
        DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Brewmaster.MODID);

    public static final Holder<MobEffect> FALLING_ANVIL_EFFECT =
            MOB_EFFECTS.register("falling_anvil", () -> new FallingAnvilEffect(MobEffectCategory.HARMFUL, 0x515151)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "falling_anvil"),
                            -0.5f,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final Supplier<MobEffect> LIGHTNING_ROD_EFFECT =
            MOB_EFFECTS.register("lightning_rod", LightningRodEffect::new);

    public static final Supplier<MobEffect> POWDER_TRAIL_EFFECT =
            MOB_EFFECTS.register("powder_trail", PowderTrailEffect::new);

    public static final Holder<MobEffect> WARDEN_CURSE =
            MOB_EFFECTS.register("warden_curse", () -> new WardenCurseEffect(MobEffectCategory.NEUTRAL, 0x062E37)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "warden_curse"),
                            9f,
                            AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, BigSmallEffect> SMALL =
            MOB_EFFECTS.register("small", () -> new BigSmallEffect(MobEffectCategory.NEUTRAL, 0xD031C9, "small", -0.1));

    public static final DeferredHolder<MobEffect, BigSmallEffect> BIG =
            MOB_EFFECTS.register("big", () -> new BigSmallEffect(MobEffectCategory.NEUTRAL, 0x91D031, "big", 1f));

    public static final DeferredHolder<MobEffect, LuckyPotionEffect> LUCKY_POTION =
            MOB_EFFECTS.register("lucky_potion", () -> new LuckyPotionEffect(MobEffectCategory.NEUTRAL, 0xF6E769));

    public static final DeferredHolder<MobEffect, MemoryLossEffect> MEMORY_LOSS =
            MOB_EFFECTS.register("memory_loss", () -> new MemoryLossEffect(MobEffectCategory.HARMFUL, 0x8F648F));

    public static final DeferredHolder<MobEffect, HighCholesterolEffect> HIGH_CHOLESTEROL =
            MOB_EFFECTS.register("high_cholesterol", () -> new HighCholesterolEffect(MobEffectCategory.HARMFUL, 0xDBA213));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
