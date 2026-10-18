package net.sachikonya.brewmaster.fluid;

import net.minecraft.world.item.ItemStack;

public interface FluidCapacityProvider {
    int getFluidCapacity(ItemStack itemStack);
}
