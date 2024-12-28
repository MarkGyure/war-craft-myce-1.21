package net.myce.warcraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myce.warcraft.block.ModBlocks;
import net.myce.warcraft.block.entity.ExampleInventoryBlockEntity;
import net.myce.warcraft.events.ClaimStoneChunkHandler;
import net.myce.warcraft.init.BlockEntityTypeInit;
import net.myce.warcraft.item.ModItemGroups;
import net.myce.warcraft.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WarCraft implements ModInitializer {
	public static final String MOD_ID = "warcraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize()
	{
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ClaimStoneChunkHandler.register();


		BlockEntityTypeInit.load();


		ItemStorage.SIDED.registerForBlockEntity(ExampleInventoryBlockEntity::getInventoryProvider, BlockEntityTypeInit.EXAMPLE_INVENTORY_BLOCK_ENTITY);

		PayloadTypeRegistry.playS2C().register(Payload.ID, Payload.CODEC);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("createfaction") //phrase/word used to call command
						.then(argument("name", StringArgumentType.string()) //creates the argument that asks for the name of the faction
								.executes(context ->
								{
									StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
									PlayerData playerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

									final String name = StringArgumentType.getString(context, "name"); //creates a string that gets the argument
									final boolean available = Factions.checkIfFactionNameAvailable(name, serverState);
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
						.then(argument("name", StringArgumentType.string()) //creates the argument that asks for the name of the faction
								.executes(context ->
								{
									StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
									PlayerData playerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

									final String name = StringArgumentType.getString(context, "name");
									final boolean notInAFaction = Factions.checkIfAlreadyInAFaction(playerData);
									final boolean factionExists = Factions.checkIfFactionExists(name, serverState);
									ServerPlayerEntity player = null;

									if(notInAFaction && factionExists)
									{
										context.getSource().sendFeedback(() -> Text.literal("Step 1"), false);
										List<ServerPlayerEntity> listOfPlayers = context.getSource().getWorld().getPlayers();

										for(ServerPlayerEntity listOfPlayer : listOfPlayers)
										{
											if (listOfPlayer.getUuid() == Factions.getFactionLeader(name, serverState))
											{
												player = listOfPlayer;
											}
										}

										ServerPlayNetworking.send(context.getSource().getPlayer(), new Payload(context.getSource().getPlayer().getName().toString()));
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
				dispatcher.register(literal("factioninfo")
						.then(argument("name", StringArgumentType.string()))
						.executes(context ->
						{
							PlayerData playerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

							context.getSource().sendFeedback(() -> Text.literal(playerData.factionName), false);

							return 1;
						})));

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
	}
	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

}