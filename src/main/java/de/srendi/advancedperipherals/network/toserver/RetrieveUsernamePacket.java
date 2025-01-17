package de.srendi.advancedperipherals.network.toserver;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.APNetworking;
import de.srendi.advancedperipherals.network.IAPPacket;
import de.srendi.advancedperipherals.network.toclient.UsernameToCachePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class RetrieveUsernamePacket implements IAPPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, RetrieveUsernamePacket> CODEC = StreamCodec.of((buffer, value) -> value.write(buffer), RetrieveUsernamePacket::decode);

    public static final Type<RetrieveUsernamePacket> TYPE = new Type<>(AdvancedPeripherals.getRL("retrieveusername"));

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

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUUID(requester);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
