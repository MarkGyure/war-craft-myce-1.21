package net.myce.warcraft.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record RequestPayload(String username, String uuid) implements CustomPayload
{
    //identifies the payload ID
    public static final CustomPayload.Id<RequestPayload> ID = new CustomPayload.Id<>(NetworkingConstants.REQUEST_PACKET_ID);
    //creates the payload's data packet by storing the string's info
    public static final PacketCodec<RegistryByteBuf, RequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, RequestPayload::username, PacketCodecs.STRING, RequestPayload::uuid,RequestPayload::new);

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return ID;
    }
}
