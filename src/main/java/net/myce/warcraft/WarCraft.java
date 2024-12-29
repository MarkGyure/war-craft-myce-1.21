package net.myce.warcraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.myce.warcraft.block.ModBlocks;
import net.myce.warcraft.events.ClaimStoneChunkHandler;
import net.myce.warcraft.factions.FactionSuggestionProvider;
import net.myce.warcraft.factions.Factions;
import net.myce.warcraft.item.ModItemGroups;
import net.myce.warcraft.item.ModItems;
import net.myce.warcraft.networking.AcceptancePayload;
import net.myce.warcraft.networking.FeedbackPayload;
import net.myce.warcraft.networking.RequestPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WarCraft implements ModInitializer
{
	public static final String MOD_ID = "warcraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ClaimStoneChunkHandler.register();

		PayloadTypeRegistry.playS2C().register(RequestPayload.ID, RequestPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(FeedbackPayload.ID, FeedbackPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(AcceptancePayload.ID, AcceptancePayload.CODEC);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
		{
			StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(handler.getPlayer().getServer());
			PlayerData playerData = StateSaverAndLoader.getPlayerState(handler.getPlayer());

			server.execute(() ->
			{
				if(playerData.username.isEmpty() || !playerData.username.equals(handler.getPlayer().getNameForScoreboard()))
				{
					playerData.username = handler.getPlayer().getNameForScoreboard();
				}

				if(playerData.factionLeader)
				{
					ServerPlayerEntity player = handler.getPlayer();

					if(!Factions.getLeaderInboxSlot(playerData, 1))
					{
						ServerPlayNetworking.send(player,
								new RequestPayload(Factions.getUsername(serverState, playerData.incomingRequestOne), playerData.incomingRequestOne.toString()));
                    }

					if(!Factions.getLeaderInboxSlot(playerData, 2))
					{
						ServerPlayNetworking.send(player,
								new RequestPayload(Factions.getUsername(serverState, playerData.incomingRequestTwo), playerData.incomingRequestTwo.toString()));
					}

					if(!Factions.getLeaderInboxSlot(playerData, 3))
					{
						ServerPlayNetworking.send(player,
								new RequestPayload(Factions.getUsername(serverState, playerData.incomingRequestThree), playerData.incomingRequestThree.toString()));
					}
				}
			});
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("createfaction") //phrase/word used to call command
						.then(argument("name", StringArgumentType.string()) //creates the argument that asks for the name of the faction
								.executes(context ->
								{
									StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
									PlayerData playerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

									final String name = StringArgumentType.getString(context, "name"); //creates a string that gets the argument
									final boolean available = Factions.checkIfFactionNameAvailable(serverState, name);
									final boolean notInAFaction = Factions.checkIfAlreadyInAFaction(playerData);

									if(available && notInAFaction)
									{
										serverState.factionList.add(name);
										playerData.factionName = name;
										playerData.factionLeader = true;

										context.getSource().sendFeedback(() -> Text.literal("Your faction " + name + " is created."), false);
									}
									else if(!available)
										context.getSource().sendFeedback(() -> Text.literal("Faction name is not available."), false);
									else
										context.getSource().sendFeedback(() -> Text.literal("You must leave the faction you're currently in first."), false);

									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("joinfaction") //phrase/word used to call command
						.then(argument("name", StringArgumentType.string()) //creates the argument that asks for the name of the faction'
								.suggests(new FactionSuggestionProvider())
								.executes(context ->
								{
									StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
									PlayerData joinerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

									final String name = StringArgumentType.getString(context, "name");
									final boolean notInAFaction = Factions.checkIfAlreadyInAFaction(joinerData);
									final boolean factionExists = Factions.checkIfFactionExists(serverState, name);

									if(notInAFaction && factionExists) //runs if the faction they requested to join exists, and they are not already in a faction
									{
										final UUID joinerUuid = context.getSource().getPlayer().getUuid(); //gets the joiner's uuid
										final UUID leaderUuid = Factions.getFactionLeader(serverState, name); //gets the leader's uuid
										final ServerPlayerEntity online = Factions.checkIfPlayerOnline(context.getSource().getWorld(), leaderUuid); //checks if the leader is online or not
										final int inboxAvailable = Factions.checkForAvailableSlot(serverState, leaderUuid, joinerUuid); //checks the leader's request inbox

										if(online != null || inboxAvailable > 0) //runs if the faction leader is online, or they have room in their inbox
										{
											context.getSource().sendFeedback(() -> Text.literal("Online: " + (online != null)), false);
											context.getSource().sendFeedback(() -> Text.literal("Inbox: " + inboxAvailable), false);
											context.getSource().sendFeedback(() -> Text.literal("World: " + context.getSource().getWorld()), false);
											context.getSource().sendFeedback(() -> Text.literal("WorldDoubleCheck: " + context.getSource().getWorld()), false);

											if(online != null)  //runs if the faction leader is online AND they have room in their inbox
											{
												context.getSource().sendFeedback(() -> Text.literal("Sent request to join faction."), false);
												ServerPlayNetworking.send(online, new RequestPayload(Factions.getUsername(serverState, joinerUuid), joinerUuid.toString()));
												Factions.sendToLeaderInbox(serverState, leaderUuid, joinerUuid, inboxAvailable);
											}
											else //runs if the faction leader is offline AND they have room in their inbox
											{
												ServerPlayNetworking.send(context.getSource().getPlayer(), new FeedbackPayload("Offline"));
												Factions.sendToLeaderInbox(serverState, leaderUuid, joinerUuid, inboxAvailable);
											}
										}
										else //runs if the player has already sent a join request or the leader's inbox is full
										{
											if(inboxAvailable < 0)
											{
												ServerPlayNetworking.send(context.getSource().getPlayer(), new FeedbackPayload("Already sent request"));
											}
											else
												ServerPlayNetworking.send(context.getSource().getPlayer(), new FeedbackPayload("Inbox full"));
										}
									}
									else if(!notInAFaction)
									{
										context.getSource().sendFeedback(() -> Text.literal("You must leave the faction you're currently in first."), false);
									}
									else
									{
										context.getSource().sendFeedback(() -> Text.literal("No faction exists with this name."), false);
									}

									return 1;
								}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("factionlist")
						.executes(context ->
						{
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());

							for(int i = 0; i < serverState.factionList.size(); i++)
							{
								int index = i;
								context.getSource().sendFeedback(() -> Text.literal(serverState.factionList.get(index)), false);
							}

							return 1;
						})));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("factioninfo")
						.executes(context ->
						{
							PlayerData playerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

							if(!playerData.factionName.isEmpty())
								context.getSource().sendFeedback(() -> Text.literal(playerData.factionName), false);
							else
								context.getSource().sendFeedback(() -> Text.literal("You are not in a faction."), false);

							return 1;
						})));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("inbox")
						.executes(context ->
						{
							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
							PlayerData playerData = serverState.players.get(context.getSource().getPlayer().getUuid());

							context.getSource().sendFeedback(() -> Text.literal(playerData.incomingRequestOne.toString()), false);
							context.getSource().sendFeedback(() -> Text.literal(playerData.incomingRequestTwo.toString()), false);
							context.getSource().sendFeedback(() -> Text.literal(playerData.incomingRequestThree.toString()), false);

							return 1;
						})));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("acceptrequest")
					.then(argument("joiner", UuidArgumentType.uuid())
						.then(argument("leader", UuidArgumentType.uuid())
								.requires(source -> source.hasPermissionLevel(4))
								.executes(context ->
								{
									StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());

									final UUID joinerUuid = UuidArgumentType.getUuid(context, "joiner");
									final UUID leaderUuid = UuidArgumentType.getUuid(context, "leader");

									final boolean accepted = Factions.acceptRequestToJoin(serverState, leaderUuid, joinerUuid);
									final ServerPlayerEntity online = Factions.checkIfPlayerOnline(context.getSource().getWorld(), joinerUuid);

									if(accepted || online != null)
									{
										if(accepted && online != null)
										{
											ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(joinerUuid),
													new AcceptancePayload("Joiner accepted", serverState.players.get(leaderUuid).factionName));

											ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
													new AcceptancePayload("Leader accepted", Factions.getUsername(serverState, joinerUuid)));
										}
										else if(!accepted)
											ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
													new FeedbackPayload("Already in a faction"));
										else
											ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
													new AcceptancePayload("Leader accepted", Factions.getUsername(serverState, joinerUuid)));
									}
									else
									{
										ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
												new FeedbackPayload("Already in a faction"));
									}

									return 1;
								})))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("declinerequest")
						.then(argument("joiner", StringArgumentType.string())
								.then(argument("leader", UuidArgumentType.uuid())
										.requires(source -> source.hasPermissionLevel(4))
										.executes(context ->
										{
											StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());

											final String joiner = StringArgumentType.getString(context, "joiner");

											final UUID joinerUuid = context.getSource().getServer().getPlayerManager().getPlayer(joiner).getUuid();
											final UUID leaderUuid = UuidArgumentType.getUuid(context, "leader");

											final boolean declined = Factions.declineRequestToJoin(serverState, leaderUuid, joinerUuid);
											final ServerPlayerEntity online = Factions.checkIfPlayerOnline(context.getSource().getWorld(), joinerUuid);

											if(declined || online != null)
											{
												if(declined && online != null)
												{
													ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(joinerUuid),
															new AcceptancePayload("Joiner declined", serverState.players.get(leaderUuid).factionName));

													ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
															new AcceptancePayload("Leader declined", Factions.getUsername(serverState, joinerUuid)));
												}
												else if(!declined)
													ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
															new FeedbackPayload("Already in a faction"));
												else
													ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
															new AcceptancePayload("Leader declined", Factions.getUsername(serverState, joinerUuid)));
											}
											else
											{
												ServerPlayNetworking.send(context.getSource().getServer().getPlayerManager().getPlayer(leaderUuid),
														new FeedbackPayload("Already in a faction"));
											}

											return 1;
										})))));
	}
}