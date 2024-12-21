package net.myce.warcraft.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ClaimStoneChunkHandler {

    public static void register() {

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            // Ensure this logic only runs on the server side
            if (!world.isClient && player.getStackInHand(hand).getName().getString().equals("Claim Stone")) {

                // Get the block position and corresponding chunk
                BlockPos pos = hitResult.getBlockPos();
                ChunkPos chunkPos = new ChunkPos(pos);
                int chunkPosX = chunkPos.x;
                int chunkPosZ = chunkPos.z;

                // Notify the player about the chunk
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.sendMessage(
                            Text.literal("Claim Stone placed in chunk: [" + chunkPosX + ", " + chunkPosZ + "]"),
                            false
                    );
                }
                // allows block to be placed
                return ActionResult.PASS;
            }
            // allows it to be placed again ??? idrk whats going on
            return ActionResult.PASS;
        });
    }
}