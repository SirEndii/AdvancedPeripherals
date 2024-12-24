package de.srendi.advancedperipherals.common.addons.valkyrienskies;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;

public class Integration implements Runnable {

    @Override
    public void run() {
        EnvironmentDetectorPeripheral.addIntegrationPlugin(ShipScannerPlugin::new);
    }
}
