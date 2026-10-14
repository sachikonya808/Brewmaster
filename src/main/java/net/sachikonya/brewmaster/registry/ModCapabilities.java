package net.sachikonya.brewmaster.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.entity.ModBlockEntities;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.potion.PotionFlaskFluidHandler;

@EventBusSubscriber(modid = Brewmaster.MODID)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.WITCH_CAULDRON_BE.get(),
                (blockEntity, side) -> blockEntity.getFluidTank());

        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> new PotionFlaskFluidHandler(stack), ModItems.WONDROUS_FLASK.get());
    }
}
