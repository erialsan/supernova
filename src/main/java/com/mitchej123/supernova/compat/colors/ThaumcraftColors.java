package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.LightColors;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Thaumcraft 4 -- candles, crystals, jars, arcane machinery, and miscellaneous glowing blocks.
 */
public final class ThaumcraftColors {

    private static final String MOD = "Thaumcraft";

    // @formatter:off
    /** Per-meta candle colours (from TC Utils.java, meta 0-15). */
    private static final int[][] CANDLE_COLORS = {
        {14,14,14}, {14, 8, 4}, {11, 5,12}, { 6, 8,12},
        {13,12, 2}, { 4,12, 3}, {13, 8, 9}, { 4, 4, 4},
        { 9, 9, 9}, { 2, 7, 9}, { 7, 3,11}, { 2, 3, 9},
        { 5, 3, 2}, { 3, 5, 2}, {11, 3, 3}, { 2, 2, 2},
    };

    /** Per-meta crystal colours (meta 0-6). */
    private static final int[][] CRYSTAL_COLORS = {
        {7,7,0}, {7,0,0}, {0,0,7}, {0,7,0}, {7,7,7}, {4,4,4}, {10,10,10},
    };
    // @formatter:on

    public static void register() {
        int count = 0;

        // blockCandle -- 16 per-meta colours
        count += ColorRegistrationHelper.registerPerMeta(MOD, "blockCandle", CANDLE_COLORS);

        // blockCrystal -- 7 per-meta colours
        count += ColorRegistrationHelper.registerPerMeta(MOD, "blockCrystal", CRYSTAL_COLORS);

        // blockJar -- per-meta
        Block jar = GameRegistry.findBlock(MOD, "blockJar");
        if (jar != null) {
            LightColorRegistry.register(jar, 0, LightColors.DIM_GRAY);
            LightColorRegistry.register(jar, 1, 0, 7, 5);
            LightColorRegistry.register(jar, 2, LightColors.DIM_GRAY);
            LightColorRegistry.register(jar, 3, LightColors.DIM_GRAY);
            count += 4;
        }

        // Uniform blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "blockArcaneFurnace", 15, 9, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "blockHole", LightColors.DYE_LIGHT_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "blockEldritchNothing", 3, 3, 3);

        // blockAiry -- specific metas only
        Block airy = GameRegistry.findBlock(MOD, "blockAiry");
        if (airy != null) {
            LightColorRegistry.register(airy, 0, 8, 8, 8);
            LightColorRegistry.register(airy, 1, 10, 15, 15);
            LightColorRegistry.register(airy, 5, 8, 8, 8);
            LightColorRegistry.register(airy, 10, 10, 15, 15);
            LightColorRegistry.register(airy, 11, 5, 0, 9);
            count += 5;
        }

        // blockCustomPlant -- specific metas only
        Block plant = GameRegistry.findBlock(MOD, "blockCustomPlant");
        if (plant != null) {
            LightColorRegistry.register(plant, 2, 5, 10, 10);
            LightColorRegistry.register(plant, 3, 8, 4, 0);
            LightColorRegistry.register(plant, 5, 6, 4, 10);
            count += 3;
        }

        // blockCustomOre -- per-meta
        Block ore = GameRegistry.findBlock(MOD, "blockCustomOre");
        if (ore != null) {
            LightColorRegistry.register(ore, 1, 8, 8, 0);
            LightColorRegistry.register(ore, 2, 8, 0, 0);
            LightColorRegistry.register(ore, 3, 0, 0, 8);
            LightColorRegistry.register(ore, 4, 0, 8, 0);
            LightColorRegistry.register(ore, 5, 8, 5, 6);
            LightColorRegistry.register(ore, 6, 0, 8, 8);
            count += 6;
        }

        // blockMetalDevice -- specific metas only
        Block metalDevice = GameRegistry.findBlock(MOD, "blockMetalDevice");
        if (metalDevice != null) {
            LightColorRegistry.register(metalDevice, 8, 0, 8, 0);
            LightColorRegistry.register(metalDevice, 13, 8, 5, 6);
            count += 2;
        }

        // blockTaintFibres -- specific meta only
        Block taintFibres = GameRegistry.findBlock(MOD, "blockTaintFibres");
        if (taintFibres != null) {
            LightColorRegistry.register(taintFibres, 2, 5, 0, 8);
            count++;
        }

        if (count > 0) {
            Supernova.LOG.info("Registered {} Thaumcraft light colors", count);
        }
    }

    private ThaumcraftColors() {}
}
