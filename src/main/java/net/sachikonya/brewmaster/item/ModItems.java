package net.sachikonya.brewmaster.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sachikonya.brewmaster.Brewmaster;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.item.custom.SeaBlessedGourdItem;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;
import net.sachikonya.brewmaster.registry.ModDataComponents;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "brewmaster" namespace
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Brewmaster.MODID);

    public static final DeferredItem<Item> WONDROUS_FLASK = ITEMS.register("wondrous_flask",
            () -> new WondrousFlaskItem(new Item.Properties()
                    .stacksTo(1)));

    public static final DeferredItem<Item> SEA_BLESSED_GOURD = ITEMS.register("sea_blessed_gourd",
            () -> new SeaBlessedGourdItem(new Item.Properties()
                    .stacksTo(1)));

    public static final DeferredItem<Item> IRON_AMETHYST = ITEMS.registerSimpleItem("iron_amethyst");
    public static final DeferredItem<Item> GOLD_AMETHYST = ITEMS.registerSimpleItem("gold_amethyst");
    public static final DeferredItem<Item> DIAMOND_AMETHYST = ITEMS.registerSimpleItem("diamond_amethyst");
    public static final DeferredItem<Item> NETHERITE_AMETHYST = ITEMS.registerSimpleItem("netherite_amethyst");

    public static final DeferredItem<Item> MAGIC_RUNE_A = ITEMS.registerSimpleItem("magic_rune_a",
            new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final DeferredItem<Item> MAGIC_RUNE_B = ITEMS.registerSimpleItem("magic_rune_b",
            new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final DeferredItem<Item> MAGIC_RUNE_C = ITEMS.registerSimpleItem("magic_rune_c",
            new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));

    public static final DeferredItem<Item> GOLDEN_SEED = ITEMS.registerSimpleItem("golden_seed",
            new Item.Properties().component(DataComponents.RARITY, Rarity.UNCOMMON));

    public static final DeferredItem<Item> OVERWORLD_UPGRADE_BOOK = ITEMS.registerSimpleItem("overworld_upgrade_book",
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(DataComponents.RARITY, Rarity.UNCOMMON));
    public static final DeferredItem<Item> NETHER_UPGRADE_BOOK = ITEMS.registerSimpleItem("nether_upgrade_book",
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(DataComponents.RARITY, Rarity.RARE));
    public static final DeferredItem<Item> END_UPGRADE_BOOK = ITEMS.registerSimpleItem("end_upgrade_book",
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(DataComponents.RARITY, Rarity.EPIC));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
