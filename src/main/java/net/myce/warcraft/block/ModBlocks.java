package net.myce.warcraft.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.MapColor;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.world.BlockView;
import net.myce.warcraft.WarCraft;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


import java.util.List;

public class ModBlocks {

    public static final Block CLAIM_STONE = registerBlock("claim_stone",
            new Block(AbstractBlock.Settings.create()
                    .strength(5f)
                    .sounds(BlockSoundGroup.LODESTONE)
                    .luminance(state -> 15)
                    .dropsNothing()
                    .mapColor(MapColor.PURPLE)), // Block settings
            "block.warcraft.claim_stone.tooltip"); // Tooltip key

    private static Block registerBlock(String name, Block block, @Nullable String tooltipKey) {
        registerBlockItem(name, block, tooltipKey);
        return Registry.register(Registries.BLOCK, Identifier.of(WarCraft.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block, @Nullable String tooltipKey) {
        Registry.register(Registries.ITEM, Identifier.of(WarCraft.MOD_ID, name),
                new BlockItem(block, new Item.Settings()) {
                    @Override
                    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
                        if (tooltipKey != null) {
                            // Add your custom tooltip directly
                            tooltip.add(Text.translatable(tooltipKey).formatted(Formatting.RED, Formatting.BOLD));
                        }
                    }
                });
    }




    public static void registerModBlocks() {
        WarCraft.LOGGER.info("Registering Mod Blocks for " + WarCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.CLAIM_STONE);
        });
    }
}
