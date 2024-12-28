package net.myce.warcraft.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.myce.warcraft.block.ExampleInventoryBlock;
import net.myce.warcraft.block.entity.ExampleInventoryBlockEntity;

// Class for example block entity renderer. All rendering stuff is removed because i dont want the model class
//to be rendering anything. all of that is done via the MintPress.json and texture. So also not needed.
public class ExampleInventoryBER implements BlockEntityRenderer<ExampleInventoryBlockEntity> {
    private final BlockEntityRendererFactory.Context context;

    public ExampleInventoryBER(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(ExampleInventoryBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f); // Center the rendering of the block
        // Handle rotation based on block facing direction
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(switch (entity.getCachedState().get(ExampleInventoryBlock.FACING)) {
            case EAST -> 270;
            case SOUTH -> 180;
            case WEST -> 90;
            default -> 0;
        }));
        matrices.pop();
    }
}