package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Campfire Backport -- campfire variants with distinct flame colors.
 */
public final class CampfireBackportColors {

    private static final String MOD = "campfirebackport";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "campfire", LightColors.DYE_ORANGE);
        count += ColorRegistrationHelper.registerBlock(MOD, "soul_campfire", LightColors.BRIGHT_CYAN);
        count += ColorRegistrationHelper.registerBlock(MOD, "foxfire_campfire", LightColors.VIVID_PURPLE);
        count += ColorRegistrationHelper.registerBlock(MOD, "shadow_campfire", LightColors.WARM_PINK);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Campfire Backport light colors", count);
        }
    }

    private CampfireBackportColors() {}
}
