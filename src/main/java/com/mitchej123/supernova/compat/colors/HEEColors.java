package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Hardcore Ender Expansion -- ravaged bricks, lasers, portals, and essence altars.
 */
public final class HEEColors {

    private static final String MOD = "HardcoreEnderExpansion";

    // @formatter:off
    /** Per-meta essence altar colours (meta 0-3). */
    private static final int[][] ESSENCE_ALTAR_COLORS = {
        {6,6,6}, {5,2,6}, {6,4,1}, {2,2,6},
    };
    // @formatter:on

    public static void register() {
        int count = 0;

        count += ColorRegistrationHelper.registerBlock(MOD, "ravaged_brick_glow", 15, 3, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "laser_beam", 10, 2, 15);
        count += ColorRegistrationHelper.registerBlock(MOD, "obsidian_special_glow", 8, 2, 15);
        count += ColorRegistrationHelper.registerBlock(MOD, "temple_end_portal", 8, 2, 15);
        count += ColorRegistrationHelper.registerBlock(MOD, "transport_beacon", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "enhanced_brewing_stand_block", LightColors.DYE_BLACK);

        // essence_altar -- 4 per-meta colours
        count += ColorRegistrationHelper.registerPerMeta(MOD, "essence_altar", ESSENCE_ALTAR_COLORS);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Hardcore Ender Expansion light colors", count);
        }
    }

    private HEEColors() {}
}
