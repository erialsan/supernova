package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * Thaumic Bases -- aurelia, braziers, campfires, and crystal blocks/slabs.
 */
public final class ThaumicBasesColors {

    private static final String MOD = "thaumicbases";

    // @formatter:off
    /** Per-meta crystal colours (meta 0-7). Metas 0-6 match TC blockCrystal; meta 7 is purple. */
    private static final int[][] CRYSTAL_COLORS = {
        {7,7,0}, {7,0,0}, {0,0,7}, {0,7,0}, {7,7,7}, {4,4,4}, {10,10,10}, {7,0,7},
    };
    // @formatter:on

    public static void register() {
        int count = 0;

        // Uniform blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "aurelia", 7, 4, 7);
        count += ColorRegistrationHelper.registerBlock(MOD, "aureliaPetal", 4, 2, 4);
        count += ColorRegistrationHelper.registerBlock(MOD, "brazier", 7, 7, 7);
        count += ColorRegistrationHelper.registerBlock(MOD, "campfire", LightColors.WARM_AMBER);
        count += ColorRegistrationHelper.registerBlock(MOD, "pyrofluid", 15, 15, 7);

        // Crystal blocks and slabs -- same per-meta mapping
        count += ColorRegistrationHelper.registerPerMeta(MOD, "crystalBlock", CRYSTAL_COLORS);
        count += ColorRegistrationHelper.registerPerMeta(MOD, "crystalSlab", CRYSTAL_COLORS);
        count += ColorRegistrationHelper.registerPerMeta(MOD, "crystalSlab_full", CRYSTAL_COLORS);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Thaumic Bases light colors", count);
        }
    }

    private ThaumicBasesColors() {}
}
