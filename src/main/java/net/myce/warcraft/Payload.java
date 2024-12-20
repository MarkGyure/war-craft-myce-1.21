package net.myce.warcraft;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record Payload(Integer blocksBroken) implements CustomPayload
{
    public static final CustomPayload.Id<Payload> ID = new CustomPayload.Id<>(NetworkingConstants.PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, Payload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, Payload::blocksBroken, Payload::new);
    // should you need to send more data, add the appropriate record parameters and change your codec:
    // public static final PacketCodec<RegistryByteBuf, BlockHighlightPayload> CODEC = PacketCodec.tuple(
    //         BlockPos.PACKET_CODEC, BlockHighlightPayload::blockPos,
    //         PacketCodecs.INTEGER, BlockHighlightPayload::myInt,
    //         Uuids.PACKET_CODEC, BlockHighlightPayload::myUuid,
    //         BlockHighlightPayload::new
    // );

    @Override
    public Payload.Id<? extends CustomPayload> getId()
    {
        return ID;
    }

}
