package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Gadomancy -- extended and specialized Thaumcraft jars.
 */
public final class GadomancyColors {

    private static final String MOD = "gadomancy";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "BlockExtendedNodeJar", LightColors.DIM_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "BlockRemoteJar", LightColors.DIM_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "BlockStickyJar", LightColors.DIM_GRAY);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Gadomancy light colors", count);
        }
    }

    private GadomancyColors() {}
}
