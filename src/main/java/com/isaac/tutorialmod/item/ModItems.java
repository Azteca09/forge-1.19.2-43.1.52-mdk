package com.isaac.tutorialmod.item;

import com.isaac.tutorialmod.TutorialMod;
import com.isaac.tutorialmod.blocks.ModBlocks;
import com.isaac.tutorialmod.blocks.entity.ModBlockEntityTypes;
import com.isaac.tutorialmod.entity.ModEntityTypes;
import com.isaac.tutorialmod.item.custom.ToydogFigureItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TutorialMod.MOD_ID);
    public static final RegistryObject<Item> GOLD_HEART = ITEMS.register("gold_heart",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TUTORIAL_TAB)));
    public static final RegistryObject<Item> TOYDOG_FIGURE_ITEM = ITEMS.register("toydog_figure_item",
            () -> new BlockItem(ModBlocks.TOYDOG_FIGURE.get(),
                    new Item.Properties().tab(ModCreativeModeTab.TUTORIAL_TAB)));
    public static final RegistryObject<Item> MUDMAN_SPAWN_EGG = ITEMS.register("mudman_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.MUDMAN, 0x22b341, 0x19732e,
                    new Item.Properties().tab(ModCreativeModeTab.TUTORIAL_TAB)));

    public static final RegistryObject<Item> TOYDOG_SPAWN_EGG = ITEMS.register("toydog_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.TOYDOG, 0x22b341, 0x19732e,
                    new Item.Properties().tab(ModCreativeModeTab.TUTORIAL_TAB)));
    public static final RegistryObject<Item> SALAMANDER_SPAWN_EGG = ITEMS.register("salamander_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SALAMANDER, 0x22b341, 0x19732e,
                    new Item.Properties().tab(ModCreativeModeTab.TUTORIAL_TAB)));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}