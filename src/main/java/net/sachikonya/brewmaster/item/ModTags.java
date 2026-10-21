package net.sachikonya.brewmaster.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.sachikonya.brewmaster.Brewmaster;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> CAULDRON_UPGRADES =
                TagKey.create(
                        Registries.ITEM,
                        ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "cauldron_upgrade_books")
                );
    }
}
