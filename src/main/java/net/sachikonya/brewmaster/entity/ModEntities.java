package net.sachikonya.brewmaster.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.entity.custom.AnvilEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Brewmaster.MODID);

    public static final Supplier<EntityType<AnvilEntity>> FALLING_ANVIL =
            ENTITY_TYPES.register("falling_anvil", () -> EntityType.Builder.of(AnvilEntity::new, MobCategory.MISC)
                    .sized(0.98f,0.98f)
                    .noSummon()
                    .build("falling_anvil"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
