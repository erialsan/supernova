package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Witchery -- candelabras, magical plants, portals, and miscellaneous glowing blocks.
 */
public final class WitcheryColors {

    private static final String MOD = "witchery";

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "candelabra", LightColors.WARM_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "glintweed", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock(MOD, "glowglobe", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock(MOD, "embermoss", 6, 3, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "leapinglily", 3, 6, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "light", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "spiritportal", 5, 5, 11);
        count += ColorRegistrationHelper.registerBlock(MOD, "tormentportal", 11, 4, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "demonheart", 3, 1, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "alluringskull", 7, 7, 7);
        count += ColorRegistrationHelper.registerBlock(MOD, "mirrorblock", LightColors.BRIGHT_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "mirrorblock2", LightColors.BRIGHT_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "voidbramble", 1, 0, 1);
        count += ColorRegistrationHelper.registerBlock(MOD, "infinityegg", 1, 0, 1);
        count += ColorRegistrationHelper.registerBlock(MOD, "witchesovenburning", 13, 12, 10);
        count += ColorRegistrationHelper.registerBlock(MOD, "distilleryburning", 6, 2, 6);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Witchery light colors", count);
        }
    }

    private WitcheryColors() {}
}
