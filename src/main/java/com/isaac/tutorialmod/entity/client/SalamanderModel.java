package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Salamander;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SalamanderModel extends AnimatedGeoModel<Salamander> {
    @Override
    public ResourceLocation getModelResource(Salamander object){
        return new ResourceLocation(TutorialMod.MOD_ID, "geo/salamander.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Salamander object){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/salamander.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Salamander animatable) {
        return new ResourceLocation(TutorialMod.MOD_ID, "animations/salamander.animation.json");
    }
}
