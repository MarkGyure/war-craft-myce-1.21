package net.myce.warcraft.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AcceptancePayload(String result, String faction) implements CustomPayload
{
    //identifies the payload ID
    public static final CustomPayload.Id<AcceptancePayload> ID = new CustomPayload.Id<>(NetworkingConstants.ACCEPTANCE_PACKET_ID);
    //creates the payload's data packet by storing the string's info
    public static final PacketCodec<RegistryByteBuf, AcceptancePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, AcceptancePayload::result, PacketCodecs.STRING, AcceptancePayload::faction,AcceptancePayload::new);

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return ID;
    }
}
