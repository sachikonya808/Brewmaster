package net.sachikonya.brewmaster.potion;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;
import net.sachikonya.brewmaster.registry.ModDataComponents;

import java.util.Objects;

public class PotionFlaskFluidHandler extends FluidHandlerItemStack {
    public PotionFlaskFluidHandler(ItemStack container) {
        super(ModDataComponents.FLASK_FLUID, container, WondrousFlaskItem.MAX_CAPACITY);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if(fluid.isEmpty() || !fluid.is(ModFluids.POTION.get())) {
            return false;
        }

        FluidStack storedFluid = getFluid();

        if(storedFluid.isEmpty()) {
            return true;
        }

        return storedFluid.is((ModFluids.POTION.get())) && Objects.equals(storedFluid.get(ModDataComponents.POTION_MIXTURE), fluid.get(ModDataComponents.POTION_MIXTURE));
    }
}
