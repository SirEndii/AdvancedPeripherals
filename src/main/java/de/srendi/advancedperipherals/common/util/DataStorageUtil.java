package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeData;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import static de.srendi.advancedperipherals.common.setup.DataComponents.ROTATION_CHARGE_SETTING;

public class DataStorageUtil {

    public static DataComponentPatch getDataStorage(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
        return access.getUpgradeData(side);
    }

    public static void putDataStorage(@NotNull ITurtleAccess access, @NotNull TurtleSide side, DataComponentPatch dataComponent) {
        access.setUpgradeData(side, dataComponent);
    }

    public static CompoundTag getDataStorage(@NotNull IPeripheralTileEntity tileEntity) {
        return tileEntity.getPeripheralSettings();
    }

    public static DataComponentPatch getDataStorage(@NotNull IPocketAccess pocket) {
        return pocket.getUpgradeData();
    }

    public static void putDataStorage(@NotNull IPocketAccess pocket, DataComponentPatch dataComponent) {
        pocket.setUpgradeData(dataComponent);
    }

    /**
     * This class is for persistent data sharing between peripherals and another part of systems
     * Like, for example, for ModelTransformingTurtle logic, because it's executed on the client where
     * aren't any peripherals available
     **/

    public static class RotationCharge {
        public static final int ROTATION_STEPS = 36;
        /**
         * Used for gear rotation animation
         */
        public static int get(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
            return getDataStorage(access, side).get(ROTATION_CHARGE_SETTING.get()).get();
        }

        public static boolean consume(@NotNull ITurtleAccess access, @NotNull TurtleSide side) {
            //TODO
            /*DataComponentPatch data = getDataStorage(access, side);
            int currentCharge = data.get(ROTATION_CHARGE_SETTING.get()).get();
            if (currentCharge > 0) {
                data.(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING) - 1));
                access.updateUpgradeNBTData(side);
                return true;
            }*/
            return false;
        }

        public static void addCycles(IPeripheralOwner owner, int count) {
            //TODO
            /*CompoundTag data = owner.getDataStorage();
            data.putInt(ROTATION_CHARGE_SETTING, Math.max(0, data.getInt(ROTATION_CHARGE_SETTING)) + count * ROTATION_STEPS);
            owner.markDataStorageDirty();
            */
        }

    }
}
