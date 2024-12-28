package net.myce.warcraft.screenhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myce.warcraft.block.ModBlocks;
import net.myce.warcraft.block.entity.ExampleInventoryBlockEntity;
import net.myce.warcraft.init.ScreenHandlerTypeInit;
import net.myce.warcraft.network.BlockPosPayload;
import net.minecraft.inventory.Inventory;

// Class that handles how the screen is made. Mainly where slots are made / added. Also takes care of the players
//controls regarding the custom inventory. Examples of this are quick move.
public class ExampleInventoryScreenHandler extends ScreenHandler {
    private final ExampleInventoryBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public interface InventoryListener {
        void onInventoryChanged(Inventory inventory);
    }

    // Client Constructor
    public ExampleInventoryScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (ExampleInventoryBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    // Main Constructor - (Directly called from server)
    public ExampleInventoryScreenHandler(int syncId, PlayerInventory playerInventory, ExampleInventoryBlockEntity blockEntity) {
        super(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());
        BlockPos blockPos = blockEntity.getPos(); // Get the block position from the block entity
        World world = blockEntity.getWorld();  // Get the world from the block entity
        // Now instantiate ListenableInventory with the required parameters
        ListenableInventory inventory = new ListenableInventory(36, blockPos, world);
        inventory.addListener(new InventoryListener() {
            @Override
            public void onInventoryChanged(Inventory inventory) {
                System.out.println("Inventory changed!");
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    if (!stack.isEmpty()) {
                        System.out.println("Slot " + i + " contains " + stack.getCount() + "x " + stack.getItem());
                    }
                }
            }
        });
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
        int row = 2;
        int column = 4;
        addSlot(new Slot(inventory, column + (row * 9), 8 + (column * 18), 18 + (row * 18) - 9));
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
            // Check if the item is being placed into the custom slot
            if (slotIndex == 36) {
                slot.markDirty();
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.MINT_PRESS);
    }
}