package de.srendi.advancedperipherals.common.addons.valkyrienskies;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheralPlugin;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation.SCAN_SHIPS;

public class ShipScannerPlugin extends BasePeripheralPlugin {
    public ShipScannerPlugin(IPeripheralOwner owner) {
        super(owner);
    }

    @Override
    public IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{SCAN_SHIPS};
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scanShips(int radius) throws LuaException {
        return withOperation(SCAN_SHIPS, new SphereOperationContext(radius), context -> {
            return context.getRadius() > SCAN_SHIPS.getMaxCostRadius() ? MethodResult.of(null, "Radius exceeds max value") : null;
        }, context -> {
            ServerLevel level = (ServerLevel) this.owner.getLevel();
            Vec3 pos = this.owner.getCenterPos();
            Ship ship = APAddons.getVS2Ship(level, new BlockPos(pos));
            if (ship != null) {
                Vector3d newPos = ship.getShipToWorld().transformPosition(new Vector3d(pos.x, pos.y, pos.z));
                pos = new Vec3(newPos.x, newPos.y, newPos.z);
            }
            List<Vector3d> shipPoses = VSGameUtilsKt.transformToNearbyShipsAndWorld(level, pos.x, pos.y, pos.z, context.getRadius());
            List<Map<String, Object>> shipDatas = new ArrayList<>(shipPoses.size());
            for (Vector3d p : shipPoses) {
                ServerShip s = VSGameUtilsKt.getShipManagingPos(level, p.x, p.y, p.z);
                if (ship == null || s.getId() != ship.getId()) {
                    shipDatas.add(LuaConverter.shipToObject(s, pos));
                }
            }
            return MethodResult.of(shipDatas);
        }, null);
    }

    @LuaFunction
    public final MethodResult scanShipCost(int radius) {
        int estimatedCost = estimateShipCost(radius);
        if (estimatedCost < 0) {
            return MethodResult.of(null, "Radius exceeds max value");
        }
        return MethodResult.of(estimatedCost);
    }

    private static int estimateShipCost(int radius) {
        if (radius <= SCAN_SHIPS.getMaxFreeRadius()) {
            return 0;
        }
        if (radius > SCAN_SHIPS.getMaxCostRadius()) {
            return -1;
        }
        return SCAN_SHIPS.getCost(SphereOperationContext.of(radius));
    }
}
