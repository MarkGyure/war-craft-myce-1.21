package net.myce.warcraft.init;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myce.warcraft.WarCraft;
import net.myce.warcraft.block.entity.ExampleInventoryBlockEntity;

public class BlockEntityTypeInit {
    public static final BlockEntityType<ExampleInventoryBlockEntity> EXAMPLE_INVENTORY_BLOCK_ENTITY = register("example_inventory_block_entity",
            BlockEntityType.Builder.create(ExampleInventoryBlockEntity::new, BlockInit.EXAMPLE_INVENTORY_BLOCK)
                    .build());

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, WarCraft.id(name), type);
    }

    public static void load() {}
}