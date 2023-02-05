package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Toydog;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class ToydogRenderer extends GeoEntityRenderer<Toydog> {
    private static final ResourceLocation TOYDOG_LOCATION = new ResourceLocation("textures/entity/toydog.png");
    private static final ResourceLocation TOYDOG_TAME_LOCATION = new ResourceLocation("textures/entity/toydog_tamed.png");
    private static final ResourceLocation TOYDOG_EFFECT_LOCATION = new ResourceLocation("textures/entity/toydog_glowing.png");

    public ToydogRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ToydogModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(Toydog pEntity){
        if (pEntity.isGlowing()){
            System.out.println("glowing");
            return TOYDOG_EFFECT_LOCATION;
        }else{
            System.out.println("other");
            return pEntity.isTame() ? TOYDOG_TAME_LOCATION : TOYDOG_LOCATION;
        }
    }

    @Override
    public RenderType getRenderType(Toydog animatable, float partialTicks, PoseStack stack,
                                    @Nullable MultiBufferSource renderTypeBuffer,
                                    @Nullable VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1.0f, 1.0f, 1.0f);

        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

}
