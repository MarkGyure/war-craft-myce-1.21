package net.myce.warcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.myce.warcraft.networking.AcceptancePayload;
import net.myce.warcraft.networking.FeedbackPayload;
import net.myce.warcraft.networking.RequestPayload;

import java.util.UUID;

public class WarCraftClient implements ClientModInitializer
{

    @Override
    public void onInitializeClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(RequestPayload.ID, (payload, context) ->
        {
            MinecraftClient client = context.client();

            client.execute(() ->
            {
                String joiner = payload.username();
                UUID joinerUuid = UUID.fromString(payload.uuid());
                UUID leaderUuid = client.player.getUuid();

                ClickEvent acceptEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptrequest " + joinerUuid + " " + leaderUuid);
                ClickEvent declineEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/declinerequest " + joinerUuid + " " + leaderUuid);

                MutableText acceptText = Text.literal("§a§l§nACCEPT").styled(style -> style.withClickEvent(acceptEvent));
                MutableText declineText = Text.literal("§c§l§nDECLINE").styled(style -> style.withClickEvent(declineEvent));

                context.player().sendMessage(Text.literal(joiner + " would like to join your faction."));
                context.player().sendMessage(acceptText);
                context.player().sendMessage(declineText);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(FeedbackPayload.ID, (payload, context) ->
        {
            MinecraftClient client = context.client();

            client.execute(() ->
            {
               if(payload.string().equals("Offline"))
               {
                   context.player().sendMessage(Text.literal
                           ("This clan's faction leader is currently offline. They will have to accept your request when they come back online."));
               }
               else if(payload.string().equals("Inbox full"))
               {
                   context.player().sendMessage(Text.literal
                           ("This clan's faction leader's inbox is full. Wait for them to clear their requests before requesting to join again."));
               }
               else if(payload.string().equals("Already sent request"))
               {
                   context.player().sendMessage(Text.literal
                           ("You have already sent a join request to this faction leader."));
               }
               else if(payload.string().equals("Already in a faction"))
               {
                   context.player().sendMessage(Text.literal
                           ("This player has already joined a different faction."));
               }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AcceptancePayload.ID, (payload, context) ->
        {
            MinecraftClient client = context.client();

            client.execute(() ->
            {
                String acceptance = payload.result();
                String faction = payload.faction();

                if(acceptance.equals("Joiner accepted"))
                    context.player().sendMessage(Text.literal("You have been accepted into the faction \"" + faction + "\"."));
                else if(acceptance.equals("Leader accepted"))
                    context.player().sendMessage(Text.literal("You have accepted " + faction + " into your faction."));
                else if(acceptance.equals("Joiner declined"))
                    context.player().sendMessage(Text.literal("You have been declined to join the faction \"" + faction + "\"."));
                else if(acceptance.equals("Leader declined"))
                    context.player().sendMessage(Text.literal("You have declined " + faction + " to join your faction."));
                else if(acceptance.equals("Already in a faction"))
                    context.player().sendMessage(Text.literal(faction + " already found a faction to join."));
                else if(acceptance.equals("Check1"))
                    context.player().sendMessage(Text.literal("Online: " + faction));
                else if(acceptance.equals("Check2"))
                    context.player().sendMessage(Text.literal("Accepted: " + faction));
                else if(acceptance.equals("Check3"))
                    context.player().sendMessage(Text.literal("World: " + faction));
                else if(acceptance.equals("Check4"))
                    context.player().sendMessage(Text.literal("WorldDoubleCheck: " + faction));
            });
        });
    }
}
