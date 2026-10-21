package net.sachikonya.brewmaster.fluid;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;

import java.util.Objects;

public class PotionFlaskFluidHandler extends FluidHandlerItemStack {
    public static final int DOSE = 250;
    public static final int DRINK_TICKS = 32;

    public PotionFlaskFluidHandler(ItemStack container) {
        super(ModDataComponents.FLASK_FLUID, container, capacityOf(container));
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if(isFlaskLocked()) { return false; }

        if(fluid.isEmpty() || !fluid.is(ModFluids.POTION.get())) {
            return false;
        }

        FluidStack storedFluid = getFluid();
        if(storedFluid.isEmpty()) {
            return true;
        }

        return storedFluid.is((ModFluids.POTION.get())) && Objects.equals(storedFluid.get(ModDataComponents.POTION_MIXTURE), fluid.get(ModDataComponents.POTION_MIXTURE));
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return !isFlaskLocked() && canFillFluidType(stack);
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluid) {
        return !isFlaskLocked() && super.canDrainFluidType(fluid);
    }

    public static int capacityOf(ItemStack itemStack) {
        return itemStack.getItem() instanceof FluidCapacityProvider provider ? provider.getFluidCapacity(itemStack) : 0;
    }

    public boolean isFlaskLocked() {
        return container.getOrDefault(ModDataComponents.TANK_LOCKED.get(), false);
    }

    public static void applyPotionEffects(PotionContents contents, LivingEntity livingEntity) {
        if(!(livingEntity.level() instanceof ServerLevel)) {
            return;
        }

        for(MobEffectInstance effectInstance : contents.getAllEffects()) {
            MobEffect mobEffect = effectInstance.getEffect().value();

            if(mobEffect.isInstantenous()) {
                mobEffect.applyInstantenousEffect(livingEntity, livingEntity, livingEntity, effectInstance.getAmplifier(), 1.0);
            } else {
                livingEntity.addEffect(new MobEffectInstance(effectInstance));
            }
        }
    }

    public static boolean shouldGlow(ItemStack stack) {
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) { return false; }

        PotionMixture mixture = getPotionMixture(fluidHandler);
        boolean hasEffects = mixture != null && getPotionMixture(fluidHandler) != PotionMixture.EMPTY;

        if(mixture != null && !mixture.effectDataList().isEmpty()) {
            Rarity rarity = PotionItemFactory.getRarity(mixture);
            updateRarity(stack, rarity);
        }

        return hasEffects;
    }

    public static void updateRarity(ItemStack itemStack, Rarity rarity) {
        itemStack.set(DataComponents.RARITY, rarity);
    }

    public static PotionMixture getPotionMixture(IFluidHandler fluidHandler) {
        if(!fluidHandler.getFluidInTank(0).is(ModFluids.POTION.get())) {
            return PotionMixture.EMPTY;
        }

        return fluidHandler.getFluidInTank(0).getOrDefault(ModDataComponents.POTION_MIXTURE.get(), PotionMixture.EMPTY);
    }

    public static int getMaxCharges(int maxCapacity) {
        return maxCapacity / PotionFlaskFluidHandler.DOSE;
    }

    public static int getRemainingCharges(ItemStack itemStack) {
        IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandlerItem == null) { return 0; }

        return fluidHandlerItem.getFluidInTank(0).getAmount() / PotionFlaskFluidHandler.DOSE;
    }

    public static int getFluidTypeID(IFluidHandler fluidHandler) {
        if(fluidHandler == null) { return 0; }

        if(fluidHandler.getFluidInTank(0).isEmpty()) { return 0; }
        if(fluidHandler.getFluidInTank(0).is(Fluids.WATER)) { return 1; }
        if(fluidHandler.getFluidInTank(0).is(ModFluids.POTION.get())) { return 2; }

        return 0;
    }
}
