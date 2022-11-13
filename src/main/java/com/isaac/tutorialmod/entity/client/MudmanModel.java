package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Mudman;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MudmanModel extends AnimatedGeoModel<Mudman> {

    @Override
    public ResourceLocation getModelResource(Mudman object){
        return new ResourceLocation(TutorialMod.MOD_ID, "geo/mudman.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Mudman object){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/mudman_texture.png");
    }
}
