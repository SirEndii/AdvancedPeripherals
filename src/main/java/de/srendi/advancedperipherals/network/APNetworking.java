package de.srendi.advancedperipherals.network;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.network.toclient.UsernameToCachePacket;
import de.srendi.advancedperipherals.network.toserver.RetrieveUsernamePacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class APNetworking {
    private static final String PROTOCOL_VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();

    public static void init(IPayloadRegistrar registrar) {
        registrar.common(ToastToClientPacket.ID, ToastToClientPacket::decode, handler -> handler.client(IAPPacket::handlePacket));
        registrar.common(UsernameToCachePacket.ID, UsernameToCachePacket::decode, handler -> handler.client(IAPPacket::handlePacket));

        registrar.common(RetrieveUsernamePacket.ID, RetrieveUsernamePacket::decode, handler -> handler.server(IAPPacket::handlePacket));
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(AdvancedPeripherals.MOD_ID)
                .versioned(PROTOCOL_VERSION);
        init(registrar);
    }

    public static void sendTo(ServerPlayer player, CustomPacketPayload message) {
        if (!(player instanceof FakePlayer)) {
            PacketDistributor.PLAYER.with(player).send(message);
        }
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.SERVER.noArg().send(message);
    }
}
