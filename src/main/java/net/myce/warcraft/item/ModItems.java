package net.myce.warcraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.myce.warcraft.WarCraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item WEALTH = registerItem("wealth", new Item(new Item.Settings()));
    public static final Item WAR_CRAFT = registerItem("war_craft", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(WarCraft.MOD_ID, name), item);
    }

    public static void registerModItems() {
        WarCraft.LOGGER.info("Registering Mod Items for " + WarCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(WEALTH);
        });
    }
}