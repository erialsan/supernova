package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.LightColors;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Thaumic Horizons -- vats, dynamos, soul beacons, glowing clouds, and miscellaneous blocks.
 */
public final class ThaumicHorizonsColors {

    private static final String MOD = "ThaumicHorizons";

    public static void register() {
        int count = 0;

        // Uniform blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "alchemite", 8, 6, 2);
        count += ColorRegistrationHelper.registerBlock(MOD, "crystalDeep", 12, 15, 15);
        count += ColorRegistrationHelper.registerBlock(MOD, "nodeMonitor", 3, 6, 7);
        count += ColorRegistrationHelper.registerBlock(MOD, "soulBeacon", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "synthNode", 5, 8, 8);
        count += ColorRegistrationHelper.registerBlock(MOD, "vat", 8, 6, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "vatInterior", 8, 6, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "vatSolid", 8, 6, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "voidTH", 1, 0, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "essentiaDynamo", 7, 7, 7);
        count += ColorRegistrationHelper.registerBlock(MOD, "visDynamo", 5, 8, 8);
        count += ColorRegistrationHelper.registerBlock(MOD, "soulJar", LightColors.DIM_GRAY);

        // cloudGlowingTH -- specific metas only
        Block cloud = GameRegistry.findBlock(MOD, "cloudGlowingTH");
        if (cloud != null) {
            LightColorRegistry.register(cloud, 1, LightColors.DYE_WHITE);
            LightColorRegistry.register(cloud, 4, 4, 4, 4);
            LightColorRegistry.register(cloud, 5, 15, 4, 2);
            LightColorRegistry.register(cloud, 6, 11, 4, 13);
            LightColorRegistry.register(cloud, 8, 10, 15, 10);
            LightColorRegistry.register(cloud, 9, 15, 14, 4);
            count += 6;
        }

        if (count > 0) {
            Supernova.LOG.info("Registered {} Thaumic Horizons light colors", count);
        }
    }

    private ThaumicHorizonsColors() {}
}
