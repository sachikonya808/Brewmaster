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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.sachikonya.brewmaster.item.FlaskTier;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;

public class FlaskUpgradeRecipe implements SmithingRecipe {
    private final Ingredient template;
    private final Ingredient addition;
    private final int targetTier;

    //JEI
    public Ingredient getTemplate() { return template; }
    public Ingredient getAddition() { return addition; }
    public int getTargetTier() { return targetTier; }

    public FlaskUpgradeRecipe(Ingredient template, Ingredient addition, int targetTier) {
        this.template = template;
        this.addition = addition;
        this.targetTier = targetTier;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack itemStack) { return template.test(itemStack); }

    @Override
    public boolean isBaseIngredient(ItemStack itemStack) { return itemStack.is(ModItems.WONDROUS_FLASK.get()); }

    @Override
    public boolean isAdditionIngredient(ItemStack itemStack) { return addition.test(itemStack); }

    @Override
    public boolean matches(SmithingRecipeInput smithingRecipeInput, Level level) {
        ItemStack base = smithingRecipeInput.base();

        return template.test(smithingRecipeInput.template())
                && addition.test(smithingRecipeInput.addition())
                && base.is(ModItems.WONDROUS_FLASK.get())
                && WondrousFlaskItem.getTier(base).ordinal() == targetTier - 1;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput smithingRecipeInput, HolderLookup.Provider provider) {
        return WondrousFlaskItem.withTier(smithingRecipeInput.base(), FlaskTier.byIndex(targetTier));
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return WondrousFlaskItem.withTier(new ItemStack(ModItems.WONDROUS_FLASK.get()), FlaskTier.byIndex(targetTier));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FLASK_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<FlaskUpgradeRecipe> {
        private static final MapCodec<FlaskUpgradeRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC
                                .fieldOf("template")
                                .forGetter(r -> r.template),

                        Ingredient.CODEC
                                .fieldOf("addition")
                                .forGetter(r -> r.addition),

                        Codec.intRange(1, FlaskTier.values().length - 1)
                                .fieldOf("tier")
                                .forGetter(r -> r.targetTier)
                ).apply(instance, FlaskUpgradeRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FlaskUpgradeRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC,
                        r -> r.template,

                        Ingredient.CONTENTS_STREAM_CODEC,
                        r -> r.addition,

                        ByteBufCodecs.VAR_INT,
                        r -> r.targetTier,

                        FlaskUpgradeRecipe::new
                );

        @Override
        public MapCodec<FlaskUpgradeRecipe> codec() {
            return CODEC;
        }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlaskUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
