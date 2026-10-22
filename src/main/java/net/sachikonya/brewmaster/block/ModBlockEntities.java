package net.sachikonya.brewmaster.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Brewmaster.MODID);

    public static final Supplier<BlockEntityType<WitchCauldronBlockEntity>> WITCH_CAULDRON_BE =
            BLOCK_ENTITIES.register("witch_cauldron_be", () -> BlockEntityType.Builder.of(
                    WitchCauldronBlockEntity::new, ModBlocks.WITCH_CAULDRON.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
