package net.sachikonya.brewmaster.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.item.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Brewmaster.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.CAULDRON_UPGRADES)
                .add(ModItems.OVERWORLD_UPGRADE_BOOK.get())
                .add(ModItems.NETHER_UPGRADE_BOOK.get())
                .add(ModItems.END_UPGRADE_BOOK.get());
    }
}
