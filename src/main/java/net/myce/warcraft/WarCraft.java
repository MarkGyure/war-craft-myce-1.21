package net.myce.warcraft;

import net.fabricmc.api.ModInitializer;

import net.myce.warcraft.block.ModBlocks;
import net.myce.warcraft.item.ModItemGroups;
import net.myce.warcraft.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Very important comment
public class WarCraft implements ModInitializer {
	public static final String MOD_ID = "warcraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		ModItemGroups.registerItemGroups();

		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
	}
}

// I love Bryce Evan Harbison