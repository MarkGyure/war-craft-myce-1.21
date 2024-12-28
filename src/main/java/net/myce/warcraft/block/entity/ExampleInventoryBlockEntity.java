package net.myce.warcraft.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myce.warcraft.block.ModBlocks;
import net.myce.warcraft.init.BlockEntityTypeInit;
import net.myce.warcraft.init.BlockInit;
import net.myce.warcraft.network.BlockPosPayload;
import net.myce.warcraft.screenhandler.ExampleInventoryScreenHandler;
import org.jetbrains.annotations.Nullable;

public class ExampleInventoryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    private static final Text DEFAULT_TITLE = Text.translatable("block.myce.warcraft.default");
    private static final Text MINT_PRESS_TITLE = Text.translatable("block.myce.warcraft.mint_press");
    private static final Text EXAMPLE_INVENTORY_TITLE = Text.translatable("block.myce.warcraft.example_inventory");

    private final SimpleInventory inventory = new SimpleInventory(36) {
        // markDirty(); is like updating the data of the inventory. so if you move an item from slot 1 to slot 2, you
        //wanna markDirty(); so the game refreshes and knows where the item is at now.
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
        // Updates the ammount of players that have the GUI opened for probably something important
        @Override
        public void onOpen(PlayerEntity player) {
            super.onOpen(player);
            ExampleInventoryBlockEntity.this.numPlayersOpen++;
            System.out.println("Num players open: " + numPlayersOpen);  // Debug logging
            update();
        }
        // Does the opposite of above for probably some important reason
        @Override
        public void onClose(PlayerEntity player) {
            super.onClose(player);
            ExampleInventoryBlockEntity.this.numPlayersOpen--;
            update();
        }
    };

    private final InventoryStorage inventoryStorage = InventoryStorage.of(inventory, null);
    private int numPlayersOpen;

    // Deadass i still dont know what super does../ but this grabs the block entity from where its registered(Init)
    //and assigns it when its called (which is in MintPressBlock)
    public ExampleInventoryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeInit.EXAMPLE_INVENTORY_BLOCK_ENTITY, pos, state);
    }

    // Idk what this does either tbh
    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world != null && !world.isClient) {
            Block block = this.getCachedState().getBlock();
            if (block == ModBlocks.MINT_PRESS) {
                System.out.println("Mint Press initialized!");
            } else if (block == BlockInit.EXAMPLE_INVENTORY_BLOCK) {
                System.out.println("Example Inventory Block initialized!");
            }
        }
    }

    // No clue what a payload is or does
    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    // Gets the Display name of what it shows inside when you open the GUI.
    @Override
    public Text getDisplayName() {
        if (this.getCachedState().isOf(ModBlocks.MINT_PRESS)) {
            return MINT_PRESS_TITLE;
        } else if (this.getCachedState().isOf(BlockInit.EXAMPLE_INVENTORY_BLOCK)) {
            return EXAMPLE_INVENTORY_TITLE;
        }
        return DEFAULT_TITLE;
    }

    // Creates the GUI screen using whatever syncId is, the players inventory and hotbar, and obv the player
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ExampleInventoryScreenHandler(syncId, playerInventory, this);
    }

    // Dont know why we need this
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
        if (nbt.contains("NumPlayersOpen", NbtElement.INT_TYPE)) {
            this.numPlayersOpen = nbt.getInt("NumPlayersOpen");
        }
    }

    // Again no clue
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
        nbt.putInt("NumPlayersOpen", this.numPlayersOpen);
        super.writeNbt(nbt, registryLookup);
    }

    // Couldn't tell ya
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // Nope.
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    // Ik update runs every frame! prob every tick bc this is MC
    private void update() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
    // Dont ask
    public InventoryStorage getInventoryProvider(Direction direction) {
        return inventoryStorage;
    }
    // No clue
    public SimpleInventory getInventory() {
        return this.inventory;
    }
    // This isnt even used ig, so
    public int getNumPlayersOpen() {
        return this.numPlayersOpen;
    }
}
