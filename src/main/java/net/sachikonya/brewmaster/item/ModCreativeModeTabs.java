package net.sachikonya.brewmaster.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "brewmaster" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Brewmaster.MODID);

    // Creates a creative tab with the id "brewmaster:brewmaster_items_tab" for the mod's items, that is placed after the combat tab
    public static final Supplier<CreativeModeTab> BREWMASTER_ITEMS_TAB = CREATIVE_MODE_TAB.register("brewmaster_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.WITCH_CAULDRON.get()))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("creativetab.brewmaster.brewmaster_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // Register the items to a creative tab
                        output.accept(ModBlocks.WITCH_CAULDRON);
                        output.accept(ModItems.WONDROUS_FLASK);
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
