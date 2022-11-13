package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.entity.custom.Mudman;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class MudmanRenderer extends GeoEntityRenderer<Mudman> {
    public Mudman(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MudmanModel());
        this.shadowRadius = 0.3f;
    }

}
