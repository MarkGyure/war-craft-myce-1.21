package net.myce.warcraft;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record Payload(String username) implements CustomPayload
{
    public static final CustomPayload.Id<Payload> ID = new CustomPayload.Id<>(NetworkingConstants.PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, Payload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, Payload::username, Payload::new);

    @Override
    public Payload.Id<? extends CustomPayload> getId()
    {
        return ID;
    }

}
