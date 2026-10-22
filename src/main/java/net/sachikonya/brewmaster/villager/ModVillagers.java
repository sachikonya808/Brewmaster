package net.sachikonya.brewmaster.villager;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.ModBlocks;

public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Brewmaster.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, Brewmaster.MODID);

    public static final Holder<PoiType> ALLIED_WITCH_POI =
            POI_TYPES.register("allied_witch_poi", () -> new PoiType(
                    ImmutableSet.copyOf(ModBlocks.WITCH_CAULDRON.get().getStateDefinition().getPossibleStates())
            ,1 , 1));

    public static final Holder<VillagerProfession> ALLIED_WITCH =
            VILLAGER_PROFESSIONS.register("allied_witch", () -> new VillagerProfession(
                    "allied_witch", holder -> holder.value() == ALLIED_WITCH_POI.value(),
                    poiTypeHolder -> poiTypeHolder.value() == ALLIED_WITCH_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.BREWING_STAND_BREW
            ));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
