package net.sachikonya.brewmaster.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.potion.PotionMixture;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Brewmaster.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionMixture>> POTION_MIXTURE =
            DATA_COMPONENTS.registerComponentType("potion_mixture", builder -> builder
                    .persistent(PotionMixture.CODEC)
                    .networkSynchronized(PotionMixture.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLASK_FLUID =
            DATA_COMPONENTS.registerComponentType("flask_fluid", builder -> builder
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static void register(IEventBus eventBus){
        DATA_COMPONENTS.register(eventBus);
    }
}
