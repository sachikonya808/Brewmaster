package net.sachikonya.brewmaster;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.block.entity.ModBlockEntities;
import net.sachikonya.brewmaster.effect.ModEffects;
import net.sachikonya.brewmaster.entity.ModEntities;
import net.sachikonya.brewmaster.entity.client.AnvilRenderer;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.item.ModCreativeModeTabs;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.recipe.ExtraBrewingStandRecipes;
import net.sachikonya.brewmaster.recipe.ModRecipes;
import net.sachikonya.brewmaster.registry.ModDataComponents;
import net.sachikonya.brewmaster.screen.ModMenuTypes;
import net.sachikonya.brewmaster.screen.custom.SeaBlessedGourdScreen;
import net.sachikonya.brewmaster.screen.custom.WitchCauldronScreen;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Brewmaster.MODID)
public class Brewmaster {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "brewmaster";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Brewmaster(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Brewmaster) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the Deferred Register to the mod event bus so tabs get registered
        ModCreativeModeTabs.register(modEventBus);
        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.register(modEventBus);
        // Register the Deferred Register to the mod event bus so that custom block entities get registered
        ModBlockEntities.register(modEventBus);
        // Register the Deferred Register to the mod event bus so that custom menus get registered
        ModMenuTypes.register(modEventBus);

        ModDataComponents.register(modEventBus);
        ModFluids.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModEffects.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(ExtraBrewingStandRecipes::register);


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.WITCH_CAULDRON_MENU.get(), WitchCauldronScreen::new);
            event.register(ModMenuTypes.SEA_BLESSED_GOURD_MENU.get(), SeaBlessedGourdScreen::new);
        }

        @SubscribeEvent
        public static void registerCaps(RegisterCapabilitiesEvent event){
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.WITCH_CAULDRON_BE.get(), (be, side) -> be.tank);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.FALLING_ANVIL.get(), AnvilRenderer::new);
        }
    }
}
