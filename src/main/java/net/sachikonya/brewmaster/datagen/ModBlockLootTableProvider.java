package net.sachikonya.brewmaster.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.sachikonya.brewmaster.block.ModBlocks;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        //Register what does everything should drop
        dropSelf(ModBlocks.WITCH_CAULDRON.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        //Gets every registered Block
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
