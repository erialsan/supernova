package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * HBM's Nuclear Tech -- radioactive glows, tritium lamps, balefire, etc.
 */
public final class HBMColors {

    private static final String MOD = "hbm";

    public static void register() {
        int count = 0;

        // Tritium lamps
        count += ColorRegistrationHelper.registerBlock(MOD, "lamp_tritium_green_on", LightColors.DYE_LIME);
        count += ColorRegistrationHelper.registerBlock(MOD, "lamp_tritium_blue_on", LightColors.DYE_LIGHT_BLUE);

        // Nether ores
        count += ColorRegistrationHelper.registerBlock(MOD, "ore_nether_smoldering", LightColors.DYE_YELLOW);
        count += ColorRegistrationHelper.registerBlock(MOD, "ore_nether_coal", 8, 6, 5);

        // Reinforced blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "reinforced_light", LightColors.GOLDEN);
        count += ColorRegistrationHelper.registerBlock(MOD, "reinforced_lamp_on", LightColors.GOLDEN);
        count += ColorRegistrationHelper.registerBlock(MOD, "reinforced_laminate", LightColors.DYE_CYAN);

        // Meteor / magma
        count += ColorRegistrationHelper.registerBlock(MOD, "block_meteor_molten", 5, 1, 0);

        // Demon lamp
        count += ColorRegistrationHelper.registerBlock(MOD, "lamp_demon", LightColors.DYE_CYAN);

        // Radioactive / balefire (yellow-green glow)
        count += ColorRegistrationHelper.registerBlock(MOD, "waste_mycelium", 12, 15, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "mush_block", 12, 15, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "mush_block_stem", 12, 15, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "balefire", 12, 15, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "mush", 6, 8, 0);

        // Fire
        count += ColorRegistrationHelper.registerBlock(MOD, "fire_digamma", LightColors.DYE_RED);

        // Radioactive glass
        count += ColorRegistrationHelper.registerBlock(MOD, "glass_uranium", LightColors.DYE_YELLOW);
        count += ColorRegistrationHelper.registerBlock(MOD, "glass_trinitite", LightColors.DYE_LIME);
        count += ColorRegistrationHelper.registerBlock(MOD, "glass_polonium", LightColors.DYE_RED);
        count += ColorRegistrationHelper.registerBlock(MOD, "glass_ash", LightColors.DYE_BLACK);

        if (count > 0) {
            Supernova.LOG.info("Registered {} HBM Nuclear Tech light colors", count);
        }
    }

    private HBMColors() {}
}
