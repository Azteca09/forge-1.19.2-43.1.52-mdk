package com.isaac.tutorialmod.event;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.blocks.entity.ModBlockEntityTypes;
import com.isaac.tutorialmod.blocks.entity.client.ToydogFigureRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClientBusEvents {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        //event.registerBlockEntityRenderer(ModBlockEntityTypes.TOYDOG_FIGURE_ENTITY.get(), ToydogFigureRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.TOYDOG_FIGURE.get(), ToydogFigureRenderer::new);
    }
}
