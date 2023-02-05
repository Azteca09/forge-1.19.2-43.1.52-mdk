package com.isaac.tutorialmod.misc;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class TMTagRegistry {
    public static final TagKey<Block> SALAMANDER_SPAWNABLE_ON = registerBlockTag("salamander_spawnable_on");

    private TMTagRegistry() {
    }
    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("tutorialmod", name));
    }
}
