package net.myce.warcraft.client.model;

import software.bernie.geckolib3.model.GeoBlockModel;
import net.minecraft.util.Identifier;

public class MintPressModel extends GeoBlockModel {

    public MintPressModel() {
        super(new Identifier("warcraft", "geo/mint_press.geo.json"));
    }

    @Override
    public Identifier getTextureResource(Object animatable) {
        return new Identifier("warcraft", "textures/block/mint_press_texture.png"); // Path to the PNG texture
    }
}
