package net.sachikonya.brewmaster;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sachikonya.brewmaster.effect.ModEffects;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Brewmaster.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Brewmaster.MODID, value = Dist.CLIENT)
public class BrewmasterClient {
    public BrewmasterClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Brewmaster.LOGGER.info("HELLO FROM CLIENT SETUP");
        Brewmaster.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        event.enqueueWork(() -> ItemProperties.register(
                ModItems.WONDROUS_FLASK.get(),
                ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "fill_level"),
                ((itemStack, clientLevel, livingEntity, i) -> {
                    IFluidHandler handler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
                    if(handler == null) return 0f;
                    int amount = handler.getFluidInTank(0).getAmount();
                    return amount <= 0 ? 0f : (float) amount / WondrousFlaskItem.getMaxCapacity(itemStack);
                }
        )));
    }

    @SubscribeEvent
    static void registerFlaskPotionColors(RegisterColorHandlersEvent.Item event) {
        event.register(((itemStack, i) -> {
            if(i != 1) return 0xFFFFFFFF;

            IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
            if(fluidHandler == null) return 0xFFFFFFFF;

            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if(fluidStack.is(Fluids.WATER)) return 0xFF3F76E4;

            PotionMixture mixture = fluidStack.get(ModDataComponents.POTION_MIXTURE.get());
            if(mixture == null) return 0xFFFFFFFF;

            PotionContents contents = PotionItemFactory.createPotion(mixture).get(DataComponents.POTION_CONTENTS);
            return contents == null ? 0xFFFFFFFF : (0xFF000000 | contents.getColor());
        }), ModItems.WONDROUS_FLASK.get());
    }

    @SubscribeEvent
    static void onLuckyPotionTooltip(ItemTooltipEvent event) {
        PotionContents contents = event.getItemStack().get(DataComponents.POTION_CONTENTS);
        if(contents == null) { return; }

        for(MobEffectInstance instance : contents.getAllEffects()) {
            if (instance.getEffect().is(ModEffects.LUCKY_POTION.getId())) {
                event.getToolTip().add(Component.translatable("tooltip.brewmaster.lucky_potion_hint").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                break;
            }
        }
    }
}
