package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    private DataGenerators() {
    }

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        generator.addProvider(event.includeServer(), new BlockTagsProvider(packOutput, completablefuture, existingFileHelper, Registration.BLOCKS));
        generator.addProvider(event.includeServer(), new RecipesProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new BlockLootTablesProvider(packOutput));
        TurtleUpgradesProvider.generate(generator.getVanillaPack(true), completablefuture);
        PocketUpgradesProvider.generate(generator.getVanillaPack(true), completablefuture);
        generator.addProvider(event.includeServer(), new PoiTypeProvider(packOutput, completablefuture, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockStatesAndModelsProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(packOutput));
    }

}
