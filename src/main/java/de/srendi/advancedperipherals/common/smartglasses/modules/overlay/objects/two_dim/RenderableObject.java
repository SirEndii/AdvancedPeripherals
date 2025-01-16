package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FixedPointNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

// TODO: generate setters/getters lua functions out of our FloatingNumberProperty fields
public class RenderableObject extends OverlayObject {

    @FloatingNumberProperty(min = 0, max = 1)
    public float opacity = 1;

    @FixedPointNumberProperty(min = 0, max = 0xFFFFFF)
    public int color = 0xFFFFFF;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float x = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float y = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float z = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float maxX = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float maxY = 0;

    @FloatingNumberProperty(min = -32767, max = 32767)
    public float maxZ = 0;

    public RenderableObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public RenderableObject(UUID player) {
        super(player);
    }

    @LuaFunction
    public final float getOpacity() {
        return opacity;
    }

    @LuaFunction
    public final void setOpacity(double opacity) {
        this.opacity = (float) opacity;
        getModule().update(this);
    }

    @LuaFunction
    public final int getColor() {
        return color;
    }

    @LuaFunction
    public final void setColor(int color) {
        this.color = color;
        getModule().update(this);
    }

    @LuaFunction
    public final void setMaxX(double maxX) {
        this.maxX = (float) maxX;
        getModule().update(this);
    }

    @LuaFunction
    public final double getMaxX() {
        return maxX;
    }

    @LuaFunction
    public final void setMaxY(double maxY) {
        this.maxY = (float) maxY;
        getModule().update(this);
    }

    @LuaFunction
    public final double getMaxY() {
        return maxY;
    }

    @LuaFunction
    public final void setMaxZ(double maxZ) {
        this.maxZ = (float) maxZ;
        getModule().update(this);
    }

    @LuaFunction
    public final double getMaxZ() {
        return maxZ;
    }

    @LuaFunction
    public final void setX(double x) {
        this.x = (float) x;
        getModule().update(this);
    }

    @LuaFunction
    public final double getX() {
        return x;
    }

    @LuaFunction
    public final void setY(double y) {
        this.y = (float) y;
        getModule().update(this);
    }

    @LuaFunction
    public final double getY() {
        return y;
    }

    @LuaFunction
    public final void setZ(double z) {
        this.z = (float) z;
        getModule().update(this);
    }

    @LuaFunction
    public final float getZ() {
        return z;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(color);
        buffer.writeFloat(opacity);

        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
        buffer.writeFloat(maxX);
        buffer.writeFloat(maxY);
        buffer.writeFloat(maxZ);
    }

    public IObjectRenderer getRenderObject() {
        return null;
    }

    @Override
    public String toString() {
        return "RenderableObject{" +
                "opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", sizeX=" + maxX +
                ", sizeY=" + maxY +
                ", sizeZ=" + maxZ +
                '}';
    }
}
