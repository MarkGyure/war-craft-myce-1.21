package net.myce.warcraft.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
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
                    .mapColor(MapColor.PURPLE))
            {
                //Stops the block from being placed in between specific y level and sends message if not
                @Override
                public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
                    // Get the Y-coordinate of the position
                    int y = pos.getY();

                    // Check if it's within the allowed Y-range (63 to 75)
                    if (y < 63 || y > 75) {
                        // If not, send a message to the player who is trying to place the block
                        if (world instanceof ServerWorld) { // Ensure we're on the server side
                            ServerWorld serverWorld = (ServerWorld) world;
                            serverWorld.getPlayers().forEach(player -> {
                                if (player instanceof PlayerEntity) {
                                    // Send a message to the player
                                    player.sendMessage(Text.translatable("block.warcraft.claim_stone.out_of_range").formatted(Formatting.RED), false);
                                }
                            });
                        }
                        return false; // Prevent placement
                    }
                    // Allow placement if in the correct range
                    return super.canPlaceAt(state, world, pos);
                }
            },
            "block.warcraft.claim_stone.tooltip"); // Tooltip key



    public static final Block TEAM_BLOCK = registerBlock("team_block",
            new Block(AbstractBlock.Settings.create()
                    .strength(5f)
                    .sounds(BlockSoundGroup.GLASS)
                    .luminance(state -> 100)
                    .dropsNothing()
                    .mapColor(MapColor.PURPLE))
            {
                //Stops the block from being placed in between specific y level and sends message if not
                @Override
                public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
                    // Get the Y-coordinate of the position
                    int y = pos.getY();

                    // Check if it's within the allowed Y-range (63 to 75)
                    if (y < 63 || y > 75) {
                        // If not, send a message to the player who is trying to place the block
                        if (world instanceof ServerWorld) { // Ensure we're on the server side
                            ServerWorld serverWorld = (ServerWorld) world;
                            serverWorld.getPlayers().forEach(player -> {
                                if (player instanceof PlayerEntity) {
                                    // Send a message to the player
                                    player.sendMessage(Text.translatable("block.warcraft.claim_stone.out_of_range").formatted(Formatting.RED), false);
                                }
                            });
                        }
                        return false; // Prevent placement
                    }
                    // Allow placement if in the correct range
                    return super.canPlaceAt(state, world, pos);
                }
            },
            "block.warcraft.claim_stone.tooltip"); // Tooltip key



    private static Block registerBlock(String name, Block block, @Nullable String tooltipKey) {
        registerBlockItem(name, block, tooltipKey);
    public static final Block MINT_PRESS = registerBlock("mint_press",
            new Block(AbstractBlock.Settings.create()
                    .strength(2.5f)
                    .sounds(BlockSoundGroup.ANVIL)
                    .dropsNothing() //this needs to change
                    .mapColor(MapColor.BLACK)));







    //Tool Tip Block Register
    private static Block registerToolTipBlock(String name, Block block, @Nullable String tooltipKey) {
        registerToolTipBlockItem(name, block, tooltipKey);
        return Registry.register(Registries.BLOCK, Identifier.of(WarCraft.MOD_ID, name), block);
    }
    // Block Register
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(WarCraft.MOD_ID, name), block);
    }
    //Block Item Registry
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(WarCraft.MOD_ID, name),
                new BlockItem(block, new Item.Settings()) {

                });
    }
    // Tool Tip Block Registry
    private static void registerToolTipBlockItem(String name, Block block, @Nullable String tooltipKey) {
        Registry.register(Registries.ITEM, Identifier.of(WarCraft.MOD_ID, name),
                new BlockItem(block, new Item.Settings()) {
                    // Adds custom tooltip
                    @Override
                    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
                        if (tooltipKey != null) {
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
