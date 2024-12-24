package net.myce.warcraft.client.screen;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myce.warcraft.WarCraft;
import net.myce.warcraft.screenhandler.ExampleInventoryScreenHandler;

public class ExampleInventoryBlockScreen extends HandledScreen<ExampleInventoryScreenHandler> {
    private static final Identifier TEXTURE = WarCraft.id("textures/gui/container/example_inventory_block.png");

    public ExampleInventoryBlockScreen(ExampleInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 184;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}