package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BlockRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class BlockObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 5;

    private final IObjectRenderer renderer = new BlockRenderer();

    @StringProperty
    public String block = "minecraft:air";

    public BlockObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public BlockObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final void setBlock(String block) {
        this.block = block;
        getModule().update(this);
    }

    @LuaFunction
    public final String getBlock() {
        return block;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeUtf(block);
    }

    public static BlockObject decode(FriendlyByteBuf buffer) {
        int objectId = buffer.readInt();
        boolean hasValidUUID = buffer.readBoolean();
        if (!hasValidUUID) {
            AdvancedPeripherals.exception("Tried to decode a buffer for an OverlayObject but without a valid player as target.", new IllegalArgumentException());
            return null;
        }
        UUID player = buffer.readUUID();
        int color = buffer.readInt();
        float opacity = buffer.readFloat();

        float x = buffer.readFloat();
        float y = buffer.readFloat();
        float z = buffer.readFloat();
        float maxX = buffer.readFloat();
        float maxY = buffer.readFloat();
        float maxZ = buffer.readFloat();
        boolean disableDepthTest = buffer.readBoolean();
        boolean disableCulling = buffer.readBoolean();
        float xRot = buffer.readFloat();
        float yRot = buffer.readFloat();
        float zRot = buffer.readFloat();

        String block = buffer.readUtf();

        BlockObject clientObject = new BlockObject(player);
        clientObject.setId(objectId);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.z = z;
        clientObject.maxX = maxX;
        clientObject.maxY = maxY;
        clientObject.maxZ = maxZ;
        clientObject.disableDepthTest = disableDepthTest;
        clientObject.disableCulling = disableCulling;
        clientObject.xRot = xRot;
        clientObject.yRot = yRot;
        clientObject.zRot = zRot;
        clientObject.block = block;

        return clientObject;
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }
}
