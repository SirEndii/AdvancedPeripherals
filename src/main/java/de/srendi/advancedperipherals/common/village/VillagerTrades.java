package de.srendi.advancedperipherals.common.village;

import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.setup.Villagers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import static de.srendi.advancedperipherals.common.village.VillagerTrade.TradeBuilder;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class VillagerTrades {

    @SubscribeEvent
    public static void registerWanderingTrade(WandererTradesEvent event) {
        if (APConfig.WORLD_CONFIG.enableWanderingTraderTrades.get()) {
            TradeBuilder.createTrade(event, Blocks.PERIPHERAL_CASING.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 1, 1)
                    .setMaxUses(8)
                    .build();
            TradeBuilder.createTrade(event, ModRegistry.Blocks.TURTLE_ADVANCED.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 2, 1)
                    .setMaxUses(8)
                    .build();
        }
    }

    @SubscribeEvent
    public static void registerVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == Villagers.COMPUTER_SCIENTIST.get()) {

            TradeBuilder.createTrade(event, Blocks.PERIPHERAL_CASING.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 1, 1)
                    .setXp(1)
                    .build();
            TradeBuilder.createTrade(event, Blocks.CHAT_BOX.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 2, 1)
                    .build();
            TradeBuilder.createTrade(event, ModRegistry.Blocks.MONITOR_NORMAL.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 2, 1)
                    .setItemAmount(2)
                    .build();
            TradeBuilder.createTrade(event, Blocks.PERIPHERAL_CASING.get(), VillagerTrade.Type.EMERALD_FOR_ITEM, 1, 1)
                    .setMaxUses(5)
                    .build();

            TradeBuilder.createTrade(event, Blocks.INVENTORY_MANAGER.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 2, 2)
                    .setXp(4)
                    .build();
            TradeBuilder.createTrade(event, Blocks.REDSTONE_INTEGRATOR.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 2, 2)
                    .setXp(3)
                    .build();
            TradeBuilder.createTrade(event, ModRegistry.Blocks.COMPUTER_ADVANCED.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 3, 2)
                    .setXp(4)
                    .build();
            TradeBuilder.createTrade(event, VillagerTrade.Type.ITEM_FOR_EMERALD, 4, 3)
                    .withTurtleUpgrade(false, CCRegistration.ID.ENVIRONMENT_TURTLE)
                    .setMaxUses(4)
                    .setXp(5)
                    .build();
            TradeBuilder.createTrade(event, VillagerTrade.Type.ITEM_FOR_EMERALD, 4, 3)
                    .withPocketUpgrade(true, CCRegistration.ID.PLAYER_POCKET)
                    .setMaxUses(4)
                    .setXp(7)
                    .build();

            TradeBuilder.createTrade(event, Items.CHUNK_CONTROLLER.get(), VillagerTrade.Type.EMERALD_FOR_ITEM, 5, 3)
                    .setMaxUses(6)
                    .setXp(8)
                    .build();
            TradeBuilder.createTrade(event, Items.COMPUTER_TOOL.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 1, 3)
                    .setMaxUses(1)
                    .setXp(16)
                    .build();
            TradeBuilder.createTrade(event, ModRegistry.Blocks.MONITOR_ADVANCED.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 6, 3)
                    .setXp(7)
                    .setItemAmount(4)
                    .build();

            TradeBuilder.createTrade(event, Blocks.ENERGY_DETECTOR.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 4, 4)
                    .setXp(6)
                    .build();
            TradeBuilder.createTrade(event, ModRegistry.Blocks.COMPUTER_ADVANCED.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 4, 4)
                    .setXp(5)
                    .setMaxUses(3)
                    .build();
            TradeBuilder.createTrade(event, Items.MEMORY_CARD.get(), VillagerTrade.Type.EMERALD_FOR_ITEM, 2, 4)
                    .setXp(4)
                    .build();
            TradeBuilder.createTrade(event, VillagerTrade.Type.ITEM_FOR_EMERALD, 8, 4)
                    .withTurtleUpgrade(true, CCRegistration.ID.CHUNKY_TURTLE)
                    .setXp(7)
                    .build();
            TradeBuilder.createTrade(event, Items.CHUNK_CONTROLLER.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 6, 4)
                    .setMaxUses(6)
                    .setXp(6)
                    .build();

            TradeBuilder.createTrade(event, Blocks.ME_BRIDGE.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 4, 5)
                    .setItemAmount(2)
                    .setXp(4)
                    .build();
            TradeBuilder.createTrade(event, ModRegistry.Blocks.SPEAKER.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 3, 5)
                    .setXp(6)
                    .build();
            TradeBuilder.createTrade(event, ModRegistry.Blocks.WIRELESS_MODEM_ADVANCED.get(), VillagerTrade.Type.ITEM_FOR_EMERALD, 4, 5)
                    .setXp(8)
                    .build();

        }
    }

}
