package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.valkyrienskies.core.api.ships.Ship;

import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AutomataLookPlugin extends AutomataCorePlugin {

    public AutomataLookPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtBlock(@NotNull IArguments arguments) throws LuaException {
        Map<?, ?> opts = arguments.count() > 0 ? arguments.getTable(0) : Collections.emptyMap();
        float yaw = opts != null ? (float) TableHelper.optNumberField(opts, "yaw", 0) : 0;
        float pitch = opts != null ? (float) TableHelper.optNumberField(opts, "pitch", 0) : 0;

        automataCore.addRotationCycle();
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        HitResult result = owner.withPlayer(APFakePlayer.wrapActionWithRot(yaw, pitch, p -> p.findHit(true, false)));
        if (result.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "No block find");
        }

        BlockHitResult blockHit = (BlockHitResult) result;
        BlockPos blockPos = blockHit.getBlockPos();
        BlockState state = owner.getLevel().getBlockState(blockPos);
        Map<String, Object> data = new HashMap<>();
        ResourceLocation blockName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        data.put("name", blockName == null ? null : blockName.toString());
        data.put("tags", LuaConverter.tagsToList(() -> state.getBlock().builtInRegistryHolder().tags()));
        Vec3 pos = blockHit.getLocation();
        Vec3 origin = automataCore.getWorldPos();
        data.put("x", pos.x - origin.x);
        data.put("y", pos.y - origin.y);
        data.put("z", pos.z - origin.z);
        if (APAddons.vs2Loaded) {
            Ship ship = APAddons.getVS2Ship(automataCore.getLevel(), blockPos);
            if (ship != null) {
                data.put("shipId", ship.getId());
                data.put("shipName", ship.getSlug());
            }
        }
        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtEntity(@NotNull IArguments arguments) throws LuaException {
        Map<?, ?> opts = arguments.count() > 0 ? arguments.getTable(0) : Collections.emptyMap();
        float yaw = opts != null ? (float) TableHelper.optNumberField(opts, "yaw", 0) : 0;
        float pitch = opts != null ? (float) TableHelper.optNumberField(opts, "pitch", 0) : 0;

        automataCore.addRotationCycle();
        HitResult result = automataCore.getPeripheralOwner().withPlayer(APFakePlayer.wrapActionWithRot(yaw, pitch, p -> p.findHit(false, true)));
        if (result.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }

        EntityHitResult entityHit = (EntityHitResult) result;
        Vec3 origin = automataCore.getWorldPos();
        return MethodResult.of(LuaConverter.completeEntityWithPositionToLua(entityHit.getEntity(), origin, true));
    }

}
