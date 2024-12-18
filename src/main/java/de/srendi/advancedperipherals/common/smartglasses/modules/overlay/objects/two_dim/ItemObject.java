package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ItemRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class ItemObject extends RenderableObject {
    public static final int TYPE_ID = 3;

    private final IObjectRenderer renderer = new ItemRenderer();

    @StringProperty
    public String item = "minecraft:air";

    public ItemObject(OverlayModule module, IArguments arguments) throws LuaException {
        super(module, arguments);
        reflectivelyMapProperties(arguments);
    }

    public ItemObject(UUID player) {
        super(player);
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(TYPE_ID);
        super.encode(buffer);
        buffer.writeUtf(item);
    }

    public static ItemObject decode(FriendlyByteBuf buffer) {
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
        String item = buffer.readUtf();

        ItemObject clientObject = new ItemObject(player);
        clientObject.setId(objectId);
        clientObject.color = color;
        clientObject.opacity = opacity;
        clientObject.x = x;
        clientObject.y = y;
        clientObject.z = z;
        clientObject.maxX = maxX;
        clientObject.maxY = maxY;
        clientObject.maxZ = maxZ;
        clientObject.item = item;

        return clientObject;
    }

    @Override
    public IObjectRenderer getRenderObject() {
        return renderer;
    }

    @Override
    public String toString() {
        return "ItemObject{" +
                "item='" + item + '\'' +
                ", opacity=" + opacity +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                '}';
    }
}