package net.myce.warcraft.screenhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.myce.warcraft.block.entity.ExampleInventoryBlockEntity;

// "Factories" wrap other objects in another object so it can be called uppon and used. At least i think so.
public class ExampleInventoryScreenHandlerFactory implements NamedScreenHandlerFactory {
    private final ExampleInventoryBlockEntity blockEntity;

    public ExampleInventoryScreenHandlerFactory(ExampleInventoryBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ExampleInventoryScreenHandler(syncId, playerInventory, blockEntity);
    }

    @Override
    public Text getDisplayName() {
        return blockEntity.getDisplayName();
    }
}
