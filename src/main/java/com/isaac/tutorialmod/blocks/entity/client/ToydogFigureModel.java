package com.isaac.tutorialmod.blocks.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.blocks.entity.custom.ToydogFigureEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ToydogFigureModel extends AnimatedGeoModel<ToydogFigureEntity> {
    @Override
    public ResourceLocation getModelResource(ToydogFigureEntity object){
        return new ResourceLocation(TutorialMod.MOD_ID, "geo/toydog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ToydogFigureEntity object){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/toydog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ToydogFigureEntity animatable) {
        return new ResourceLocation(TutorialMod.MOD_ID, "animations/toydog.animation.json"); //isaac this is test of the entity animation
    }
}
