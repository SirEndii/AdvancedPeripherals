package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketColonyIntegratorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironmentUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketPlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PocketUpgradesProvider {

    public static void addUpgrades(BootstrapContext<IPocketUpgrade> upgrades) {
        upgrades.register(id(CCRegistration.ID.CHATTY_POCKET), new PocketChatBoxUpgrade(new ItemStack(Blocks.CHAT_BOX.get())));
        upgrades.register(id(CCRegistration.ID.PLAYER_POCKET), new PocketPlayerDetectorUpgrade(new ItemStack(Blocks.PLAYER_DETECTOR.get())));
        upgrades.register(id(CCRegistration.ID.ENVIRONMENT_POCKET), new PocketEnvironmentUpgrade(new ItemStack(Blocks.ENVIRONMENT_DETECTOR.get())));
        upgrades.register(id(CCRegistration.ID.GEOSCANNER_POCKET), new PocketGeoScannerUpgrade(new ItemStack(Blocks.GEO_SCANNER.get())));
        upgrades.register(id(CCRegistration.ID.COLONY_POCKET), new PocketColonyIntegratorUpgrade(new ItemStack(Blocks.COLONY_INTEGRATOR.get())));
    }

    public static ResourceKey<IPocketUpgrade> id(ResourceLocation id) {
        return ResourceKey.create(IPocketUpgrade.REGISTRY, id);
    }
}
