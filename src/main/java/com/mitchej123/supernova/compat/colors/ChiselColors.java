package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.LightColors;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Chisel -- decorative blocks, torches, jack-o-lanterns, and dye-colored circuits.
 */
public final class ChiselColors {

    private static final String MOD = "chisel";

    public static void register() {
        int count = 0;

        // Uniform blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "glowstone", LightColors.GOLDEN);
        count += ColorRegistrationHelper.registerBlock(MOD, "lavastone", 14, 8, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "froglight", LightColors.GOLDEN);
        count += ColorRegistrationHelper.registerBlock(MOD, "holystone", 3, 3, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "beacon", LightColors.DYE_WHITE);

        // Torch variants (torch1 through torch10)
        for (int i = 1; i <= 10; i++) {
            count += ColorRegistrationHelper.registerBlock(MOD, "torch" + i, LightColors.WARM_AMBER);
        }

        // Jack-o-lantern variants (jackolantern1 through jackolantern16)
        for (int i = 1; i <= 16; i++) {
            count += ColorRegistrationHelper.registerBlock(MOD, "jackolantern" + i, 15, 13, 11);
        }

        // Per-meta: lantern (6 variants)
        Block lantern = GameRegistry.findBlock(MOD, "lantern");
        if (lantern != null) {
            LightColorRegistry.register(lantern, 0, 15, 12, 5);
            LightColorRegistry.register(lantern, 1, LightColors.GOLDEN);
            LightColorRegistry.register(lantern, 2, LightColors.DYE_RED);
            LightColorRegistry.register(lantern, 3, LightColors.BRIGHT_CYAN);
            LightColorRegistry.register(lantern, 4, 7, 0, 7);
            LightColorRegistry.register(lantern, 5, LightColors.DYE_PURPLE);
            count += 6;
        }

        // Per-meta: dye-colored dim blocks (16 metas, dye order -> wool remap)
        count += ColorRegistrationHelper.registerDyeRemapped(MOD, "futuraCircuit", LightColors.DIM_DYE_PALETTE);
        count += ColorRegistrationHelper.registerDyeRemapped(MOD, "hexPlating", LightColors.DIM_DYE_PALETTE);
        count += ColorRegistrationHelper.registerDyeRemapped(MOD, "hexLargePlating", LightColors.DIM_DYE_PALETTE);

        if (count > 0) {
            Supernova.LOG.info("Registered {} Chisel light colors", count);
        }
    }

    private ChiselColors() {}
}
