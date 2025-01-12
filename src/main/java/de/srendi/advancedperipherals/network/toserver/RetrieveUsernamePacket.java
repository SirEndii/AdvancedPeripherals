package de.srendi.advancedperipherals.network.toserver;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.APNetworking;
import de.srendi.advancedperipherals.network.IAPPacket;
import de.srendi.advancedperipherals.network.toclient.UsernameToCachePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.UUID;

public class RetrieveUsernamePacket implements IAPPacket {

    public static final ResourceLocation ID = AdvancedPeripherals.getRL("retrieveusername");

    public UUID uuid;
    public UUID requester;

    public RetrieveUsernamePacket(UUID uuid, UUID requester) {
        this.uuid = uuid;
        this.requester = requester;
    }

    public static RetrieveUsernamePacket decode(FriendlyByteBuf buffer) {
        return new RetrieveUsernamePacket(buffer.readUUID(), buffer.readUUID());
    }

    @Override
    public void handle(IPayloadContext context) {
        Optional<GameProfile> gameProfile = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(uuid);
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(requester);

        // The player left the server before the packet could be handled
        if (player == null)
            return;

        if (gameProfile.isEmpty())
            return;

        APNetworking.sendTo(player, new UsernameToCachePacket(gameProfile.get().getId(), gameProfile.get().getName()));
    }

    /*@Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUUID(requester);
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
