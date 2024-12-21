package net.myce.warcraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.text.Text;
import net.myce.warcraft.block.ModBlocks;
import net.myce.warcraft.item.ModItemGroups;
import net.myce.warcraft.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		PayloadTypeRegistry.playS2C().register(Payload.ID, Payload.CODEC);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("createfaction") //phrase/word used to call command
					.then(argument("name", StringArgumentType.string()) //creates the argument that asks for the name of the faction
							.executes(context ->
							{
								final String name = StringArgumentType.getString(context, "name"); //creates a string that gets the argument

								StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
								PlayerData playerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

								playerData.factionName = name;
								serverState.factionList.add(name);

								context.getSource().sendFeedback(() -> Text.literal("Your faction " + name + " is created."), false);

								return 1;
							}))));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(literal("factioninfo")
						.executes(context ->
						{
							PlayerData playerData = StateSaverAndLoader.getPlayerState(context.getSource().getPlayer());

							context.getSource().sendFeedback(() -> Text.literal(playerData.factionName), false);

							return 2;
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

							return 3;
						})));
	}
}