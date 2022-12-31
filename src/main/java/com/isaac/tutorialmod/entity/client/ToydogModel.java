package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Toydog;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
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

    public void setCustomAnimations(Toydog animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData)animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
        //int unpausedMultiplier = Minecraft.m_91087_().m_91104_() && !manager.shouldPlayWhilePaused ? 0 : 1;
        int unpausedMultiplier = Minecraft.getInstance().isPaused() && !manager.shouldPlayWhilePaused ? 0 : 1;
        head.setRotationX(head.getRotationX() + extraData.headPitch * 0.017453292F * (float)unpausedMultiplier);
        head.setRotationY(head.getRotationY() + extraData.netHeadYaw * 0.017453292F * (float)unpausedMultiplier);
    }
}
