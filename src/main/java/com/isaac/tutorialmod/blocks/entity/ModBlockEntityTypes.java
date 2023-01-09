package com.isaac.tutorialmod.blocks.entity;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.blocks.ModBlocks;
import com.isaac.tutorialmod.blocks.entity.custom.ToydogFigureEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static DeferredRegister<BlockEntityType<?>> TILES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TutorialMod.MOD_ID);
    public static final RegistryObject<BlockEntityType<ToydogFigureEntity>> TOYDOG_FIGURE =
            TILES.register("toydog_figure_entity", () ->
                BlockEntityType.Builder.of(ToydogFigureEntity::new,
                        ModBlocks.TOYDOG_FIGURE.get()).build(null));
    //public static void register(IEventBus eventBus) {
        //TILES.register(eventBus);
    //}
}
