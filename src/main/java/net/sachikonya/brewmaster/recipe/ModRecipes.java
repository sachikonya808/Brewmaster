package net.sachikonya.brewmaster.recipe;

import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Brewmaster.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Brewmaster.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WitchCauldronRecipe>> WITCH_CAULDRON_SERIALIZER =
            SERIALIZERS.register("witch_cauldron", WitchCauldronRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<WitchCauldronRecipe>> WITCH_CAULDRON_TYPE =
            TYPES.register("witch_cauldron", () -> new RecipeType<WitchCauldronRecipe>() {
                @Override
                public String toString() {
                    return "witch_cauldron";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BrewingRecipe>> BREWING_SERIALIZER =
            SERIALIZERS.register("witch_cauldron_brewing", BrewingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BrewingRecipe>> BREWING_TYPE =
            TYPES.register("witch_cauldron_brewing", () -> new RecipeType<BrewingRecipe>() {
                @Override
                public String toString() {
                    return "witch_cauldron_brewing";
                }
            });

    public static void register (IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
