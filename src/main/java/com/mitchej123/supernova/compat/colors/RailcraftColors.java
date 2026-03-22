package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Railcraft -- lanterns and firestone.
 */
public final class RailcraftColors {

    private static final String MOD = "Railcraft";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "lantern.stone", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock(MOD, "lantern.metal", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock(MOD, "firestone.recharge", 15, 9, 0);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Railcraft light colors", count);
        }
    }

    private RailcraftColors() {}
}
