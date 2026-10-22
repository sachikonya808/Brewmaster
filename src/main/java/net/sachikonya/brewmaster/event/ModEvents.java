package net.sachikonya.brewmaster.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.item.ModItems;
import net.sachikonya.brewmaster.villager.ModVillagers;
import net.sachikonya.brewmaster.villager.RandomBrewingRecipeListing;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = Brewmaster.MODID)
public class ModEvents {
    /*
    @SubscribeEvent
    public void onIncomingDamage(LivingDamageEvent.Pre event) {
        MobEffectInstance effectInstance = event.getEntity().getEffect(ModEffects.BRITTLE);
        if(effectInstance == null) { return; }

        float multiplier = 1.0f + (0.2f * (effectInstance.getAmplifier() + 1));
        event.setNewDamage(event.getNewDamage() * multiplier);
    }
    */
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if(event.getType() == ModVillagers.ALLIED_WITCH.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(Items.NETHER_WART, 2), 12, 1, 0.05f
            ));
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.GLASS_BOTTLE, 9),
                    new ItemStack(Items.EMERALD, 1), 12, 30, 0.05f
            ));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(Items.AMETHYST_SHARD, 3), 12, 1, 0.05f
            ));
            trades.get(2).add(new RandomBrewingRecipeListing(2, 12, 15));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 4),
                    new ItemStack(Items.BLAZE_POWDER, 1), 12, 1, 0.05f
            ));
            trades.get(3).add(new RandomBrewingRecipeListing(2, 12, 15));

            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(ModItems.WONDROUS_FLASK.get(), 1), 12, 15, 0.05f
            ));
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 45),
                    new ItemStack(ModItems.GOLDEN_SEED.get(), 1), 12, 30, 0.05f
            ));

            trades.get(5).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 64),
                    Optional.of(new ItemCost(Items.BOOK, 1)),
                    new ItemStack(ModItems.OVERWORLD_UPGRADE_BOOK.get(), 1), 12, 30, 0.05f
            ));
            trades.get(5).add(new RandomBrewingRecipeListing(2, 12, 15));
        }
    }
}
