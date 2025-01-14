package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class RefinedStorageApi {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                BlockEntityTypes.RS_BRIDGE.get(),
                (blockEntity, side) -> blockEntity);
    }


}
