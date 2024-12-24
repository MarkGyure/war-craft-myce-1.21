package net.myce.warcraft.screenhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.myce.warcraft.block.entity.ExampleInventoryBlockEntity;
import net.myce.warcraft.init.BlockInit;
import net.myce.warcraft.init.ScreenHandlerTypeInit;
import net.myce.warcraft.network.BlockPosPayload;

public class ExampleInventoryScreenHandler extends ScreenHandler {
    private final ExampleInventoryBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    // Client Constructor
    public ExampleInventoryScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (ExampleInventoryBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    // Main Constructor - (Directly called from server)
    public ExampleInventoryScreenHandler(int syncId, PlayerInventory playerInventory, ExampleInventoryBlockEntity blockEntity) {
        super(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER, syncId);

        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        SimpleInventory inventory = this.blockEntity.getInventory();
        checkSize(inventory, 36);
        inventory.onOpen(playerInventory.player);

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addBlockInventory(inventory);
    }

    private void addPlayerInventory(PlayerInventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 102 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 160));
        }
    }

    private void addBlockInventory(SimpleInventory inventory) {
        // Add a single slot in the middle of the 4x9 grid
        int row = 2;  // Middle row (index 2 in a 4-row grid)
        int column = 4;  // Middle column (index 4 in a 9-column grid)
        addSlot(new Slot(inventory, column + (row * 9), 8 + (column * 18), 18 + (row * 18)));
    }


    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.blockEntity.getInventory().onClose(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = getSlot(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack inSlot = slot.getStack();
            newStack = inSlot.copy();

            // Check if the item is being placed into the custom slot (index 36)
            if (slotIndex == 36) {
                // Consume the item when it is moved into the custom slot
                consumeItem(inSlot);  // Consume the item
                slot.setStack(ItemStack.EMPTY);  // Clear the slot after consumption
                slot.markDirty();  // Mark the slot as dirty to update the inventory state
            } else {
                // Handle the usual inventory moves
                if (slotIndex < 36) { // Moving from player inventory to custom block inventory
                    if (!insertItem(inSlot, 36, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else { // Moving from custom block inventory to player inventory
                    if (!insertItem(inSlot, 0, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (inSlot.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                } else {
                    slot.markDirty();
                }
            }
        }

        return newStack;
    }

    private void consumeItem(ItemStack itemStack) {
        // Check if the item is not empty and can be consumed
        if (!itemStack.isEmpty()) {
            System.out.println("Consuming Item: " + itemStack.getItem()); // Debug line

            // For simplicity, let's just remove the item (this is the "consumption" part).
            itemStack.decrement(1);  // Decrease the stack size by 1. You can change this to consume more if needed.

            // If the item is empty, you can update the inventory or trigger other effects.
            if (itemStack.isEmpty()) {
                System.out.println("Item consumed completely"); // Debug line
                // Example: Add a reward or trigger some other effect
            }
        }
    }



    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, BlockInit.EXAMPLE_INVENTORY_BLOCK);
    }



}