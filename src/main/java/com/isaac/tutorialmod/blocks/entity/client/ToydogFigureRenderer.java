package com.isaac.tutorialmod.blocks.entity.client;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.blocks.entity.custom.ToydogFigureEntity;
import com.isaac.tutorialmod.entity.client.ToydogModel;
import com.isaac.tutorialmod.entity.custom.Toydog;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import javax.annotation.Nullable;

public class ToydogFigureRenderer extends GeoBlockRenderer<ToydogFigureEntity> {
    public ToydogFigureRenderer(BlockEntityRendererProvider.Context renderDispatcherIn){
        super(renderDispatcherIn, new ToydogFigureModel());
    }

    @Override
    public ResourceLocation getTextureLocation(ToydogFigureEntity instance){
        return new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/toydog.png");
    }

    @Override
    public RenderType getRenderType(ToydogFigureEntity animatable, float partialTicks, PoseStack stack,
                                    @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder,
                                    int packedLightIn, ResourceLocation textureLocation){
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
