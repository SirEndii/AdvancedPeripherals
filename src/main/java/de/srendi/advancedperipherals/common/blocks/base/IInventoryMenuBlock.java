package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.container.base.BaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

/**
 * Used to assign an inventory storage to a block entity with a proper container

 * @param <T>
 */
public interface IInventoryMenuBlock<T extends BaseContainer>  extends IInventoryBlock {

    Component getDisplayName();

    T createContainer(int id, Inventory playerInventory, BlockPos pos, Level world);

}
