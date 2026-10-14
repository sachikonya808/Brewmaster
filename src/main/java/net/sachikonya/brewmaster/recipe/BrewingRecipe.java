package net.sachikonya.brewmaster.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sachikonya.brewmaster.potion.PotionEffectData;

import java.util.List;

public record BrewingRecipe(
        Ingredient inputItem, List<PotionEffectData> effectDataList, double durationMultiplierAddition)
implements Recipe<BrewingRecipeInput> {
    @Override
    public boolean matches(BrewingRecipeInput brewingRecipeInput, Level level) {
        if (level.isClientSide()){
            return false;
        }

        if (brewingRecipeInput.size() != 1){
            return false;
        }

        return inputItem.test(brewingRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(BrewingRecipeInput brewingRecipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BREWING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.BREWING_TYPE.get();
    }

    //Serializer

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {
        public static final MapCodec<BrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY
                        .fieldOf("ingredient")
                        .forGetter(BrewingRecipe::inputItem),
                PotionEffectData.CODEC
                        .listOf()
                        .optionalFieldOf("effects",List.of())
                        .forGetter(BrewingRecipe::effectDataList),
                Codec.DOUBLE
                        .optionalFieldOf("duration_multiplier_addition",0.0)
                        .forGetter(BrewingRecipe::durationMultiplierAddition)
        ).apply(inst, BrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, BrewingRecipe::inputItem,
                        PotionEffectData.STREAM_CODEC.apply(ByteBufCodecs.list()),
                        BrewingRecipe::effectDataList,
                        ByteBufCodecs.DOUBLE,
                        BrewingRecipe::durationMultiplierAddition,
                        BrewingRecipe::new
                );


        @Override
        public MapCodec<BrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
