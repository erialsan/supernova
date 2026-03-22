package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Thaumic Exploration -- addon jars and candles.
 */
public final class ThaumicExplorationColors {

    private static final String MOD = "ThaumicExploration";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "floatCandle", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock(MOD, "boundJar", LightColors.DIM_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "thinkTankJar", LightColors.DIM_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "trashJar", LightColors.DIM_GRAY);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Thaumic Exploration light colors", count);
        }
    }

    private ThaumicExplorationColors() {}
}
