package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Salamander;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class SalamanderRenderer extends GeoEntityRenderer<Salamander> {

    public SalamanderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SalamanderModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(Salamander instance){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/salamander.png");
    }

    @Override
    public RenderType getRenderType(Salamander animatable, float partialTicks, PoseStack stack,
                                    @Nullable MultiBufferSource renderTypeBuffer,
                                    @Nullable VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(0.8f, 0.8f, 0.8f);

        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
