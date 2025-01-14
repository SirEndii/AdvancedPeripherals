package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public class RSBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> {

    public static final String PERIPHERAL_TYPE = "rsBridge";

    public RSBridgePeripheral(RsBridgeEntity owner) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(owner));
    }

    @Override
    public boolean isEnabled() {
        return APAddons.refinedStorageLoaded && APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }
}
