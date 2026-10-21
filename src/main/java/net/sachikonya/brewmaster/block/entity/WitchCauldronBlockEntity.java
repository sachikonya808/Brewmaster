package net.sachikonya.brewmaster.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.sachikonya.brewmaster.block.WitchCauldronState;
import net.sachikonya.brewmaster.block.custom.WitchCauldronBlock;
import net.sachikonya.brewmaster.fluid.ModFluids;
import net.sachikonya.brewmaster.fluid.PotionFlaskFluidHandler;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.item.ModTags;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;
import net.sachikonya.brewmaster.potion.PotionItemFactory;
import net.sachikonya.brewmaster.potion.PotionMixture;
import net.sachikonya.brewmaster.recipe.*;
import net.sachikonya.brewmaster.registry.ModDataComponents;
import net.sachikonya.brewmaster.screen.custom.WitchCauldronMenu;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WitchCauldronBlockEntity extends BlockEntity implements MenuProvider {
    // -----------------------------------------------------------------------------------------------------------------
    // Init / Overrides + Core Functionality

    public static final int SLOT_COUNT = 3; //<---- How many slots should the block have!
    public static final int UPGRADE_COUNT = 3;

    public final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public final ItemStackHandler upgradeHandler = new ItemStackHandler(UPGRADE_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            recalcUpgrades();
            setChanged();
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModTags.Items.CAULDRON_UPGRADES);
        }
    };

    public static final int INGREDIENT_SLOT = 0;
    public static final int BOTTLE_SLOT = 1;
    public static final int FUEL_SLOT = 2;

    private static final int FUEL_USES = 20;
    public static final int FUEL_BONUS = 2;


    public static final int UPGRADE_SLOT_1 = 0;
    public static final int UPGRADE_SLOT_2 = 1;
    public static final int UPGRADE_SLOT_3 = 2;
    public static final int MAX_BASE_INGREDIENTS = 2;
    public static final int UPGRADE_BONUS_PER_BOOK = 2;
    private int ingredientsUpgrade = 0;
    private int ingredientsUsed = 0;


    private int fuel = 0;
    private int progress, bottlingProgress = 0;
    private int maxProgress, bottlingMaxProgress = 72;

    protected final ContainerData data;
    public static final int CONTAINER_DATA_SIZE = 9;
    public WitchCauldronBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.WITCH_CAULDRON_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> WitchCauldronBlockEntity.this.progress;
                    case 1 -> WitchCauldronBlockEntity.this.maxProgress;
                    case 2 -> WitchCauldronBlockEntity.this.tank.getFluidAmount();
                    case 3 -> WitchCauldronBlockEntity.this.ingredientsUsed;
                    case 4 -> WitchCauldronBlockEntity.this.getFinalMaxIngredients();
                    case 5 -> WitchCauldronBlockEntity.this.getFluidTypeID();
                    case 6 -> WitchCauldronBlockEntity.this.bottlingProgress;
                    case 7 -> WitchCauldronBlockEntity.this.bottlingMaxProgress;
                    case 8 -> WitchCauldronBlockEntity.this.fuel;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> WitchCauldronBlockEntity.this.progress = value;
                    case 1 -> WitchCauldronBlockEntity.this.maxProgress = value;
                    case 3 -> WitchCauldronBlockEntity.this.ingredientsUsed = value;
                    case 6 -> WitchCauldronBlockEntity.this.bottlingProgress = value;
                    case 7 -> WitchCauldronBlockEntity.this.bottlingMaxProgress = value;
                    case 8 -> WitchCauldronBlockEntity.this.fuel = value;
                }
            }

            @Override
            public int getCount() { return CONTAINER_DATA_SIZE; } //<-- How many variables are going to get stored inside the constructor!
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.brewmaster.witch_cauldron");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new WitchCauldronMenu(i, inventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(SLOT_COUNT);
        SimpleContainer upgrades = new SimpleContainer(UPGRADE_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        for (int i = 0; i < UPGRADE_COUNT; i++) {
            upgrades.setItem(i, upgradeHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
        Containers.dropContents(this.level, this.worldPosition, upgrades);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("witch_cauldron.progress", progress);
        pTag.putInt("witch_cauldron.max_progress", maxProgress);
        pTag.putInt("witch_cauldron.ingredients_used", ingredientsUsed);
        pTag.putInt("witch_cauldron.bottling_progress", bottlingProgress);
        pTag.putInt("witch_cauldron.max_bottling_progress",bottlingMaxProgress);
        pTag.put("Tank", tank.writeToNBT(pRegistries, new CompoundTag()));
        pTag.putInt("witch_cauldron.fuel", fuel);
        pTag.put("upgrades", upgradeHandler.serializeNBT(pRegistries));

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        tank.readFromNBT(pRegistries, pTag.getCompound("Tank"));
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("witch_cauldron.progress");
        maxProgress = pTag.getInt("witch_cauldron.max_progress");
        ingredientsUsed = pTag.getInt("witch_cauldron.ingredients_used");
        bottlingProgress = pTag.getInt("witch_cauldron.bottling_progress");
        bottlingMaxProgress = pTag.getInt("witch_cauldron.max_bottling_progress");
        fuel = pTag.getInt("witch_cauldron.fuel");
        upgradeHandler.deserializeNBT(pRegistries, pTag.getCompound("upgrades"));
        recalcUpgrades();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    WitchCauldronState state = WitchCauldronState.WAITING;
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if(!isHeated()) {
            resetProgress();
            updateBrewingState(level, blockPos, blockState, false);
            return;
        }

        ItemStack input = itemHandler.getStackInSlot(INGREDIENT_SLOT);
        ItemStack output = itemHandler.getStackInSlot(BOTTLE_SLOT);
        ItemStack fuelSlot = itemHandler.getStackInSlot(FUEL_SLOT);

        if (fuel <= 0 && fuelSlot.is(Items.BLAZE_POWDER)) {
            fuel = FUEL_USES;
            itemHandler.extractItem(FUEL_SLOT, 1, false);
            setChanged(level, blockPos, blockState);
        }

        switch (state) {
            case WAITING -> {
                syncIngredientCount();
                boolean brewingBase = input.is(Items.NETHER_WART) && hasWater();
                boolean addingIngredient = hasRecipe() && hasPotionFluid();
                boolean bottling = hasPotionFluid() && (output.is(Items.GLASS_BOTTLE));
                boolean filling = output.is(ModItems.WONDROUS_FLASK) && canFillFlask(output);

                updateBrewingState(level, blockPos, blockState, (addingIngredient || brewingBase || bottling));

                if(addingIngredient) {
                    state = WitchCauldronState.BREWING;
                }
                if(brewingBase) {
                    state = WitchCauldronState.BREWING_BASE;
                }
                if(bottling) {
                    state = WitchCauldronState.BOTTLING;
                }
                if(filling) {
                    state = WitchCauldronState.FILLING;
                }

                resetProgress();
            }
            case BREWING_BASE -> {
                if(!(input.is(Items.NETHER_WART) && hasWater())) { state = WitchCauldronState.WAITING; }
                increaseCraftingProgress();
                setChanged(level, blockPos, blockState);

                if(hasCraftingFinished()) {
                    convertWatertoPotion();

                    level.playSound(null,blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);
                    resetProgress();
                }
            }
            case BREWING -> {
                if(!(hasRecipe() && hasPotionFluid())) { state = WitchCauldronState.WAITING; }
                increaseCraftingProgress();
                setChanged(level, blockPos, blockState);

                if(hasCraftingFinished()) {
                    processIngredient();

                    level.playSound(null,blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);
                    resetProgress();
                }
            }
            case BOTTLING -> {
                if(!hasPotionFluid() || !output.is(Items.GLASS_BOTTLE)) { state = WitchCauldronState.WAITING; }

                increaseBottlingProgress();
                setChanged(level, blockPos, blockState);

                if(hasBottlingFinished()) {
                    bottlePotion();

                    level.playSound(null,blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);
                    resetProgress();
                }
            }
            case FILLING -> {
                if(output.isEmpty() || (output.is(ModItems.WONDROUS_FLASK.get()) && !canFillFlask(output))) { state = WitchCauldronState.WAITING; }

                increaseBottlingProgress();
                setChanged(level, blockPos, blockState);

                if(hasBottlingFinished()) {
                    bottlePotion();

                    level.playSound(null,blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f);
                    resetProgress();
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Helpers / Getters

    private void consumeFuel() {
        if(this.fuel > 0) {
            --this.fuel;
        }
    }

    private void updateBrewingState(Level level, BlockPos pos, BlockState state, boolean active) {
        if(hasPotionFluid()){
            level.setBlock(pos, state.setValue(WitchCauldronBlock.HAS_OR_IS_BREWING, true), Block.UPDATE_ALL);
            return;
        }

        if(state.getValue(WitchCauldronBlock.HAS_OR_IS_BREWING) != active) {
            level.setBlock(pos, state.setValue(WitchCauldronBlock.HAS_OR_IS_BREWING, active), Block.UPDATE_ALL);
        }
    }

    private void resetProgress() {
        progress = 0;
        bottlingProgress = 0;
        maxProgress = 72;
        bottlingMaxProgress = 72;
    }

    public boolean isHeated() {
        if (level == null) return false;
        BlockState below = level.getBlockState(worldPosition.below());
        return below.is(BlockTags.CAMPFIRES) && below.hasProperty(CampfireBlock.LIT) && below.getValue(CampfireBlock.LIT);
    }

    private boolean hasRecipe() {
        ItemStack ingredientStack = itemHandler.getStackInSlot(INGREDIENT_SLOT);
        Optional<RecipeHolder<BrewingRecipe>> recipe = BrewingRecipeLookup.findRecipe(level, ingredientStack);
        if (recipe.isEmpty()) {
            return false;
        }

        return canAddIngredients();
    }

    public void syncIngredientCount() {
        PotionMixture mixture = getPotionMixture();
        if(mixture != PotionMixture.EMPTY) {
            ingredientsUsed = mixture.ingredientsAdded();
            setChanged();
        } else {
            ingredientsUsed = 0;
            setChanged();
        }
    }

    private void recalcUpgrades() {
        int count = 0;
        for(int i = 0 ; i < upgradeHandler.getSlots() ; i++) {
            if(!upgradeHandler.getStackInSlot(i).isEmpty()) {
                count++;
            }
        }
        ingredientsUpgrade = count * UPGRADE_BONUS_PER_BOOK;
    }

    public int getFinalMaxIngredients() {
        int fuelBonus = fuel > 0 ? FUEL_BONUS : 0;
        return MAX_BASE_INGREDIENTS + fuelBonus + ingredientsUpgrade;
    }

    public ItemStack getIngredient() {
        return itemHandler.getStackInSlot(INGREDIENT_SLOT);
    }
    public boolean canAddIngredients() { return ingredientsUsed < getFinalMaxIngredients(); }


    public int getFuel() { return fuel; }
    public int getIngredientsUsed() { return ingredientsUsed; }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }
    private boolean hasBottlingFinished() {
        return this.bottlingProgress >= this.bottlingMaxProgress;
    }
    private void increaseCraftingProgress() {
        progress++;
    }
    private void increaseBottlingProgress() {
        bottlingProgress++;
    }

    private void syncToClient() {
        if(level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    //Fluid / Potion Shenanigans

    //Creating the Fluid Tank
    public static final int CAPACITY = 1000;

    public final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            syncToClient();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.is(Fluids.WATER) || stack.is(ModFluids.POTION.get());
        }
    };

    public FluidTank getFluidTank() {
        return tank;
    }

    public int getFluidTypeID() {
        FluidStack fluid = tank.getFluid();

        if(fluid.isEmpty()) { return 0; }
        if(fluid.is(Fluids.WATER)) { return 1; }
        if(fluid.is(ModFluids.POTION.get())){ return 2; }

        return 0;
    }

    public boolean hasWater() {
        return tank.getFluid().is(Fluids.WATER);
    }
    public boolean hasPotionFluid() {
        return tank.getFluid().is(ModFluids.POTION.get());
    }

    public PotionMixture getPotionMixture() {
        FluidStack fluid = tank.getFluid();
        if (!fluid.is(ModFluids.POTION.get())){
            return PotionMixture.EMPTY;
        }

        return fluid.getOrDefault(ModDataComponents.POTION_MIXTURE.get(), PotionMixture.EMPTY);
    }

    public void setPotionMixture(PotionMixture mixture) {
        FluidStack fluid = tank.getFluid();

        if(!fluid.is((ModFluids.POTION.get()))) {
            return;
        }

        fluid.set(ModDataComponents.POTION_MIXTURE.get(),mixture);
        setChanged();
        syncToClient();
    }

    public boolean convertWatertoPotion() {
        if (level == null || level.isClientSide()){
            return false;
        }
        if(!hasWater()){
            return false;
        }

        if(tank.getFluidAmount() != CAPACITY) {
            return false;
        }

        consumeFuel();
        FluidStack potion = new FluidStack(ModFluids.POTION.get(), CAPACITY);
        potion.set(ModDataComponents.POTION_MIXTURE.get(), PotionMixture.EMPTY);
        tank.setFluid(potion);
        itemHandler.extractItem(INGREDIENT_SLOT, 1, false);
        ingredientsUsed = 0;

        setChanged();
        syncToClient();
        return true;
    }

    public void processIngredient() {
        if (level == null || level.isClientSide()){
            return;
        }
        if(!hasPotionFluid()){
            return;
        }
        if(!canAddIngredients()){
            return;
        }

        ItemStack ingredientStack = itemHandler.getStackInSlot(INGREDIENT_SLOT);
        if (ingredientStack.isEmpty()) {
            return;
        }

        Optional<RecipeHolder<BrewingRecipe>> recipe = BrewingRecipeLookup.findRecipe(level, ingredientStack);
        if (recipe.isEmpty()) {
            return;
        }

        consumeFuel();
        PotionMixture mixture = getPotionMixture();
        PotionMixture updated = BrewingRecipeLookup.applyRecipe(mixture,recipe.get().value());
        setPotionMixture(updated);

        //Get the container (bucket/glass bottle/etc.) of the ingredient
        ItemStack leftoverContainer = ingredientStack.hasCraftingRemainingItem() ? ingredientStack.getCraftingRemainingItem() : ItemStack.EMPTY;

        itemHandler.extractItem(INGREDIENT_SLOT, 1, false);
        ingredientsUsed++;

        //Pop it into the world
        if(!leftoverContainer.isEmpty() && level != null) {
            if(ingredientStack.isEmpty()) {
                itemHandler.setStackInSlot(INGREDIENT_SLOT, leftoverContainer);
            } else {
                Block.popResource(level, worldPosition, leftoverContainer);
            }
        }

        setChanged();
    }

    private void bottlePotion() {
        ItemStack input = itemHandler.getStackInSlot(BOTTLE_SLOT);

        boolean isGlassBottle = input.is(Items.GLASS_BOTTLE);
        boolean isWondrousFlask = input.is(ModItems.WONDROUS_FLASK.get());

        if (!isGlassBottle && !isWondrousFlask) {
            return;
        }

        if(!hasPotionFluid() && tank.getFluidAmount() < 250){
            return;
        }

        ItemStack output;

        if(isGlassBottle) {
            PotionMixture mixture = getPotionMixture();
            output = PotionItemFactory.createPotion(mixture);
        } else {
            output = PotionItemFactory.fillFlask(input, tank.getFluid().copy());
        }

        if(output == null || output.isEmpty()) { return; }

        tank.drain(250, IFluidHandler.FluidAction.EXECUTE);
        itemHandler.extractItem(BOTTLE_SLOT, 1, false);

        ItemStack remainder = itemHandler.insertItem(BOTTLE_SLOT, output, false);

        if(!remainder.isEmpty() && level != null) {
            Block.popResource(level, worldPosition, remainder);
        }
        if (tank.isEmpty()) {
            ingredientsUsed = 0;
        }

        setChanged();
    }

    private boolean canFillFlask(ItemStack flask) {
        FluidStack fluidStack = tank.getFluid();
        return WondrousFlaskItem.canFillFluidType(flask, fluidStack);
    }
    // -----------------------------------------------------------------------------------------------------------------
}