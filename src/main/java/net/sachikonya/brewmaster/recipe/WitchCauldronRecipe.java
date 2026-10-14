package net.sachikonya.brewmaster.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record WitchCauldronRecipe(Ingredient inputItem, ItemStack output) implements Recipe<WitchCauldronRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);

        return list;
    }

    @Override
    public boolean matches(WitchCauldronRecipeInput witchCauldronRecipeInput, Level level) {
        if (level.isClientSide()){
            return false;
        }

        return inputItem.test(witchCauldronRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(WitchCauldronRecipeInput witchCauldronRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WITCH_CAULDRON_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.WITCH_CAULDRON_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<WitchCauldronRecipe> {
        public static final MapCodec<WitchCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
               Ingredient.CODEC_NONEMPTY
                       .fieldOf("ingredient")
                       .forGetter(WitchCauldronRecipe::inputItem),
               ItemStack.CODEC
                       .fieldOf("result")
                       .forGetter(WitchCauldronRecipe::output)
        ).apply(inst, WitchCauldronRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WitchCauldronRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, WitchCauldronRecipe::inputItem,
                        ItemStack.STREAM_CODEC, WitchCauldronRecipe::output,
                        WitchCauldronRecipe::new
                );

        @Override
        public MapCodec<WitchCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WitchCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
