package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Toydog;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ToydogModel extends AnimatedGeoModel<Toydog>{
    @Override
    public ResourceLocation getModelResource(Toydog object){
        return new ResourceLocation(TutorialMod.MOD_ID, "geo/toydog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Toydog object){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/toydog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Toydog animatable) {
        return new ResourceLocation(TutorialMod.MOD_ID, "animations/toydog.animation.json");
    }
}
