package com.isaac.tutorialmod.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Mudman;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class MudmanRenderer extends GeoEntityRenderer<Mudman> {
    public MudmanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MudmanModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(Mudman instance){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/mudman.png");
    }

    @Override
    public RenderType getRenderType(Mudman animatable, float partialTicks, PoseStack stack,
                                    @Nullable MultiBufferSource renderTypeBuffer,
                                    @Nullable VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(0.8f, 0.8f, 0.8f);

        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
