package net.myce.warcraft.init;


import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myce.warcraft.WarCraft;

import java.util.ArrayList;
import java.util.List;

public class ItemInit {
    public static final List<ItemConvertible> BLACKLIST = new ArrayList<>();

    public static final Item EXAMPLE_ITEM = register("example_item", new Item(new Item.Settings()));


    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, WarCraft.id(name), item);
    }

    public static void load() {
    }
}