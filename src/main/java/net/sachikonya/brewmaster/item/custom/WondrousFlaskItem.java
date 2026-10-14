package net.sachikonya.brewmaster.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;

public class WondrousFlaskItem extends Item {
    public static final int MAX_CAPACITY = 1000;
    public static final int DOSE = 250;
    private static final int DRINK_TICKS = 32;

    public WondrousFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return DRINK_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);

        if(fluidHandler == null || fluidHandler.getFluidInTank(0).getAmount() < DOSE) {
            return InteractionResultHolder.fail(itemStack);
        }

        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(level.isClientSide()) {
            return stack;
        }

        IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandler == null) {
            return stack;
        }

        FluidStack storedFluid = fluidHandler.getFluidInTank(0);
        if(storedFluid.isEmpty() || storedFluid.getAmount() < DOSE) {
            return stack;
        }

        PotionMixture potionMixture = storedFluid.get(ModDataComponents.POTION_MIXTURE.get());
        if(potionMixture == null) {
            System.out.println("[Brewmaster] Bottle has NO potion_MIXTURE component!");
            return stack;
        }
        ItemStack potion = PotionItemFactory.createPotion(potionMixture);


        FluidStack consumedFluid = fluidHandler.drain(DOSE, IFluidHandler.FluidAction.EXECUTE);
        if(consumedFluid.getAmount() != DOSE) {
            return stack;
        }
        ItemStack updatedStack = fluidHandler.getContainer();

        PotionContents potionContents = potion.get(DataComponents.POTION_CONTENTS);
        if(potionContents != null && potionContents.hasEffects()) {
            applyPotionEffects(potionContents, livingEntity);
        } else {
            System.out.println("[Brewmaster]: Bottle was consumed but its fluid had no effects...");
        }

        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 1.0f);
        return updatedStack;
    }

    private static void applyPotionEffects(PotionContents contents, LivingEntity livingEntity) {
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


    public static int getRemainingCharges(ItemStack itemStack) {
        IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandlerItem == null) { return 0; }

        return fluidHandlerItem.getFluidInTank(0).getAmount() / DOSE;
    }
}
