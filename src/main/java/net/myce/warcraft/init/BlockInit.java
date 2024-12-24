package net.myce.warcraft.init;


import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.myce.warcraft.WarCraft;
import net.myce.warcraft.block.ExampleInventoryBlock;

import java.util.List;
import java.util.Optional;

public class BlockInit {
    public static final Block EXAMPLE_BLOCK = registerWithItem("example_block", new Block(AbstractBlock.Settings.create()
            .strength(1.5F, 6.0F)
            .requiresTool()));


    public static final ExampleInventoryBlock EXAMPLE_INVENTORY_BLOCK = registerWithItem("example_inventory_block",
            new ExampleInventoryBlock(AbstractBlock.Settings.create()
                    .strength(1.5F, 6.0F)
                    .requiresTool()));

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, WarCraft.id(name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block, Item.Settings settings) {
        T registered = register(name, block);
        ItemInit.register(name, new BlockItem(registered, settings));
        return registered;
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        return registerWithItem(name, block, new Item.Settings());
    }

    private static SuspiciousStewEffectsComponent createStewEffects(SuspiciousStewEffectsComponent.StewEffect... effects) {
        return new SuspiciousStewEffectsComponent(List.of(effects));
    }

    public static void load() {
    }
}