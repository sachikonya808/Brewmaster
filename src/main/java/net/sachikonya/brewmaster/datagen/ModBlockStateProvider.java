package net.sachikonya.brewmaster.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.ModBlocks;
import net.sachikonya.brewmaster.block.custom.WitchCauldronBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Brewmaster.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //Simple Block Item Example:
        //blockWithItem(ModBlocks.WITCH_CAULDRON);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }


    /* Simple Block Item WITH Custom Block States Example
    private void witchCauldron() {
        getVariantBuilder(ModBlocks.WITCH_CAULDRON.get()).forAllStates(state -> {
            if(state.getValue(WitchCauldronBlock.HAS_OR_IS_BREWING)) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("witch_cauldron",                 //Must be
                        ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "block/" + "witch_cauldron")))};    //the same
            } else {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("witch_cauldron",
                        ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "block/" + "witch_cauldron")))};
            }
        });

        simpleBlockItem(ModBlocks.WITCH_CAULDRON.get(), models().cubeAll("witch_cauldron",
                ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "block/" + "witch_cauldron")));
    }
     */
}
