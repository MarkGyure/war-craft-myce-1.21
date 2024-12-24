package net.myce.warcraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.myce.warcraft.WarCraft;
import net.myce.warcraft.block.ExampleInventoryBlock;
import net.myce.warcraft.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup WAR_CRAFT_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(WarCraft.MOD_ID, "war_craft_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.WAR_CRAFT))
                    .displayName(Text.translatable("itemgroup.warcraft.war_craft_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.WEALTH);
                        entries.add(ModBlocks.CLAIM_STONE);
                        entries.add(ModBlocks.TEAM_BLOCK);
                        
                    }).build());


    public static void registerItemGroups() {
        WarCraft.LOGGER.info("Registering Item Groups for " + WarCraft.MOD_ID);
    }
}