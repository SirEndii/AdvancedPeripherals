package de.srendi.advancedperipherals.common.blocks.blockentities;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionStrategy;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.common.support.network.InWorldNetworkNodeContainerImpl;
import com.refinedmods.refinedstorage.common.support.network.SimpleConnectionStrategy;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RSBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class RsBridgeEntity extends PeripheralBlockEntity<RSBridgePeripheral> implements IPeripheralTileEntity, NetworkNodeContainerProvider {

    protected CompoundTag peripheralSettings;
    private final InWorldNetworkNodeContainer networkNodeContainer;

    public RsBridgeEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.RS_BRIDGE.get(), pos, state);
        peripheralSettings = new CompoundTag();
        ConnectionStrategy connectionStrategy = new SimpleConnectionStrategy(pos);
        NetworkNode node = new SimpleNetworkNode(APConfig.PERIPHERALS_CONFIG.rsConsumption.get());
        networkNodeContainer = new InWorldNetworkNodeContainerImpl(this, node, "RS Bridge", 1, connectionStrategy, null);
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
        return Set.of(networkNodeContainer);
    }

    @Override
    public void addContainer(@NotNull InWorldNetworkNodeContainer inWorldNetworkNodeContainer) {
    }

    @Override
    public boolean canBuild(@NotNull ServerPlayer serverPlayer) {
        return true;
    }

    @Override
    public void update(@Nullable Level level) {
        NetworkNodeContainerProvider.super.update(level);
    }

    @Override
    public void initialize(@Nullable Level level, @Nullable Runnable callback) {
        NetworkNodeContainerProvider.super.initialize(level, callback);
    }

    @Override
    public void remove(@Nullable Level level) {
        NetworkNodeContainerProvider.super.remove(level);
    }
}
