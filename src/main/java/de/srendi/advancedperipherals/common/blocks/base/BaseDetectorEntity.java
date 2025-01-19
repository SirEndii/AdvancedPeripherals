package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.util.proxy.IStorageProxy;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseDetectorEntity<T, S extends IStorageProxy, P extends BasePeripheral<?>> extends PeripheralBlockEntity<P> {

    private static final String RATE_LIMIT_TAG = "RateLimit";

    private final Capability<T> capability;
    // proxy that will forward X to the output but limit it to maxTransferRate
    private final S proxy = createProxy();
    private volatile long transferRate = 0;
    private LazyOptional<S> inputStorageCap = LazyOptional.empty();
    private LazyOptional<T> zeroStorageCap = LazyOptional.empty();

    protected BaseDetectorEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, Capability<T> capability) {
        super(tileEntityType, pos, state);
        this.capability = capability;
    }

    @NotNull
    protected abstract S createProxy();

    @NotNull
    protected abstract T getZeroStorage();

    @NotNull
    protected S getStorageProxy() {
        return this.proxy;
    }

    public long getTransferRate() {
        return this.transferRate;
    }

    public long getMaxTransferRate() {
        return this.proxy.getMaxTransferRate();
    }

    public long getTransferRateLimit() {
        return this.proxy.getTransferRate();
    }

    public void setTransferRateLimit(long rate) {
        if (this.proxy.getTransferRate() != rate) {
            this.proxy.setTransferRate(rate);
            this.setChanged();
        }
    }

    @Nullable
    public String getLastTransferedId() {
        return this.proxy.getLastTransferedId();
    }

    @Nullable
    public String getReadyTransferId() {
        return this.proxy.getReadyTransferId();
    }

    public Direction getInputDirection() {
        return this.getBlockState().getValue(BaseBlock.ORIENTATION).front();
    }

    public Direction getOutputDirection() {
        return this.getBlockState().getValue(BaseBlock.ORIENTATION).front().getOpposite();
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction direction) {
        Direction inputDirection = this.getInputDirection();
        Direction outputDirection = this.getOutputDirection();
        if (cap == this.capability) {
            if (direction == inputDirection) {
                if (!this.inputStorageCap.isPresent()) {
                    this.inputStorageCap = LazyOptional.of(this::getStorageProxy);
                }
                return this.inputStorageCap.cast();
            } else if (direction == outputDirection) {
                if (!this.zeroStorageCap.isPresent()) {
                    this.zeroStorageCap = LazyOptional.of(this::getZeroStorage);
                }
                return this.zeroStorageCap.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putLong(RATE_LIMIT_TAG, this.getTransferRateLimit());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        this.proxy.setTransferRate(nbt.getLong(RATE_LIMIT_TAG));
        super.load(nbt);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            this.transferRate = this.proxy.getAndResetTransfered();
        }
    }

    @NotNull
    public LazyOptional<? extends T> getOutputStorage() {
        Direction outputDirection = this.getOutputDirection();
        BlockEntity be = level.getBlockEntity(worldPosition.relative(outputDirection));
        return be == null ? LazyOptional.empty() : be.getCapability(this.capability, outputDirection.getOpposite());
    }
}
