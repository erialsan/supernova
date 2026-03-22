package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.LightColors;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Twilight Forest -- fireflies, portals, and enchanted blocks.
 */
public final class TwilightForestColors {

    private static final String MOD = "TwilightForest";

    public static void register() {
        int count = 0;

        // Uniform blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "tile.TFFirefly", LightColors.DYE_ORANGE);
        count += ColorRegistrationHelper.registerBlock(MOD, "tile.TFFireflyJar", LightColors.DYE_ORANGE);
        count += ColorRegistrationHelper.registerBlock(MOD, "tile.TFPortal", LightColors.DYE_PURPLE);
        count += ColorRegistrationHelper.registerBlock(MOD, "tile.CinderFurnaceLit", 13, 12, 10);
        count += ColorRegistrationHelper.registerBlock(MOD, "tile.TrollBer", 15, 14, 12);
        count += ColorRegistrationHelper.registerBlock(MOD, "tile.HugeGloomBlock", 4, 2, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "tile.FieryBlock", 7, 4, 0);

        // Per-meta: ForceField (5 variants)
        Block forceField = GameRegistry.findBlock(MOD, "tile.ForceField");
        if (forceField != null) {
            LightColorRegistry.register(forceField, 0, 2, 0, 1);
            LightColorRegistry.register(forceField, 1, 2, 1, 2);
            LightColorRegistry.register(forceField, 2, 2, 1, 0);
            LightColorRegistry.register(forceField, 3, 0, 2, 0);
            LightColorRegistry.register(forceField, 4, 0, 1, 2);
            count += 5;
        }

        if (count > 0) {
            Supernova.LOG.info("Registered {} Twilight Forest light colors", count);
        }
    }

    private TwilightForestColors() {}
}
