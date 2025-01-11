package de.srendi.advancedperipherals.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.ToastUtil;
import de.srendi.advancedperipherals.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ToastToClientPacket implements IAPPacket {

    public static final ResourceLocation ID = AdvancedPeripherals.getRL("toasttoclient");

    private final Component title;
    private final Component component;

    public ToastToClientPacket(Component title, Component component) {
        this.title = title;
        this.component = component;
    }

    public static ToastToClientPacket decode(FriendlyByteBuf buffer) {
        return new ToastToClientPacket(buffer.read(), buffer.readComponent());
    }

    @Override
    public void handle(@NotNull IPayloadContext context) {
        // Should in the theory not happen, but safe is safe.
        if (!FMLEnvironment.dist.isClient()) {
            AdvancedPeripherals.debug("Tried to display toasts on the server, aborting.");
            return;
        }
        ToastUtil.displayToast(title, component);
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buffer) {
        buffer.writeComponent(this.title);
        buffer.writeComponent(this.component);
    }

    @NotNull
    @Override
    public ResourceLocation id() {
        return ID;
    }

}
