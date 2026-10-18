package net.sachikonya.brewmaster.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ComponentItemHandler;

public class SeaBlessedGourdHandler extends ComponentItemHandler {
    public static final int SIZE = 2;
    public static final int POTION_SLOT = 0, TEARS_SLOT = 1;

    public SeaBlessedGourdHandler(ItemStack itemStack) { super(itemStack, DataComponents.CONTAINER, SIZE); }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if(stack.isEmpty()) {
            return true;
        }

        return switch (slot) {
            case POTION_SLOT -> stack.is(Items.POTION);
            case TEARS_SLOT -> stack.is(Items.GHAST_TEAR);
            default -> false;
        };
    }
}