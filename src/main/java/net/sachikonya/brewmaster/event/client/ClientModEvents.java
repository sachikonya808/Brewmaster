package net.sachikonya.brewmaster.event.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.client.WitchCauldronRenderer;
import net.sachikonya.brewmaster.block.ModBlockEntities;
import net.sachikonya.brewmaster.effect.ModEffects;
import net.sachikonya.brewmaster.entity.ModEntities;
import net.sachikonya.brewmaster.entity.client.AnvilRenderer;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;
import net.sachikonya.brewmaster.screen.ModMenuTypes;
import net.sachikonya.brewmaster.screen.custom.SeaBlessedGourdScreen;
import net.sachikonya.brewmaster.screen.custom.WitchCauldronScreen;

@EventBusSubscriber(modid = Brewmaster.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.WITCH_CAULDRON_MENU.get(), WitchCauldronScreen::new);
        event.register(ModMenuTypes.SEA_BLESSED_GOURD_MENU.get(), SeaBlessedGourdScreen::new);
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event){
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.FALLING_ANVIL.get(), AnvilRenderer::new);
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

    //ModBlocks Client Extensions
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers entityRenderersEvent) {
        entityRenderersEvent.registerBlockEntityRenderer(ModBlockEntities.WITCH_CAULDRON_BE.get(), WitchCauldronRenderer::new);
    }

    //ModFluids Client Extensions
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
