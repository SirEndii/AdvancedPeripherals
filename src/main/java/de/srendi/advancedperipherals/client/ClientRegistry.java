package de.srendi.advancedperipherals.client;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.InventoryManagerScreen;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.ContainerTypes;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    private static final String[] TURTLE_MODELS = new String[]{"turtle_chat_box_upgrade_left", "turtle_chat_box_upgrade_right", "turtle_environment_upgrade_left", "turtle_environment_upgrade_right", "turtle_player_upgrade_left", "turtle_player_upgrade_right", "turtle_geoscanner_upgrade_left", "turtle_geoscanner_upgrade_right"};

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (String model : TURTLE_MODELS) {
            event.register(new ModelResourceLocation(AdvancedPeripherals.getRL(model), "standalone"));
        }
    }

    @SubscribeEvent
    public static void menuRegister(RegisterMenuScreensEvent event) {
        event.register(ContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), InventoryManagerScreen::new);
    }

    @SubscribeEvent
    public static void onUpgradeModeller(RegisterTurtleModellersEvent event) {
        event.register(CCRegistration.CHUNKY_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        event.register(CCRegistration.COMPASS_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        event.register(CCRegistration.CHAT_BOX_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_chatty_left"), AdvancedPeripherals.getRL("block/turtle_chatty_right")));
        event.register(CCRegistration.ENVIRONMENT_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_environment_left"), AdvancedPeripherals.getRL("block/turtle_environment_right")));
        event.register(CCRegistration.GEO_SCANNER_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_geoscanner_left"), AdvancedPeripherals.getRL("block/turtle_geoscanner_right")));
        event.register(CCRegistration.PLAYER_DETECTOR_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_player_left"), AdvancedPeripherals.getRL("block/turtle_player_right")));
        event.register(CCRegistration.OP_END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.OP_HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.OP_WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }
}
