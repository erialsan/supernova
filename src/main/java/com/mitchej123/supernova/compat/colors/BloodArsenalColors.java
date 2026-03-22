package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Blood Arsenal -- blood-themed glowing blocks and torches.
 */
public final class BloodArsenalColors {

    private static final String MOD = "BloodArsenal";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "blood_infused_glowstone", LightColors.DYE_RED);
        count += ColorRegistrationHelper.registerBlock(MOD, "blood_lamp", LightColors.DYE_RED);
        count += ColorRegistrationHelper.registerBlock(MOD, "blood_torch", 14, 0, 0);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Blood Arsenal light colors", count);
        }
    }

    private BloodArsenalColors() {}
}
