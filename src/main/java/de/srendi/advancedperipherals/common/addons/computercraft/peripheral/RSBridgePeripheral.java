package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.NetworkComponent;
import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import org.jetbrains.annotations.NotNull;

public class RSBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> implements IStorageSystemPeripheral {

    public static final String PERIPHERAL_TYPE = "rsBridge";

    public RSBridgePeripheral(RsBridgeEntity owner) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(owner));
    }

    @Override
    @LuaFunction(mainThread = true)
    public boolean isEnabled() {
        return APAddons.refinedStorageLoaded && APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }

    private NetworkNode getNode() {
        return owner.tileEntity.getNode();
    }

    private Network getNetwork() {
        return getNode().getNetwork();
    }

    private MethodResult notConnected() {
        return MethodResult.of(null, "NOT_CONNECTED");
    }

    private <I extends NetworkComponent> I getComponent(@NotNull Class<I> componentClass) {
        return getNetwork().getComponent(componentClass);
    }

    private boolean isAvailable() {
        return getNetwork() != null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isConnected() {
        return MethodResult.of(isAvailable());
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isOnline() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(getComponent(EnergyNetworkComponent.class).getStored() > 0);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getItem(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getFluid(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult listItems() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult listFluids() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult listCraftableItems() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult listCraftableFluids() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult listCells() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult listDrives() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult exportItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getFilteredPatterns(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getPatterns() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getStoredEnergy() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getEnergyCapacity() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getEnergyUsage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvgPowerInjection() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternItemStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternFluidStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternChemicalStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalItemStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalFluidStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalChemicalStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternItemStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternFluidStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternChemicalStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedItemStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedFluidStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedChemicalStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternItemStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternFluidStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternChemicalStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableItemStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableFluidStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableChemicalStorage() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getCraftingTasks() {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult cancelCraftingTasks(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isItemCrafting(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isFluidCraftable(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isFluidCrafting(IArguments arguments) throws LuaException {
        return null;
    }
}
