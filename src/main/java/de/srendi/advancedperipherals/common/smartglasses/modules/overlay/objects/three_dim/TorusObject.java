package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.TorusRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class TorusObject extends ThreeDimensionalObject {
    public static final int TYPE_ID = 7;

    private final IObjectRenderer renderer = new TorusRenderer();

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int sides = 32;

    @FixedPointNumberProperty(min = 1, max = 1024)
    public int rings = 16;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float minorRadius = 0.1f;

    @FloatingNumberProperty(min = 0.001f, max = 128)
    public float majorRadius = 0.5f;

    public TorusObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public TorusObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final void setMinorRadius(float radius) {
        this.minorRadius = radius;
        getModule().update(this);
    }

    @LuaFunction
    public final float getMinorRadius() {
        return minorRadius;
    }

    @LuaFunction
    public final void setMajorRadius(float radius) {
        this.majorRadius = radius;
        getModule().update(this);
    }

    @LuaFunction
    public final float getMajorRadius() {
        return majorRadius;
    }

    @LuaFunction
    public final void setSides(int sides) {
        this.sides = sides;
        getModule().update(this);
    }

    @LuaFunction
    public final int getSides() {
        return sides;
    }

    @LuaFunction
    public final void setRings(int rings) {
        this.rings = rings;
        getModule().update(this);
    }

    @LuaFunction
    public final int getRings() {
        return rings;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeInt(sides);
        buffer.writeInt(rings);
        buffer.writeFloat(minorRadius);
        buffer.writeFloat(majorRadius);
    }

    public static TorusObject decode(FriendlyByteBuf buffer) {
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

        int sectors = buffer.readInt();
        int stacks = buffer.readInt();
        float minorRadius = buffer.readFloat();
        float majorRadius = buffer.readFloat();

        TorusObject clientObject = new TorusObject(player);
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
        clientObject.sides = sectors;
        clientObject.rings = stacks;
        clientObject.minorRadius = minorRadius;
        clientObject.majorRadius = majorRadius;

        return clientObject;
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }
}
