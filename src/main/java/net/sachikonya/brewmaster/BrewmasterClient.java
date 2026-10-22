package net.sachikonya.brewmaster;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;

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
}
