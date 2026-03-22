package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;

/**
 * Natura -- glowshrooms and thornvines.
 */
public final class NaturaColors {

    private static final String MOD = "Natura";

    // @formatter:off
    private static final int[][] GLOWSHROOM_COLORS = {
        {0, 9, 1}, {7, 0, 8}, {0, 3, 9},
    };
    // @formatter:on

    public static void register() {
        int count = 0;

        // Per-meta: Glowshroom (3 variants)
        count += ColorRegistrationHelper.registerPerMeta(MOD, "Glowshroom", GLOWSHROOM_COLORS);

        // Uniform blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "blueGlowshroom", 0, 3, 9);
        count += ColorRegistrationHelper.registerBlock(MOD, "greenGlowshroom", 0, 9, 1);
        count += ColorRegistrationHelper.registerBlock(MOD, "purpleGlowshroom", 7, 0, 8);
        count += ColorRegistrationHelper.registerBlock(MOD, "Thornvines", 9, 7, 0);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Natura light colors", count);
        }
    }

    private NaturaColors() {}
}
