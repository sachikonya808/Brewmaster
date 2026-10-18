package net.sachikonya.brewmaster.screen.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.screen.ModMenuTypes;

public class WitchCauldronMenu extends AbstractContainerMenu {
    public final WitchCauldronBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    //ugly hack lmao, original is {29, 24, 20, 16, 11, 6, 0}; but I reversed it to fix a rendering issue
    private static final int[] BUBBLELENGTHS = new int[] {0, 6, 11, 16, 20, 24, 29};

    public static final int MENU_HEIGHT = 188;

    public WitchCauldronMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(WitchCauldronBlockEntity.CONTAINER_DATA_SIZE)); // <--- MUST BE THE SAME AS getCount() OR IT'LL CRASH
    }

    public WitchCauldronMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.WITCH_CAULDRON_MENU.get(), pContainerId);
        this.blockEntity = ((WitchCauldronBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, 81, 19));
        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 1, 126, 54));
        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 2,31,34));

        addDataSlots(data);
    }

    public int getBrewAmount() { return data.get(2); }
    public int getIngredientsUsed() { return data.get(3); }
    public int getMaxIngredients() { return data.get(4); }
    public int getFluidType() { return data.get(5); }
    public int getRemainingFuel() { return data.get(8); }

    public PotionMixture getPotionMixture() { return blockEntity.getPotionMixture(); }

    public boolean isCrafting(){
        return data.get(0) > 0;
    }
    public boolean isBottling() { return data.get(6) > 0; }

    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 28;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }
    public int getScaledBottlingArrowProgress() {
        int progress = this.data.get(6);
        int maxProgress = this.data.get(7);
        int arrowPixelSize = 28;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    public int getScaledRemainingFuel() {
        int fuel = this.data.get(8);
        int maxFuel = 20;
        int fuelPixelSize = 18;

        return fuel != 0 ? fuel * fuelPixelSize / maxFuel : 0;
    }

    public int getScaledBubbles() {
        return BUBBLELENGTHS[data.get(0) / 2 % 7];
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.WITCH_CAULDRON.get());
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
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must be the number of slots you have!
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
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, MENU_HEIGHT - 24));
        }
    }
}
