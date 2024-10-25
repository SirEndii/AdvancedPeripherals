package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraft.client.Minecraft;

public class OverlayGlassesFunctions implements IModuleFunctions {

    private final OverlayModule overlayModule;
    private final SmartGlassesAccess access;

    public OverlayGlassesFunctions(OverlayModule overlayModule) {
        this.overlayModule = overlayModule;
        this.access = overlayModule.access;
    }

    @LuaFunction
    public final MethodResult createRectangle(IArguments arguments) throws LuaException {
        RectangleObject rectangle = new RectangleObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(rectangle);

        return MethodResult.of(object, "SUCCESS");
    }

    @LuaFunction
    public final MethodResult createCircle(IArguments arguments) throws LuaException {
        CircleObject circle = new CircleObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(circle);

        return MethodResult.of(object, "SUCCESS");
    }

    @LuaFunction
    public final MethodResult createText(IArguments arguments) throws LuaException {
        TextObject circle = new TextObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(circle);

        return MethodResult.of(object, "SUCCESS");
    }

    @LuaFunction
    public final MethodResult createItem(IArguments arguments) throws LuaException {
        ItemObject item = new ItemObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(item);

        return MethodResult.of(object, "SUCCESS");
    }

    @LuaFunction
    public final MethodResult createBlock(IArguments arguments) throws LuaException {
        BlockObject block = new BlockObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(block);

        return MethodResult.of(object, "SUCCESS");
    }

    @LuaFunction
    public final MethodResult createBox(IArguments arguments) throws LuaException {
        BoxObject block = new BoxObject(overlayModule, arguments);
        RenderableObject object = overlayModule.addObject(block);

        return MethodResult.of(object, "SUCCESS");
    }

    @LuaFunction
    public final MethodResult getObject(IArguments arguments) throws LuaException {
        int id = arguments.getInt(0);
        return MethodResult.of(overlayModule.getObjects().get(id));
    }

    @LuaFunction
    public final MethodResult removeObject(int id) {
        return MethodResult.of(overlayModule.removeObject(id));
    }

    @LuaFunction
    public final MethodResult clear() {
        return MethodResult.of(overlayModule.clear());
    }

    @LuaFunction
    public final MethodResult getObjectsSize() {
        return MethodResult.of(overlayModule.getObjects().size());
    }

    // TODO: This will crash on dedicated servers
    @LuaFunction
    public final MethodResult getSize() {
        return MethodResult.of(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
    }

    @LuaFunction
    public final MethodResult getCoords() {
        return MethodResult.of(access.getEntity().position().x, access.getEntity().position().y, access.getEntity().position().z);
    }

    @LuaFunction
    public final MethodResult update() {
        return MethodResult.of(overlayModule.bulkUpdate());
    }

    @LuaFunction
    public final MethodResult autoUpdate(IArguments arguments) throws LuaException {
        overlayModule.autoUpdate = arguments.optBoolean(0, !overlayModule.autoUpdate);
        return MethodResult.of(overlayModule.autoUpdate);
    }


}
