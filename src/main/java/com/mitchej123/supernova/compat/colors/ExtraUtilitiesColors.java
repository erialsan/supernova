package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Extra Utilities -- chandeliers, magnum torches, colored lightgem, and colored redstone lamps.
 */
public final class ExtraUtilitiesColors {

    private static final String MOD = "ExtraUtilities";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "chandelier", LightColors.WARM_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "magnumTorch", LightColors.WARM_WHITE);

        // color_lightgem -- 16 per-meta colours (wool order)
        count += ColorRegistrationHelper.registerDyed(MOD, "color_lightgem", LightColors.BRIGHT_DYE_PALETTE);

        // color_redstoneLight -- 16 per-meta colours (wool order)
        count += ColorRegistrationHelper.registerDyed(MOD, "color_redstoneLight", LightColors.BRIGHT_DYE_PALETTE);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Extra Utilities light colors", count);
        }
    }

    private ExtraUtilitiesColors() {}
}
