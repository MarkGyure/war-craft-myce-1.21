package net.myce.warcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class WarCraftClient implements ClientModInitializer
{

    @Override
    public void onInitializeClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(Payload.ID, (payload, context) ->
        {
            MinecraftClient client = context.client();

            client.execute(() ->
            {
                context.player().sendMessage(Text.literal("Total dirt blocks broken: " + payload.blocksBroken()));
            });
        });
    }
}
