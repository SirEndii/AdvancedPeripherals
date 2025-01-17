package de.srendi.advancedperipherals.common.blocks.base;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public abstract class PeripheralBlockEntity<T extends BasePeripheral<?>> extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider, IPeripheralTileEntity, ICapabilityProvider {
    private static final String PERIPHERAL_SETTINGS_KEY = "peripheralSettings";
    protected CompoundTag peripheralSettings;
    protected NonNullList<ItemStack> items;
    @Nullable
    protected T peripheral = null;
    private IItemHandler itemHandler;
    private IFluidHandler fluidHandler;

    public PeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        if (this instanceof IInventoryBlock<?> inventoryBlock) {
            items = NonNullList.withSize(inventoryBlock.getInvSize(), ItemStack.EMPTY);
        } else {
            items = NonNullList.withSize(0, ItemStack.EMPTY);
        }
        peripheralSettings = new CompoundTag();
    }

    @Nullable
    @Override
    public IPeripheral createPeripheralCap(@Nullable Direction side) {
        if (peripheral == null)
            // Perform later peripheral creation, because creating peripheral
            // on init of tile entity cause some infinity loop, if peripheral
            // are depend on tile entity data
            this.peripheral = this.createPeripheral();
        if (peripheral.isEnabled()) {
            return peripheral;
        } else {
            AdvancedPeripherals.debug(peripheral.getType() + " is disabled, you can enable it in the Configuration.");
        }
        return null;
    }

    @Nullable
    @Override
    public IFluidHandler createFluidHandlerCap(@Nullable Direction side) {
        if (fluidHandler == null)
            fluidHandler = new FluidTank(0);
        return fluidHandler;
    }

    @Nullable
    @Override
    public IItemHandler createItemHandlerCap(@Nullable Direction side) {
        if (itemHandler == null)
            itemHandler = new SidedInvWrapper(this, null);
        return itemHandler;
    }

    @NotNull
    protected abstract T createPeripheral();

    public Iterable<IComputerAccess> getConnectedComputers() {
        if (peripheral == null) // just avoid some NPE in strange cases
            return Collections.emptyList();
        return peripheral.getConnectedComputers();
    }

    /*@Override
    public ITextComponent getDisplayName() {
        return this instanceof IInventoryBlock ? ((IInventoryBlock) this).getDisplayName() : null;
    }*/

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ContainerHelper.saveAllItems(tag, items, provider);
        if (!peripheralSettings.isEmpty()) tag.put(PERIPHERAL_SETTINGS_KEY, peripheralSettings);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        ContainerHelper.loadAllItems(tag, items, provider);
        peripheralSettings = tag.getCompound(PERIPHERAL_SETTINGS_KEY);
        super.loadAdditional(tag, provider);
    }

    @Override
    protected Component getDefaultName() {
        return this instanceof IInventoryBlock<?> inventoryBlock ? inventoryBlock.getDisplayName() : null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player playerEntity) {
        return createMenu(id, inventory);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return this instanceof IInventoryBlock<?> inventoryBlock ? inventoryBlock.createContainer(id, player, worldPosition, level) : null;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, @Nullable Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return this instanceof IInventoryBlock;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (itemStack.isEmpty()) return true;
        }
        return false;
    }


    @NotNull
    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    @NotNull
    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(items, index, count);
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @NotNull
    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    public CompoundTag getPeripheralSettings() {
        return peripheralSettings;
    }

    @Override
    public void markSettingsChanged() {
        setChanged();
    }
}

