package de.srendi.advancedperipherals.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UsernameToCachePacket implements IAPPacket {

    public static final ResourceLocation ID = AdvancedPeripherals.getRL("usernametocache");

    public UUID uuid;
    public String username;

    public UsernameToCachePacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public static UsernameToCachePacket decode(FriendlyByteBuf buffer) {
        return new UsernameToCachePacket(buffer.readUUID(), buffer.readUtf());
    }

    @Override
    public void handle(IPayloadContext context) {
        ClientUUIDCache.putUsername(uuid, username);
    }

    /*@Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(username);
    }

    @NotNull
    @Override
    public ResourceLocation id() {
        return ID;
    }*/

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
