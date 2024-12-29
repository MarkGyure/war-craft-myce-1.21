package net.myce.warcraft.networking;

import net.minecraft.util.Identifier;

public class NetworkingConstants
{
    public static final Identifier REQUEST_PACKET_ID = Identifier.of("warcraft", "faction_join_request");
    public static final Identifier FEEDBACK_PACKET_ID = Identifier.of("warcraft", "request_feedback");
    public static final Identifier ACCEPTANCE_PACKET_ID = Identifier.of("warcraft", "request_acceptance");
}
