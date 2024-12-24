package net.myce.warcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.text.Text;
import net.myce.warcraft.client.model.ExampleChestModel;
import net.myce.warcraft.client.renderer.ExampleInventoryBER;
import net.myce.warcraft.client.screen.ExampleInventoryBlockScreen;
import net.myce.warcraft.init.BlockEntityTypeInit;
import net.myce.warcraft.init.ScreenHandlerTypeInit;

public class WarCraftClient implements ClientModInitializer
{

    @Override
    public void onInitializeClient()
    {

        ClientPlayNetworking.registerGlobalReceiver(Payload.ID, (payload, context) ->
        {
            MinecraftClient client = context.client();

            client.execute(() ->
            {
                context.player().sendMessage(Text.literal(payload.username() + " would like to join your faction."));
            });
        });



        EntityModelLayerRegistry.registerModelLayer(ExampleChestModel.LAYER_LOCATION, ExampleChestModel::getTexturedModelData);
        HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER, ExampleInventoryBlockScreen::new);
        BlockEntityRendererFactories.register(BlockEntityTypeInit.EXAMPLE_INVENTORY_BLOCK_ENTITY, ExampleInventoryBER::new);



    }
}
