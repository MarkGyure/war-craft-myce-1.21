package net.myce.warcraft.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record FeedbackPayload(String string) implements CustomPayload
{
    //identifies the payload ID
    public static final CustomPayload.Id<FeedbackPayload> ID = new CustomPayload.Id<>(NetworkingConstants.FEEDBACK_PACKET_ID);
    //creates the payload's data packet by storing the string's info
    public static final PacketCodec<RegistryByteBuf, FeedbackPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, FeedbackPayload::string, FeedbackPayload::new);

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return ID;
    }
}
