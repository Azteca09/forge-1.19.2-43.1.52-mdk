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
    private static final ResourceLocation TOYDOG_LOCATION = new ResourceLocation("textures/entity/toydog.png");
    private static final ResourceLocation TOYDOG_TAME_LOCATION = new ResourceLocation("textures/entity/toydog_tamed.png");
    private static final ResourceLocation TOYDOG_EFFECT_LOCATION = new ResourceLocation("textures/entity/toydog_glowing.png");
    @Override
    public ResourceLocation getModelResource(Toydog object){
        return new ResourceLocation(TutorialMod.MOD_ID, "geo/toydog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Toydog object){
        //return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/toydog_tamed.png");
        if (object.isGlowing()){
            return TOYDOG_EFFECT_LOCATION;
        }else{
            return object.isTame() ? TOYDOG_TAME_LOCATION : TOYDOG_LOCATION;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(Toydog animatable) {
        return new ResourceLocation(TutorialMod.MOD_ID, "animations/toydog.animation.json");
    }

    public void setCustomAnimations(Toydog toydog, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(toydog, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone tail = this.getAnimationProcessor().getBone("tail");
        EntityModelData extraData = (EntityModelData)animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        AnimationData manager = toydog.getFactory().getOrCreateAnimationData(instanceId);

        int unpausedMultiplier = Minecraft.getInstance().isPaused() && !manager.shouldPlayWhilePaused ? 0 : 1;
        head.setRotationX(extraData.headPitch * 0.017453292F * (float)unpausedMultiplier);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F * (float)unpausedMultiplier);

        tail.setRotationX(toydog.getTailAngle());
    }
}
