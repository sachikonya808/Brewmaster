package net.sachikonya.brewmaster.fluid;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sachikonya.brewmaster.Brewmaster;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Brewmaster.MODID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, Brewmaster.MODID);

    public static final DeferredHolder<FluidType, FluidType> POTION_TYPE = FLUID_TYPES.register(
            "potion", () -> new FluidType(FluidType.Properties.create()
                    .density(1000)
                    .viscosity(1000)
                    .sound(SoundAction.get("bucket_fill"), SoundEvents.BOTTLE_FILL))
    );

    public static final DeferredHolder<Fluid, FlowingFluid> POTION =
            FLUIDS.register("potion", () -> new BaseFlowingFluid.Source(props()));
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_POTION =
            FLUIDS.register("flowing_potion", () -> new BaseFlowingFluid.Flowing(props()));

    private static BaseFlowingFluid.Properties props() {
        return new BaseFlowingFluid.Properties(POTION_TYPE, POTION, FLOWING_POTION);
    }

    public static void register (IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}