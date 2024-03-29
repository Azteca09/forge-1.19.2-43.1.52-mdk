package com.isaac.tutorialmod.event;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.ModEntityTypes;
import com.isaac.tutorialmod.entity.custom.Mudman;
import com.isaac.tutorialmod.entity.custom.Salamander;
import com.isaac.tutorialmod.entity.custom.Toydog;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID)
    public static class ForgeEvents {

    }

    @Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.MUDMAN.get(), Mudman.setAttributes());
            event.put(ModEntityTypes.TOYDOG.get(), Toydog.setAttributes());
            event.put(ModEntityTypes.SALAMANDER.get(), Salamander.setAttributes());
        }

    }
}
