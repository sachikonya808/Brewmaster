package net.sachikonya.brewmaster.datagen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.loot.AddItemModifier;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Brewmaster.MODID);
    }

    @Override
    protected void start() {
        //Golden Seeds
        add("golden_seed_from_witch", new AddItemModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/witch")).build(),
                LootItemKilledByPlayerCondition.killedByPlayer().build(),
                LootItemRandomChanceCondition.randomChance(0.05f).build()
        }, new ItemStack(ModItems.GOLDEN_SEED.get())));
        add("golden_seed_from_grass", new AddItemModifier(new LootItemCondition[]{
                AnyOfCondition.anyOf(
                    LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/short_grass")),
                    LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/tall_grass")),
                    LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/fern")),
                    LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/large_fern"))
                ).build(),
                MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)).invert().build(),
                LootItemRandomChanceCondition.randomChance(0.01f).build()
        }, new ItemStack(ModItems.GOLDEN_SEED.get())));

        //Overworld Book
        add("overworld_book_from_library", new AddItemModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                LootItemRandomChanceCondition.randomChance(1f).build()
        }, new ItemStack(ModItems.OVERWORLD_UPGRADE_BOOK.get())));

        //Nether Book
        add("nether_book_from_bridge", new AddItemModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_bridge")).build(),
                LootItemRandomChanceCondition.randomChance(0.1f).build()
        }, new ItemStack(ModItems.NETHER_UPGRADE_BOOK.get())));
        add("nether_book_from_hoglin_stable", new AddItemModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_hoglin_stable")).build(),
                LootItemRandomChanceCondition.randomChance(0.1f).build()
        }, new ItemStack(ModItems.NETHER_UPGRADE_BOOK.get())));
        add("nether_book_from_other", new AddItemModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_other")).build(),
                LootItemRandomChanceCondition.randomChance(0.1f).build()
        }, new ItemStack(ModItems.NETHER_UPGRADE_BOOK.get())));
        add("nether_book_from_treasure", new AddItemModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/bastion_treasure")).build(),
                LootItemRandomChanceCondition.randomChance(1f).build()
        }, new ItemStack(ModItems.NETHER_UPGRADE_BOOK.get())));

        //End Book
        add("end_book_from_end_city_treasure", new AddItemModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                LootItemRandomChanceCondition.randomChance(0.67f).build()
        }, new ItemStack(ModItems.END_UPGRADE_BOOK.get())));

        //Magic Runes - TEMP: While I think how tf to add custom drops to the suspicious gravel/sand
        add("magic_rune_a_from_structures", new AddItemModifier(new LootItemCondition[]{
                AnyOfCondition.anyOf(
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/desert_pyramid")),
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/underwater_ruin_big")),
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/underwater_ruin_small"))
                ).build(),
                LootItemRandomChanceCondition.randomChance(1f/3f).build()
        }, new ItemStack(ModItems.MAGIC_RUNE_A.get())));
        add("magic_rune_b_from_structures", new AddItemModifier(new LootItemCondition[]{
                AnyOfCondition.anyOf(
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/desert_pyramid")),
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/underwater_ruin_big")),
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/underwater_ruin_small"))
                ).build(),
                LootItemRandomChanceCondition.randomChance(1f/3f).build()
        }, new ItemStack(ModItems.MAGIC_RUNE_B.get())));
        add("magic_rune_c_from_structures", new AddItemModifier(new LootItemCondition[]{
                AnyOfCondition.anyOf(
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/desert_pyramid")),
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/underwater_ruin_big")),
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/underwater_ruin_small"))
                ).build(),
                LootItemRandomChanceCondition.randomChance(1f/3f).build()
        }, new ItemStack(ModItems.MAGIC_RUNE_C.get())));
    }
}
