package com.isaac.tutorialmod.blocks;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.blocks.custom.ToydogFigure;
import com.isaac.tutorialmod.blocks.entity.custom.ToydogFigureEntity;
import com.isaac.tutorialmod.item.ModCreativeModeTab;
import com.isaac.tutorialmod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TutorialMod.MOD_ID);
    public static final RegistryObject<Block> TOYDOG_FIGURE =
            BLOCKS.register("toydog_figure",
                    ToydogFigure::new);
    /*
    public static final RegistryObject<Block> TOYDOG_FIGURE = registerBlock("toydog_figure",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD).noOcclusion())
                    , ModCreativeModeTab.TUTORIAL_TAB);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItems(name, toReturn, tab);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItems(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }
    public static void register(IEventBus eventBus){

        BLOCKS.register(eventBus);
    }
    */

}
