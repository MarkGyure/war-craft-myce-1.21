package net.myce.warcraft.client.render;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import net.myce.warcraft.client.model.MintPressModel;
import net.myce.warcraft.block.ModBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockEntityRenderer;

public class MintPressRenderer extends GeoBlockRenderer<MintPressModel> {

    public MintPressRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(MintPressModel model, BlockEntityRendererFactory.Context context, float tickDelta) {
        super.render(model, context, tickDelta);
    }

    @Override
    public void render(MintPressModel model, BlockEntityRendererFactory.Context context, float tickDelta, boolean isItem) {
        super.render(model, context, tickDelta, isItem);
    }
}
