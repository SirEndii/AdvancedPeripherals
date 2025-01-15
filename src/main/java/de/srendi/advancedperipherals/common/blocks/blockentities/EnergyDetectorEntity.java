package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.util.EnergyStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyDetectorEntity extends PeripheralBlockEntity<EnergyDetectorPeripheral> {

    //a zero size, zero transfer energy storage to ensure that cables connect
    private final EnergyStorage zeroStorage = new EnergyStorage(0, 0, 0);
    public int transferRate = 0;
    //storageProxy that will forward the energy to the output but limit it to maxTransferRate
    public EnergyStorageProxy storageProxy = new EnergyStorageProxy(this, APConfig.PERIPHERALS_CONFIG.energyDetectorMaxFlow.get());
    IEnergyStorage energyStorageCap = storageProxy;
    Direction energyInDirection = Direction.NORTH;
    Direction energyOutDirection = Direction.SOUTH;
    IEnergyStorage zeroStorageCap = zeroStorage;
    private IEnergyStorage outReceivingStorage = null;

    public EnergyDetectorEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.ENERGY_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected EnergyDetectorPeripheral createPeripheral() {
        return new EnergyDetectorPeripheral(this);
    }

    @Nullable
    @Override
    public IEnergyStorage createEnergyStorageCap(@Nullable Direction direction) {
        energyInDirection = getBlockState().getValue(JigsawBlock.ORIENTATION).front();
        energyOutDirection = getBlockState().getValue(JigsawBlock.ORIENTATION).front().getOpposite();

        if (direction == energyInDirection) {
            return energyStorageCap;
        } else if (direction == energyOutDirection) {
            return zeroStorageCap;
        }
        return null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putInt("transferRate", transferRate);
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        transferRate = tag.getInt("transferRate");
        super.loadAdditional(tag, provider);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            // this handles the rare edge case that receiveEnergy is called multiple times in one tick
            transferRate = storageProxy.getTransferredInThisTick();
            storageProxy.resetTransferedInThisTick();
        }
    }

    public void invalidateStorages() {
        outReceivingStorage = null;
    }

    // returns the cached output storage of the receiving block or refetches it if it has been invalidated
    public IEnergyStorage getOutputStorage() {
        // the documentation says that the value of the LazyOptional should be cached locally and invallidated using addListener
        if (outReceivingStorage == null) {
            BlockEntity teOut = level.getBlockEntity(worldPosition.relative(energyOutDirection));
            if (teOut == null) {
                return null;
            }
            outReceivingStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(energyOutDirection), energyOutDirection.getOpposite());
        }
        return outReceivingStorage;
    }
}
