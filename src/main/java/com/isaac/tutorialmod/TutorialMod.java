package com.isaac.tutorialmod;

import com.isaac.tutorialmod.blocks.ModBlocks;
import com.isaac.tutorialmod.blocks.entity.ModBlockEntityTypes;
import com.isaac.tutorialmod.blocks.entity.client.ToydogFigureRenderer;
import com.isaac.tutorialmod.entity.ModEntityTypes;
import com.isaac.tutorialmod.entity.client.MudmanRenderer;
import com.isaac.tutorialmod.entity.client.SalamanderRenderer;
import com.isaac.tutorialmod.entity.client.ToydogRenderer;

import com.isaac.tutorialmod.entity.custom.Salamander;
import com.isaac.tutorialmod.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib3.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TutorialMod.MOD_ID)
public class TutorialMod
{
    public static final String MOD_ID = "tutorialmod";
    //private static final Logger LOGGER = LogUtils.getLogger();

    public TutorialMod()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //ModBlockTags.register(eventBus);
        ModItems.ITEMS.register(eventBus);
        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntityTypes.TILES.register(eventBus);
        ModEntityTypes.ENTITY_TYPES.register(eventBus);

        GeckoLib.initialize();

        eventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntityTypes.SALAMANDER.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Salamander::checkSalamanderSpawnRules);
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntityTypes.MUDMAN.get(), MudmanRenderer::new);
            EntityRenderers.register(ModEntityTypes.TOYDOG.get(), ToydogRenderer::new);
            EntityRenderers.register(ModEntityTypes.SALAMANDER.get(), SalamanderRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityTypes.TOYDOG_FIGURE.get(), ToydogFigureRenderer::new);
        }
    }
}
