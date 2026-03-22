package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * OpenComputers -- computer and peripheral blocks.
 */
public final class OpenComputersColors {

    private static final String MOD = "OpenComputers";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "assembler", 0, 5, 5);
        count += ColorRegistrationHelper.registerBlock(MOD, "capacitor", 0, 5, 2);
        count += ColorRegistrationHelper.registerBlock(MOD, "carpetedCapacitor", 0, 5, 2);
        count += ColorRegistrationHelper.registerBlock(MOD, "geolyzer", 3, 2, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "hologram1", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "hologram2", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "powerDistributor", 5, 5, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "screen1", LightColors.BRIGHT_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "screen2", LightColors.BRIGHT_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "screen3", LightColors.BRIGHT_GRAY);

        if (count > 0) {
            Supernova.LOG.info("Registered {} OpenComputers light colors", count);
        }
    }

    private OpenComputersColors() {}
}
