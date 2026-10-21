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
import net.sachikonya.brewmaster.fluid.FluidCapacityProvider;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.fluid.PotionFlaskFluidHandler;
import net.sachikonya.brewmaster.item.FlaskTier;
import net.sachikonya.brewmaster.item.FlaskUpgradeData;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WondrousFlaskItem extends Item implements FluidCapacityProvider {
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
        return PotionFlaskFluidHandler.DRINK_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);

        if(fluidHandler == null || fluidHandler.getFluidInTank(0).getAmount() < PotionFlaskFluidHandler.DOSE) {
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
        if(storedFluid.isEmpty() || storedFluid.getAmount() < PotionFlaskFluidHandler.DOSE) {
            return stack;
        }

        PotionMixture potionMixture = storedFluid.get(ModDataComponents.POTION_MIXTURE.get());
        if(potionMixture == null) {
            return stack;
        }

        FluidStack consumedFluid = fluidHandler.drain(PotionFlaskFluidHandler.DOSE, IFluidHandler.FluidAction.EXECUTE);
        if(consumedFluid.getAmount() != PotionFlaskFluidHandler.DOSE) {
            return stack;
        }

        ItemStack updatedStack = fluidHandler.getContainer();

        ItemStack potion = PotionItemFactory.createPotion(potionMixture);
        PotionContents potionContents = potion.get(DataComponents.POTION_CONTENTS);
        if(potionContents != null && potionContents.hasEffects()) {
            PotionFlaskFluidHandler.applyPotionEffects(potionContents, livingEntity);
        }

        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 1.0f);
        return updatedStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        switch (getTier(stack).ordinal()) {
            case 1 -> tooltipComponents.add(Component.translatable("tooltip.brewmaster.flask_iron").withStyle(ChatFormatting.WHITE));
            case 2 -> tooltipComponents.add(Component.translatable("tooltip.brewmaster.flask_gold").withStyle(ChatFormatting.GOLD));
            case 3 -> tooltipComponents.add(Component.translatable("tooltip.brewmaster.flask_diamond").withStyle(ChatFormatting.AQUA));
            case 4 -> tooltipComponents.add(Component.translatable("tooltip.brewmaster.flask_netherite").withStyle(ChatFormatting.DARK_PURPLE));
            default -> tooltipComponents.add(Component.translatable("tooltip.brewmaster.flask_base").withStyle(ChatFormatting.GREEN));
        }

        List<Component> lines = new ArrayList<>();
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);

        int i = PotionFlaskFluidHandler.getFluidTypeID(fluidHandler);
        switch (i) {
            case 0 -> lines.add(Component.translatable("tooltip.brewmaster.empty_fluid"));
            case 1 -> lines.add(Component.translatable("block.minecraft.water"));
            case 2 -> {
                if(PotionFlaskFluidHandler.getPotionMixture(fluidHandler).isPotionEmpty()) {
                   lines.add(Component.translatable("effect.none").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
                } else {
                    lines.add(Component.translatable("item.brewmaster.mixed_potion").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
                    lines.addAll(PotionFlaskFluidHandler.getPotionMixture(fluidHandler).getTooltipLines());
                }
            }
            default -> lines.add(Component.literal("!!! UNKNOWN FLUID !!!").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_RED));
        }

        tooltipComponents.addAll(lines);
        tooltipComponents.add(Component.translatable("tooltip.brewmaster.charges", PotionFlaskFluidHandler.getRemainingCharges(stack), PotionFlaskFluidHandler.getMaxCharges(getMaxCapacity(stack))));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return PotionFlaskFluidHandler.shouldGlow(stack);
    }

    @Override
    public int getFluidCapacity(ItemStack itemStack) {
        return getMaxCapacity(itemStack);
    }

    public static FlaskTier getTier(ItemStack itemStack) {
        return FlaskTier.byIndex(itemStack.getOrDefault(ModDataComponents.FLASK_UPGRADE.get(), FlaskUpgradeData.DEFAULT).tier());
    }

    public static int getMaxCapacity(ItemStack itemStack) {
        return getTier(itemStack).getCapacity();
    }

    public static ItemStack withTier(ItemStack itemStack, FlaskTier flaskTier) {
        ItemStack result = itemStack.copy();

        result.set(ModDataComponents.FLASK_UPGRADE.get(), new FlaskUpgradeData(flaskTier.ordinal()));
        return result;
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
        if(fluidHandler.getFluidInTank(0).getAmount() >= PotionFlaskFluidHandler.capacityOf(flask)) {
            return false;
        }

        return fluidHandler.getFluidInTank(0).is((ModFluids.POTION.get())) && Objects.equals(fluidHandler.getFluidInTank(0).get(ModDataComponents.POTION_MIXTURE), fluid.get(ModDataComponents.POTION_MIXTURE));
    }
}
