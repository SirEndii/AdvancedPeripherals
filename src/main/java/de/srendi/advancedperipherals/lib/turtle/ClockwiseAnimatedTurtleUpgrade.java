package de.srendi.advancedperipherals.lib.turtle;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static de.srendi.advancedperipherals.common.setup.DataComponents.TURTLE_UPGRADE_STORED_DATA;

public abstract class ClockwiseAnimatedTurtleUpgrade<T extends IBasePeripheral<?>> extends PeripheralTurtleUpgrade<T> {


    protected ClockwiseAnimatedTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, item);
    }

    // Optional callbacks for addons based on AP
    public void chargeConsumingCallback() {

    }

    @Override
    public ItemStack getUpgradeItem(DataComponentPatch upgradeData) {
        if (upgradeData.isEmpty()) return getCraftingItem();
        ItemStack baseItem = getCraftingItem().copy();
        baseItem.applyComponents(upgradeData);
        return baseItem;
    }

    @Override
    public DataComponentPatch getUpgradeData(ItemStack stack) {
        var storedData = stack.get(TURTLE_UPGRADE_STORED_DATA);
        if (storedData == null)
            return DataComponentPatch.EMPTY;
        return storedData;
    }

    @Override
    public boolean isItemSuitable(@NotNull ItemStack stack) {
        if (!stack.has(TURTLE_UPGRADE_STORED_DATA))
            return super.isItemSuitable(stack);
        var tweakedStack = stack.copy();
        tweakedStack.remove(TURTLE_UPGRADE_STORED_DATA);
        return super.isItemSuitable(tweakedStack);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (tick % 2 == 0) {
            if (DataStorageUtil.RotationCharge.consume(turtle, side))
                chargeConsumingCallback();
        }
    }
}
