package net.sachikonya.brewmaster.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.item.FlaskTier;
import net.sachikonya.brewmaster.item.FlaskUpgradeData;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WondrousFlaskItem extends Item {
    public static final int DOSE = 250;
    private static final int DRINK_TICKS = 32;

    public WondrousFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

        if(fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) {
            return 0;
        }

        return Math.round(13.0f * fluidHandler.getFluidInTank(0).getAmount() / (float)getMaxCapacity(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

        if(fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) {
            return 0xFF0000;
        }

        float remainingFluid = (float)fluidHandler.getFluidInTank(0).getAmount() / (float)getMaxCapacity(stack);
        return Mth.hsvToRgb(remainingFluid / 3.0f, 1.0f, 1.0f);
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
            return stack;
        }

        FluidStack consumedFluid = fluidHandler.drain(DOSE, IFluidHandler.FluidAction.EXECUTE);
        if(consumedFluid.getAmount() != DOSE) {
            return stack;
        }
        ItemStack updatedStack = fluidHandler.getContainer();

        ItemStack potion = PotionItemFactory.createPotion(potionMixture);
        PotionContents potionContents = potion.get(DataComponents.POTION_CONTENTS);
        if(potionContents != null && potionContents.hasEffects()) {
            applyPotionEffects(potionContents, livingEntity);
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<Component> lines = new ArrayList<>();
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);

        int i = getFluidTypeID(fluidHandler);
        switch (i) {
            case 0 -> lines.add(Component.translatable("tooltip.brewmaster.empty_fluid"));
            case 1 -> lines.add(Component.translatable("block.minecraft.water"));
            case 2 -> {
                if(getPotionMixture(fluidHandler).isPotionEmpty()) {
                   lines.add(Component.translatable("effect.none").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
                } else {
                    lines.add(Component.translatable("item.brewmaster.mixed_potion").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
                    lines.addAll(getPotionMixture(fluidHandler).getTooltipLines());
                }
            }
            default -> lines.add(Component.literal("!!! UNKNOWN FLUID !!!").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_RED));
        }

        tooltipComponents.addAll(lines);
        tooltipComponents.add(Component.translatable("tooltip.brewmaster.charges", getRemainingCharges(stack), getMaxCharges(stack)));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) { return super.isFoil(stack); }

        PotionMixture mixture = getPotionMixture(fluidHandler);
        boolean hasEffects = mixture != null && getPotionMixture(fluidHandler) != PotionMixture.EMPTY;

        if(mixture != null && !mixture.effectDataList().isEmpty()) {
            Rarity rarity = PotionItemFactory.getRarity(mixture);
            updateRarity(stack, rarity);
        }

        return super.isFoil(stack) || hasEffects;
    }

    public static FlaskTier getTier(ItemStack itemStack) {
        return FlaskTier.byIndex(itemStack.getOrDefault(ModDataComponents.FLASK_UPGRADE.get(), FlaskUpgradeData.DEFAULT).tier());
    }

    public static int getMaxCapacity(ItemStack itemStack) {
        return getTier(itemStack).getCapacity();
    }

    public static int getMaxCharges(ItemStack itemStack) {
        return getMaxCapacity(itemStack) / DOSE;
    }

    public static ItemStack withTier(ItemStack itemStack, FlaskTier flaskTier) {
        ItemStack result = itemStack.copy();

        result.set(ModDataComponents.FLASK_UPGRADE.get(), new FlaskUpgradeData(flaskTier.ordinal()));
        return result;
    }

    private static int getFluidTypeID(IFluidHandler fluidHandler) {
        if(fluidHandler == null) { return 0; }

        if(fluidHandler.getFluidInTank(0).isEmpty()) { return 0; }
        if(fluidHandler.getFluidInTank(0).is(Fluids.WATER)) { return 1; }
        if(fluidHandler.getFluidInTank(0).is(ModFluids.POTION.get())) { return 2; }

        return 0;
    }

    private static PotionMixture getPotionMixture(IFluidHandler fluidHandler) {
        if(!fluidHandler.getFluidInTank(0).is(ModFluids.POTION.get())) {
            return PotionMixture.EMPTY;
        }

        return fluidHandler.getFluidInTank(0).getOrDefault(ModDataComponents.POTION_MIXTURE.get(), PotionMixture.EMPTY);
    }

    private static void updateRarity(ItemStack itemStack, Rarity rarity) {
        itemStack.set(DataComponents.RARITY, rarity);
    }

    public static int getRemainingCharges(ItemStack itemStack) {
        IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandlerItem == null) { return 0; }

        return fluidHandlerItem.getFluidInTank(0).getAmount() / DOSE;
    }

    public static boolean canFillFluidType(ItemStack flask, FluidStack fluid) {
        IFluidHandler fluidHandler = flask.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandler == null) { return false; }

        if(fluid.isEmpty() || !fluid.is(ModFluids.POTION.get())) {
            return false;
        }
        if(fluidHandler.getFluidInTank(0).isEmpty()) {
            return true;
        }

        return fluidHandler.getFluidInTank(0).is((ModFluids.POTION.get())) && Objects.equals(fluidHandler.getFluidInTank(0).get(ModDataComponents.POTION_MIXTURE), fluid.get(ModDataComponents.POTION_MIXTURE));
    }
}
