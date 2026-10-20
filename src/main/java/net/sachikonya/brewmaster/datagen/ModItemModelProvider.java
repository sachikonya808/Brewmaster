package net.sachikonya.brewmaster.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Brewmaster.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //Add items here
        basicItem(ModItems.SEA_BLESSED_GOURD.get());

        basicItem(ModItems.IRON_AMETHYST.get());
        basicItem(ModItems.GOLD_AMETHYST.get());
        basicItem(ModItems.DIAMOND_AMETHYST.get());
        basicItem(ModItems.NETHERITE_AMETHYST.get());

        basicItem(ModItems.MAGIC_RUNE_A.get());
        basicItem(ModItems.MAGIC_RUNE_B.get());
        basicItem(ModItems.MAGIC_RUNE_C.get());
        basicItem(ModItems.GOLDEN_SEED.get());
    }
}
