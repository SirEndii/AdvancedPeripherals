package de.srendi.advancedperipherals.common.blocks.blockentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.orientation.BlockOrientation;
import appeng.api.util.AECableType;
import appeng.me.helpers.IGridConnectedBlockEntity;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeBridgeEntityListener;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeBridgeEntity extends PeripheralBlockEntity<MeBridgePeripheral> implements IActionSource, IActionHost, IInWorldGridNodeHost, IGridConnectedBlockEntity, ICraftingSimulationRequester {

    private final List<CraftJob> jobs = new CopyOnWriteArrayList<>();
    private boolean initialized = false;
    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, MeBridgeEntityListener.INSTANCE);

    public MeBridgeEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.ME_BRIDGE.get(), pos, state);
        getMainNode().setExposedOnSides(getGridConnectableSides(null));
    }

    @NotNull
    @Override
    protected MeBridgePeripheral createPeripheral() {
        return new MeBridgePeripheral(this);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (!this.level.isClientSide) {
            if (!initialized) {
                mainNode.setFlags(GridFlags.REQUIRE_CHANNEL);
                mainNode.setIdlePowerUsage(APConfig.PERIPHERALS_CONFIG.meConsumption.get());
                mainNode.setVisualRepresentation(new ItemStack(Blocks.ME_BRIDGE.get()));
                mainNode.setInWorldNode(true);
                mainNode.create(level, getBlockPos());
                //peripheral can be null if `createPeripheralCap` was not called before
                if (peripheral == null)
                    peripheral = createPeripheral();
                peripheral.setNode(mainNode);
                initialized = true;
            }

            // Try to start the job if the job calculation finished
            jobs.forEach(CraftJob::maybeCraft);

            // Remove the job if the crafting started, we can't do anything with it anymore
            jobs.removeIf(CraftJob::isCraftingStarted);
        }
    }

    @NotNull
    @Override
    public Optional<Player> player() {
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<IActionHost> machine() {
        return Optional.of(this);
    }

    @NotNull
    @Override
    public <T> Optional<T> context(@NotNull Class<T> key) {
        return Optional.empty();
    }

    @Nullable
    @Override
    public IGridNode getActionableNode() {
        return mainNode.getNode();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        mainNode.destroy();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        mainNode.destroy();
    }

    @Override
    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return IGridConnectedBlockEntity.super.getGridConnectableSides(orientation);
    }

    @Nullable
    @Override
    public IGridNode getGridNode() {
        return IGridConnectedBlockEntity.super.getGridNode();
    }

    @Nullable
    @Override
    public IGridNode getGridNode(@NotNull Direction dir) {
        return getActionableNode();
    }

    @Override
    public void saveChanges() {

    }

    @NotNull
    @Override
    public AECableType getCableConnectionType(@NotNull Direction dir) {
        return AECableType.SMART;
    }

    /**
     * Return the current action source, used to extract items.
     */
    @Nullable
    @Override
    public IActionSource getActionSource() {
        return this;
    }

    public void addJob(CraftJob job) {
        jobs.add(job);
    }
}
