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
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.sachikonya.brewmaster.fluid.FluidCapacityProvider;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.fluid.PotionFlaskFluidHandler;
import net.sachikonya.brewmaster.item.SeaBlessedGourdHandler;
import net.sachikonya.brewmaster.item.SeaBlessedGourdState;
import net.sachikonya.brewmaster.potion.PotionEffectData;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;
import net.sachikonya.brewmaster.screen.custom.SeaBlessedGourdMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeaBlessedGourdItem extends Item implements FluidCapacityProvider {
    public static final int CAPACITY = 2500;
    public static final int DISTILL_COST = 1;
    public static final int DISTILL_TIME = 12000;

    public SeaBlessedGourdItem(Properties properties) {
        super(properties);
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(level instanceof ServerLevel serverLevel && entity instanceof Player) {
            SeaBlessedGourdHandler inventory = new SeaBlessedGourdHandler(stack);
            ItemStack potionInput = inventory.getStackInSlot(SeaBlessedGourdHandler.POTION_SLOT);
            ItemStack tears = inventory.getStackInSlot(SeaBlessedGourdHandler.TEARS_SLOT);

            if(!potionInput.is(Items.POTION)) {
                updateDistillTime(0,stack);
                return;
            }

            SeaBlessedGourdState distillState = stack.getOrDefault(ModDataComponents.GOURD_DISTILL, SeaBlessedGourdState.EMPTY);
            int distillTime = distillState.distillTime();

            boolean canDistill = tears.is(Items.GHAST_TEAR) && tears.getCount() >= DISTILL_COST;
            if(canDistill){
                if(level.isClientSide()) { return; }

                IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);
                if(fluidHandler == null) { return; }

                FluidStack storedFluid = fluidHandler.getFluidInTank(0);
                if (storedFluid.getAmount() >= CAPACITY) { return; }

                PotionMixture potionMixture = retrieveMixtureFromPotion(potionInput);
                if(potionMixture == null) { return; }

                FluidStack potion = new FluidStack(ModFluids.POTION.get(), PotionFlaskFluidHandler.DOSE);
                potion.set(ModDataComponents.POTION_MIXTURE.get(), potionMixture);
                boolean accepted = canFillFluidType(stack, potion);
                if(!accepted) {
                    updateDistillTime(0, stack);
                    return;
                }

                updateDistillTime(distillTime+1, stack);
                System.out.println("DT: "+distillTime);
                if(distillTime >= DISTILL_TIME) {
                    if(fluidHandler.getFluidInTank(0).isEmpty()) {
                        fluidHandler.fill(potion, IFluidHandler.FluidAction.EXECUTE);
                        updateDistillTime(0,stack);
                    } else {
                        fluidHandler.fill(potion, IFluidHandler.FluidAction.EXECUTE);
                        updateDistillTime(0, stack);
                    }

                    inventory.extractItem(SeaBlessedGourdHandler.TEARS_SLOT, DISTILL_COST, false);
                }
            } else {
                updateDistillTime(0,stack);
            }
        }
    }

    private void updateDistillTime(int time, ItemStack itemStack) {
        itemStack.set(ModDataComponents.GOURD_DISTILL, new SeaBlessedGourdState(time));
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

        return Math.round(13.0f * fluidHandler.getFluidInTank(0).getAmount() / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

        if(fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) {
            return 0xFF0000;
        }

        float remainingFluid = (float)fluidHandler.getFluidInTank(0).getAmount() / (float)CAPACITY;
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
    public int getFluidCapacity(ItemStack itemStack) {
        return CAPACITY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        if(player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                int inventorySlot = (usedHand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40);

                player.openMenu(
                        new SimpleMenuProvider((id, inv, p) ->
                                new SeaBlessedGourdMenu(id, inv, new SeaBlessedGourdHandler(itemStack), inventorySlot),
                                itemStack.getHoverName()),
                        buffer -> buffer.writeInt(inventorySlot)
                );
                return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
            }
        }

        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);

        if(fluidHandler != null && fluidHandler.getFluidInTank(0).getAmount() >= PotionFlaskFluidHandler.DOSE && !player.isSecondaryUseActive()) {
            return ItemUtils.startUsingInstantly(level, player, usedHand);
        }

        return InteractionResultHolder.fail(itemStack);
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
        tooltipComponents.add(Component.translatable("tooltip.brewmaster.charges", PotionFlaskFluidHandler.getRemainingCharges(stack), PotionFlaskFluidHandler.getMaxCharges(CAPACITY)));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return PotionFlaskFluidHandler.shouldGlow(stack);
    }

    public static PotionMixture retrieveMixtureFromPotion(ItemStack itemStack) {
        PotionMixture stored = itemStack.get(ModDataComponents.POTION_MIXTURE.get());
        if(stored != null) return stored;

        PotionContents contents = itemStack.get(DataComponents.POTION_CONTENTS);
        if(contents == null) return PotionMixture.EMPTY;

        List<PotionEffectData> effectData = new ArrayList<>();
        for(MobEffectInstance instance : contents.getAllEffects()) {
            effectData.add(new PotionEffectData(instance.getEffect(), instance.getAmplifier(), instance.getDuration()));
        }

        return effectData.isEmpty() ? PotionMixture.EMPTY : new PotionMixture(effectData, 1.0, 67);
    }

    public static boolean canFillFluidType(ItemStack gourd, FluidStack fluid) {
        IFluidHandler fluidHandler = gourd.getCapability(Capabilities.FluidHandler.ITEM, null);
        if(fluidHandler == null) { return false; }

        if(fluid.isEmpty() || !fluid.is(ModFluids.POTION.get())) {
            return false;
        }
        if(fluidHandler.getFluidInTank(0).isEmpty()) {
            return true;
        }

        return fluidHandler.getFluidInTank(0).is((ModFluids.POTION.get())) && Objects.equals(fluidHandler.getFluidInTank(0).get(ModDataComponents.POTION_MIXTURE), fluid.get(ModDataComponents.POTION_MIXTURE));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}