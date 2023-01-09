package com.isaac.tutorialmod.item.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.item.custom.ToydogFigureItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ToydogFigureItemModel extends AnimatedGeoModel<ToydogFigureItem> {
    @Override
    public ResourceLocation getModelResource(ToydogFigureItem object){
        return new ResourceLocation(TutorialMod.MOD_ID, "geo/toydog_figure.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ToydogFigureItem object){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/toydog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ToydogFigureItem animatable) {
        return new ResourceLocation(TutorialMod.MOD_ID, "animations/toydog.animation.json"); //isaac this is test of the entity animation
    }
}
