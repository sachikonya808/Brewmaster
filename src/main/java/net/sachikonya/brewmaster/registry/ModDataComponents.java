package net.sachikonya.brewmaster.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.item.FlaskUpgradeData;
import net.sachikonya.brewmaster.item.SeaBlessedGourdState;
import net.sachikonya.brewmaster.potion.PotionMixture;

import java.util.function.Supplier;

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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FlaskUpgradeData>> FLASK_UPGRADE =
            DATA_COMPONENTS.registerComponentType("flask_upgrade", builder -> builder
                    .persistent(FlaskUpgradeData.CODEC)
                    .networkSynchronized(FlaskUpgradeData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SeaBlessedGourdState>> GOURD_DISTILL =
            DATA_COMPONENTS.registerComponentType("gourd_distill", builder -> builder
                    .persistent(SeaBlessedGourdState.CODEC)
                    .networkSynchronized(SeaBlessedGourdState.STREAM_CODEC));

    public static final Supplier<DataComponentType<Boolean>> TANK_LOCKED =
            DATA_COMPONENTS.registerComponentType("tank_locked", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static void register(IEventBus eventBus){
        DATA_COMPONENTS.register(eventBus);
    }
}
