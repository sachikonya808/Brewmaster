package net.sachikonya.brewmaster.fluid.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;

@EventBusSubscriber(modid = Brewmaster.MODID, value = Dist.CLIENT)
public class ModFluidsClientExtensions {
    private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation FLOWING = ResourceLocation.withDefaultNamespace("block/water_flow");

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() { return STILL; }
            @Override
            public ResourceLocation getFlowingTexture() { return FLOWING; }

            @Override
            public int getTintColor(FluidStack stack) {
                PotionMixture mixture = stack.getOrDefault(ModDataComponents.POTION_MIXTURE.get(), PotionMixture.EMPTY);
                return 0xFF000000 | mixture.getPotionColor();
            }
            @Override
            public int getTintColor() {
                return 0xFF000000 | PotionMixture.EMPTY.getPotionColor();
            }
        }, ModFluids.POTION_TYPE.get());
    }
}
