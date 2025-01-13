package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleEnvironmentDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtlePlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
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

public class PocketUpgradesProvider {

    public static void generate(DataGenerator.PackGenerator output, CompletableFuture<HolderLookup.Provider> registries) {
        var newRegistries = RegistryPatchGenerator.createLookup(registries, Util.make(new RegistrySetBuilder(), builder -> {
            builder.add(ITurtleUpgrade.REGISTRY, upgrades -> {
                upgrades.register(id(CCRegistration.ID.CHATTY_POCKET), new TurtleChatBoxUpgrade(new ItemStack(Blocks.CHAT_BOX.get())));
                upgrades.register(id(CCRegistration.ID.PLAYER_POCKET), new TurtlePlayerDetectorUpgrade(new ItemStack(Blocks.PLAYER_DETECTOR.get())));
                upgrades.register(id(CCRegistration.ID.ENVIRONMENT_POCKET), new TurtleEnvironmentDetectorUpgrade(new ItemStack(Blocks.ENVIRONMENT_DETECTOR.get())));
                upgrades.register(id(CCRegistration.ID.GEOSCANNER_TURTLE), new TurtleGeoScannerUpgrade(new ItemStack(Blocks.GEO_SCANNER.get())));
            });
        }));
        //TODO - crashes data gen
        //output.addProvider(o -> new DatapackBuiltinEntriesProvider(o, newRegistries, Set.of(AdvancedPeripherals.MOD_ID)));
    }

    public static ResourceKey<ITurtleUpgrade> id(ResourceLocation id) {
        return ITurtleUpgrade.createKey(id);
    }
}
