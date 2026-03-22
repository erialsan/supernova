package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Galacticraft -- arc lamps, torches, breathable air, and planetary flora.
 */
public final class GalacticraftColors {

    public static void register() {
        int count = 0;

        // GalacticraftCore
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.arclamp", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.brightAir", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.brightBreathableAir", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.unlitTorchLit", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.unlitTorchLit_Stone", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.unlitTorch", 3, 2, 1);
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.unlitTorch_Stone", 3, 2, 1);
        count += ColorRegistrationHelper.registerBlock("GalacticraftCore", "tile.glowstoneTorch", LightColors.GOLDEN);

        // GalacticraftMars
        count += ColorRegistrationHelper.registerBlock("GalacticraftMars", "tile.cavernVines", 0, 12, 10);
        count += ColorRegistrationHelper.registerBlock("GalacticraftMars", "tile.sludge", 3, 8, 2);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Galacticraft light colors", count);
        }
    }

    private GalacticraftColors() {}
}
