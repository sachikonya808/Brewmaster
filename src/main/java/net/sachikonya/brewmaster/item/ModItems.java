package net.sachikonya.brewmaster.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sachikonya.brewmaster.Brewmaster;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.item.custom.WondrousFlaskItem;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "brewmaster" namespace
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Brewmaster.MODID);

    /*
    public static final DeferredHolder<Item, Item> WONDROUS_FLASK =
            ITEMS.registerItem("wondrous_flask", WondrousFlaskItem::new, new Item.Properties().stacksTo(1));


     */

    public static final DeferredItem<Item> WONDROUS_FLASK = ITEMS.register("wondrous_flask",
            () -> new WondrousFlaskItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
