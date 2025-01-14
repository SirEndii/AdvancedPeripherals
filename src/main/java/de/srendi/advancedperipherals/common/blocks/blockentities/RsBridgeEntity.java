package de.srendi.advancedperipherals.common.blocks.blockentities;

import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RSBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RsBridgeEntity extends PeripheralBlockEntity<RSBridgePeripheral> implements IPeripheralTileEntity, NetworkNodeContainerProvider {

    protected CompoundTag peripheralSettings;

    public RsBridgeEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.RS_BRIDGE.get(), pos, state);
        peripheralSettings = new CompoundTag();
    }

    @NotNull
    @Override
    protected RSBridgePeripheral createPeripheral() {
        return new RSBridgePeripheral(this);
    }

    @Override
    public CompoundTag getPeripheralSettings() {
        return peripheralSettings;
    }

    @Override
    public void markSettingsChanged() {
        setChanged();
    }

    @NotNull
    @Override
    public Set<InWorldNetworkNodeContainer> getContainers() {
        return Set.of();
    }

    @Override
    public void addContainer(@NotNull InWorldNetworkNodeContainer inWorldNetworkNodeContainer) {
    }

    @Override
    public boolean canBuild(ServerPlayer serverPlayer) {
        return true;
    }
}
