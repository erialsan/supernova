package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Galaxy Space -- glowing crystals and glowstone variants.
 */
public final class GalaxySpaceColors {

    private static final String MOD = "GalaxySpace";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "barnardaCcrystal", 0, 8, 8);
        count += ColorRegistrationHelper.registerBlock(MOD, "ceresglowstone", 0, 12, 12);
        count += ColorRegistrationHelper.registerBlock(MOD, "enceladusglowstone", 0, 12, 12);
        count += ColorRegistrationHelper.registerBlock(MOD, "ioglowstone", LightColors.GOLDEN);
        count += ColorRegistrationHelper.registerBlock(MOD, "plutoglowstone", 12, 5, 2);
        count += ColorRegistrationHelper.registerBlock(MOD, "proteusglowstone", 0, 12, 12);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Galaxy Space light colors", count);
        }
    }

    private GalaxySpaceColors() {}
}
