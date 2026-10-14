package net.sachikonya.brewmaster.block.client;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.entity.ModBlockEntities;

@EventBusSubscriber(modid = Brewmaster.MODID, value = Dist.CLIENT)
public class ModBlocksClientExtensions {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers entityRenderersEvent) {
        entityRenderersEvent.registerBlockEntityRenderer(ModBlockEntities.WITCH_CAULDRON_BE.get(), WitchCauldronRenderer::new);
    }
}
