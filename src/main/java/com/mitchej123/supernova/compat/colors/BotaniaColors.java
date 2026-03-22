package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.LightColors;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Botania -- magical flowers, pylons, and decorative blocks.
 */
public final class BotaniaColors {

    private static final String MOD = "Botania";

    public static void register() {
        int count = 0;

        // Uniform blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "manaFlame", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "manaGlass", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "manaGlassPane", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "elfGlass", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "elfGlassPane", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "seaLamp", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "enchanter", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "blazeBlock", LightColors.DYE_YELLOW);
        count += ColorRegistrationHelper.registerBlock(MOD, "bifrost", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "bifrostPerm", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "bifrostPermPane", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock(MOD, "prism", LightColors.DYE_WHITE);

        // Per-meta: livingwood meta 5 only
        Block livingwood = GameRegistry.findBlock(MOD, "livingwood");
        if (livingwood != null) {
            LightColorRegistry.register(livingwood, 5, 4, 8, 0);
            count++;
        }

        // Per-meta: dye-colored full-brightness blocks (16 metas, wool order)
        count += ColorRegistrationHelper.registerDyed(MOD, "shinyFlower", LightColors.BRIGHT_DYE_PALETTE);
        count += ColorRegistrationHelper.registerDyed(MOD, "miniIsland", LightColors.BRIGHT_DYE_PALETTE);

        // Per-meta: dye-colored dim blocks (16 metas, wool order)
        count += ColorRegistrationHelper.registerDyed(MOD, "buriedPetals", LightColors.DIM_DYE_PALETTE);
        count += ColorRegistrationHelper.registerDyed(MOD, "mushroom", LightColors.DIM_DYE_PALETTE);

        // Per-meta: pylon (3 variants)
        Block pylon = GameRegistry.findBlock(MOD, "pylon");
        if (pylon != null) {
            LightColorRegistry.register(pylon, 0, LightColors.DYE_LIGHT_BLUE);
            LightColorRegistry.register(pylon, 1, LightColors.DYE_GREEN);
            LightColorRegistry.register(pylon, 2, LightColors.WARM_PINK);
            count += 3;
        }

        if (count > 0) {
            Supernova.LOG.info("Registered {} Botania light colors", count);
        }
    }

    private BotaniaColors() {}
}
