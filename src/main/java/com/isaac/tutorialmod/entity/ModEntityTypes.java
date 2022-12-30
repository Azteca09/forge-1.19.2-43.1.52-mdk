package com.isaac.tutorialmod.entity;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.entity.custom.Mudman;
import com.isaac.tutorialmod.entity.custom.Toydog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.rmi.registry.Registry;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TutorialMod.MOD_ID);

    public static final RegistryObject<EntityType<Mudman>> MUDMAN =
            ENTITY_TYPES.register("mudman",
                    () -> EntityType.Builder.of(Mudman::new, MobCategory.MONSTER)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "mudman").toString()));

    public static final RegistryObject<EntityType<Toydog>> TOYDOG =
            ENTITY_TYPES.register("toydog",
                    () -> EntityType.Builder.of(Toydog::new, MobCategory.CREATURE)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "toydog").toString()));

    public static void register(IEventBus eventBus) {

        ENTITY_TYPES.register(eventBus);
    }
}

