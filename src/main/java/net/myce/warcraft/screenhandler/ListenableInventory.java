package net.myce.warcraft.screenhandler;

import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myce.warcraft.item.ModItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Handles the effects of using the custom inventory. like dropping items, consuming items, ect.
public class ListenableInventory extends SimpleInventory {
    private final List<ExampleInventoryScreenHandler.InventoryListener> listeners;
    private final BlockPos blockPos;
    private final World world;

    private static final Map<Item, Integer> VIABLE_ITEMS_WEALTH = new HashMap<>();
    static {
        VIABLE_ITEMS_WEALTH.put(Items.EMERALD, 1);
        VIABLE_ITEMS_WEALTH.put(Items.COPPER_INGOT, 1);
        VIABLE_ITEMS_WEALTH.put(Items.IRON_INGOT, 1);
        VIABLE_ITEMS_WEALTH.put(Items.GOLD_INGOT, 2);
        VIABLE_ITEMS_WEALTH.put(Items.DIAMOND, 7);
        VIABLE_ITEMS_WEALTH.put(Items.NETHERITE_INGOT, 36);
    }

    // Constructor
    public ListenableInventory(int size, BlockPos blockPos, World world) {
        super(size);
        this.listeners = new ArrayList<>(); // Initialize the listeners list
        this.blockPos = blockPos;
        this.world = world;
    }

    public void addListener(ExampleInventoryScreenHandler.InventoryListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ExampleInventoryScreenHandler.InventoryListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        notifyListeners();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        notifyListeners();
    }

    private void notifyListeners() {
        for (ExampleInventoryScreenHandler.InventoryListener listener : listeners) {
            listener.onInventoryChanged(this);
        }
        // Trigger drop logic every time items are added to the inventory
        dropWealthItemIfNeeded();
    }

    private void dropWealthItemIfNeeded() {
        int totalWealth = 0;
        // Calculate total wealth based on the items in the inventory
        for (int i = 0; i < this.size(); i++) {
            ItemStack stack = this.getStack(i);
            if (!stack.isEmpty() && VIABLE_ITEMS_WEALTH.containsKey(stack.getItem())) {
                int wealthPerItem = VIABLE_ITEMS_WEALTH.get(stack.getItem());
                totalWealth += stack.getCount() * wealthPerItem;
                // Consume the stack by emptying it
                setStack(i, ItemStack.EMPTY);
            }
        }
        // Drop the wealth items if there is any wealth to drop
        if (totalWealth > 0) {
            for (int i = 0; i < totalWealth; i++) {
                ItemStack wealthStack = new ItemStack(ModItems.WEALTH);
                // Create an ItemEntity to drop the item at the block position
                ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), wealthStack);
                world.spawnEntity(itemEntity);
            }
        } else {
        }
    }
}
