package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.IInventoryBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.container.InventoryManagerContainer;
import de.srendi.advancedperipherals.common.items.MemoryCardItem;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static de.srendi.advancedperipherals.common.items.MemoryCardItem.OWNER_NBT_KEY;
import static de.srendi.advancedperipherals.common.setup.DataComponents.OWNER;

public class InventoryManagerEntity extends PeripheralBlockEntity<InventoryManagerPeripheral> implements IInventoryBlock<InventoryManagerContainer> {

    private UUID owner = null;

    public InventoryManagerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.INVENTORY_MANAGER.get(), pos, state);
    }

    @NotNull
    @Override
    protected InventoryManagerPeripheral createPeripheral() {
        return new InventoryManagerPeripheral(this);
    }

    @Override
    public InventoryManagerContainer createContainer(int id, Inventory playerInventory, BlockPos pos, Level world) {
        return new InventoryManagerContainer(id, playerInventory, pos, world);
    }

    @Override
    public int getInvSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStackIn, @Nullable Direction direction) {
        return itemStackIn.getItem() instanceof MemoryCardItem;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        if (stack.getItem() instanceof MemoryCardItem) {
            if (stack.has(OWNER)) {
                this.owner = stack.get(OWNER);
                stack.remove(OWNER);
            } else if (stack != this.getItem(index)) {
                // Only clear owner when the new card item is not the current item
                this.owner = null;
            }
        } else {
            this.owner = null;
        }
        super.setItem(index, stack);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.advancedperipherals.inventory_manager");
    }

    @Override
    public void loadAdditional(CompoundTag data, @NotNull HolderLookup.Provider provider) {
        if (data.contains("ownerId")) {
            this.owner = data.getUUID("ownerId");
        }
        super.loadAdditional(data, provider);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag data, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(data, provider);
        if (this.owner != null) {
            data.putUUID("ownerId", this.owner);
        }
    }

    public Player getOwnerPlayer() {
        if (this.owner == null) {
            return null;
        }
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(this.owner);
    }
}
