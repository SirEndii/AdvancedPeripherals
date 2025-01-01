package de.srendi.advancedperipherals.common.addons.valkyrienskies;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataCorePlugin;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.List;
import java.util.stream.Collectors;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.MOUNT_SHIP;

public class AutomataVSMountPlugin extends AutomataCorePlugin {

    public AutomataVSMountPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final boolean isOnShip() {
        IPeripheralOwner owner = this.automataCore.getPeripheralOwner();
        return APAddons.isBlockOnShip(owner.getLevel(), owner.getPos());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult canMountToShip() {
        List<ServerShip> ships = this.getMountableShips();
        if (ships.size() == 0) {
            return MethodResult.of();
        }
        List<String> shipNames = ships.stream().map(s -> s.getSlug()).collect(Collectors.toList());
        return MethodResult.of(shipNames);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult mountToShip(IArguments args) throws LuaException {
        String name = args.optString(0).orElse(null);
        List<ServerShip> ships = this.getMountableShips();
        if (ships.size() == 0) {
            return MethodResult.of(false, "no mountable ship detected");
        }
        ServerShip targetShip = null;
        if (name == null) {
            targetShip = ships.get(0);
        } else {
            for (ServerShip s : ships) {
                if (s.getSlug().equals(name)) {
                    targetShip = s;
                    break;
                }
            }
        }
        if (targetShip == null) {
            return MethodResult.of(false, "target ship not found");
        }
        IPeripheralOwner owner = this.automataCore.getPeripheralOwner();
        Level level = owner.getLevel();
        Vec3 pos = this.getMountDetectPosition();
        Vector3d targetPos = targetShip.getWorldToShip().transformPosition(new Vector3d(pos.x, pos.y, pos.z));
        BlockPos newPosition = new BlockPos(targetPos.x, targetPos.y, targetPos.z);
        return this.automataCore.withOperation(MOUNT_SHIP, new SingleOperationContext(1, 1), context -> {
            boolean result = owner.move(level, newPosition);
            if (!result) {
                return MethodResult.of(false, "cannot mount to ship");
            }
            return MethodResult.of(true);
        }, context -> {
            if (!owner.isMovementPossible(level, newPosition)) {
                return MethodResult.of(false, "move forbidden");
            }
            return null;
        });
    }

    protected Vec3 getMountDetectPosition() {
        IPeripheralOwner owner = this.automataCore.getPeripheralOwner();
        return owner.getCenterPos().add(Vec3.atLowerCornerOf(owner.getFacing().getNormal()));
    }

    protected List<ServerShip> getMountableShips() {
        IPeripheralOwner owner = this.automataCore.getPeripheralOwner();
        return ValkyrienSkies.getNearbyShips((ServerLevel) owner.getLevel(), this.getMountDetectPosition(), 0.5);
    }
}
