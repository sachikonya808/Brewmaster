package net.sachikonya.brewmaster;

import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.block.ModBlockEntities;
import net.sachikonya.brewmaster.effect.ModEffects;
import net.sachikonya.brewmaster.entity.ModEntities;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.item.ModCreativeModeTabs;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.loot.ModLoot;
import net.sachikonya.brewmaster.recipe.ExtraBrewingStandRecipes;
import net.sachikonya.brewmaster.recipe.ModRecipes;
import net.sachikonya.brewmaster.registry.ModDataComponents;
import net.sachikonya.brewmaster.screen.ModMenuTypes;
import net.sachikonya.brewmaster.villager.ModVillagers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

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
        ModLoot.register(modEventBus);
        ModVillagers.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP, ARE YA READY TO BREW SOME POTIONS?!");

        Config.EFFECT_STRINGS.get().forEach((effect) -> LOGGER.info("EFFECT >> {}", effect));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }
}
