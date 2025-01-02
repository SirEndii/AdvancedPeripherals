package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.*;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TurtleUpgradesProvider {

    public static void generate(DataGenerator.PackGenerator output, CompletableFuture<HolderLookup.Provider> registries) {
        var newRegistries = RegistryPatchGenerator.createLookup(registries, Util.make(new RegistrySetBuilder(), builder -> {
            builder.add(ITurtleUpgrade.REGISTRY, upgrades -> {
                upgrades.register(id(CCRegistration.ID.CHATTY_TURTLE), new TurtleChatBoxUpgrade(CCRegistration.ID.CHATTY_TURTLE, new ItemStack(Blocks.CHAT_BOX.get())));
                upgrades.register(id(CCRegistration.ID.PLAYER_TURTLE), new TurtlePlayerDetectorUpgrade(CCRegistration.ID.PLAYER_TURTLE, new ItemStack(Blocks.PLAYER_DETECTOR.get())));
                upgrades.register(id(CCRegistration.ID.ENVIRONMENT_TURTLE), new TurtleEnvironmentDetectorUpgrade(CCRegistration.ID.ENVIRONMENT_TURTLE, new ItemStack(Blocks.ENVIRONMENT_DETECTOR.get())));
                upgrades.register(id(CCRegistration.ID.CHUNKY_TURTLE), new TurtleChunkyUpgrade(CCRegistration.ID.CHUNKY_TURTLE, new ItemStack(Items.CHUNK_CONTROLLER)));
                upgrades.register(id(CCRegistration.ID.GEOSCANNER_TURTLE), new TurtleGeoScannerUpgrade(CCRegistration.ID.GEOSCANNER_TURTLE, new ItemStack(Blocks.ENVIRONMENT_DETECTOR.get())));
                upgrades.register(id(CCRegistration.ID.COMPASS_TURTLE), new TurtleCompassUpgrade(CCRegistration.ID.COMPASS_TURTLE, new ItemStack(net.minecraft.world.item.Items.COMPASS)));
                upgrades.register(id(CCRegistration.ID.WEAK_AUTOMATA), new WeakAutomata(CCRegistration.ID.WEAK_AUTOMATA, new ItemStack(Items.WEAK_AUTOMATA_CORE.get())));
                upgrades.register(id(CCRegistration.ID.END_AUTOMATA), new EndAutomata(CCRegistration.ID.END_AUTOMATA, new ItemStack(Items.END_AUTOMATA_CORE.get())));
                upgrades.register(id(CCRegistration.ID.HUSBANDRY_AUTOMATA), new HusbandryAutomata(CCRegistration.ID.HUSBANDRY_AUTOMATA, new ItemStack(Items.HUSBANDRY_AUTOMATA_CORE.get())));
                upgrades.register(id(CCRegistration.ID.OP_WEAK_AUTOMATA), new OverpoweredWeakAutomata(CCRegistration.ID.OP_WEAK_AUTOMATA, new ItemStack(Items.OVERPOWERED_WEAK_AUTOMATA_CORE.get())));
                upgrades.register(id(CCRegistration.ID.OP_END_AUTOMATA), new OverpoweredEndAutomata(CCRegistration.ID.OP_END_AUTOMATA, new ItemStack(Items.OVERPOWERED_END_AUTOMATA_CORE.get())));
                upgrades.register(id(CCRegistration.ID.OP_HUSBANDRY_AUTOMATA), new OverpoweredHusbandryAutomata(CCRegistration.ID.OP_HUSBANDRY_AUTOMATA, new ItemStack(Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get())));
            });
        }));
        output.addProvider(o -> new DatapackBuiltinEntriesProvider(o, newRegistries, Set.of(AdvancedPeripherals.MOD_ID)));
    }

    public static ResourceKey<ITurtleUpgrade> id(ResourceLocation id) {
        return ITurtleUpgrade.createKey(id);
    }

}