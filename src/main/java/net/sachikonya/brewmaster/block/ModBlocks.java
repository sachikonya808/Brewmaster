package net.sachikonya.brewmaster.block;

import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.custom.WitchCauldronBlock;
import net.sachikonya.brewmaster.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "brewmaster" namespace
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Brewmaster.MODID);

    // Creates a new Block with the id "brewmaster:witch_cauldron", combining the namespace and path
    public static final DeferredBlock<Block> WITCH_CAULDRON = registerBlock("witch_cauldron",
            () -> new WitchCauldronBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(2f).requiresCorrectToolForDrops().sound(SoundType.COPPER)));


    // Creates the new BlockItems
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
