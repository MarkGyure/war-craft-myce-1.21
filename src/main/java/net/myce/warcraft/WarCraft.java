package net.myce.warcraft;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myce.warcraft.block.ModBlocks;
import net.myce.warcraft.item.ModItemGroups;
import net.myce.warcraft.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarCraft implements ModInitializer {
	public static final String MOD_ID = "warcraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private Integer totalDirtBlocksBroken = 0;

	@Override
	public void onInitialize()
	{
		ModItemGroups.registerItemGroups();

		ModBlocks.registerModBlocks();
		ModItems.registerModItems();

		PayloadTypeRegistry.playS2C().register(Payload.ID, Payload.CODEC);

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (state.getBlock() == Blocks.GRASS_BLOCK || state.getBlock() == Blocks.DIRT) {
				StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());

				serverState.totalDirtBlocksBroken += 1;


				MinecraftServer server = world.getServer();

				PacketByteBuf data = PacketByteBufs.create();
				data.writeInt(serverState.totalDirtBlocksBroken);

				ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());

				server.execute(() -> {
					ServerPlayNetworking.send(playerEntity, new Payload(totalDirtBlocksBroken));
				});
			}
		});
	}
}