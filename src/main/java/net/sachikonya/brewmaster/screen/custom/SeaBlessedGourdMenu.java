package net.sachikonya.brewmaster.screen.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.sachikonya.brewmaster.item.SeaBlessedGourdHandler;
import net.sachikonya.brewmaster.item.SeaBlessedGourdState;
import net.sachikonya.brewmaster.item.custom.SeaBlessedGourdItem;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.registry.ModDataComponents;
import net.sachikonya.brewmaster.screen.ModMenuTypes;
import net.sachikonya.brewmaster.screen.client.CauldronFluidRender;

public class SeaBlessedGourdMenu extends AbstractContainerMenu {
    private final int gourdSlot;
    private final IItemHandler iItemHandler;
    private final Inventory inventory;

    public static final int MENU_HEIGHT = 204;

    public SeaBlessedGourdMenu(int containerId, Inventory inventory, FriendlyByteBuf byteBuf) {
        this(containerId, inventory, new ItemStackHandler(SeaBlessedGourdHandler.SIZE), byteBuf.readInt());
    }

    public SeaBlessedGourdMenu(int containerId, Inventory inventory, IItemHandler iItemHandler, int gourdSlot) {
        super(ModMenuTypes.SEA_BLESSED_GOURD_MENU.get(), containerId);
        this.gourdSlot = gourdSlot;
        this.iItemHandler = iItemHandler;
        this.inventory = inventory;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new SlotItemHandler(iItemHandler, SeaBlessedGourdHandler.POTION_SLOT, 56, 27){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.POTION);
            }
        });
        this.addSlot(new SlotItemHandler(iItemHandler, SeaBlessedGourdHandler.TEARS_SLOT, 56, 49){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GHAST_TEAR);
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().getItem(gourdSlot).getItem() instanceof SeaBlessedGourdItem;
    }

    public int getFluidType() { return 2; }
    public int getCapacity() { return SeaBlessedGourdItem.CAPACITY; }
    public ItemStack getGourdStack() { return inventory.getItem(gourdSlot); }
    public SeaBlessedGourdState getState() { return getGourdStack().getOrDefault(ModDataComponents.GOURD_DISTILL.get(), SeaBlessedGourdState.EMPTY); }

    public boolean isDistilling() {
        SeaBlessedGourdState state = getState();
        int progress = state.distillTime();

        return (progress > 0);
    }

    public int getBrewAmount() {
        ItemStack itemStack = getGourdStack();

        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if(fluidHandler == null) { return  0; }

        return fluidHandler.getFluidInTank(0).getAmount();
    }

    public int getPotionColor() {
        IFluidHandler fluidHandler = getGourdStack().getCapability(Capabilities.FluidHandler.ITEM);
        if(fluidHandler == null) { return CauldronFluidRender.WATER_COLOR; }
        PotionMixture mixture = fluidHandler.getFluidInTank(0).get(ModDataComponents.POTION_MIXTURE.get());
        if(mixture == null) { return CauldronFluidRender.WATER_COLOR; }

        return mixture.getPotionColor();
    }

    public int getScaledDistillProgress() {
        SeaBlessedGourdState state = getState();

        int progress = state.distillTime();
        int maxProgress = SeaBlessedGourdItem.DISTILL_TIME;
        int arrowPixelSize = 28;

        return progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        int invY = MENU_HEIGHT - 82;
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, invY + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            final int idx = i;
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, MENU_HEIGHT - 24) {
                @Override
                public boolean mayPickup(Player player) {
                    return idx != gourdSlot;
                }
            });
        }
    }
}
