package com.mitchej123.supernova.config;

import com.mitchej123.supernova.api.LightColors;
import com.mitchej123.supernova.api.PackedColorLight;
import com.mitchej123.supernova.api.TranslucencyRegistry;
import net.minecraft.init.Blocks;

public final class DefaultTranslucency {

    public static void register() {
        // Glass: slight attenuation, no color shift
        TranslucencyRegistry.registerTransmittance(Blocks.glass, 14, 14, 14);
        TranslucencyRegistry.registerTransmittance(Blocks.glass_pane, 14, 14, 14);

        // Stained glass: per-dye color filter (wool meta order)
        for (int meta = 0; meta < 16; meta++) {
            int packed = LightColors.BRIGHT_DYE_PALETTE[meta];
            int r = PackedColorLight.red(packed), g = PackedColorLight.green(packed), b = PackedColorLight.blue(packed);
            TranslucencyRegistry.registerTransmittance(Blocks.stained_glass, meta, r, g, b);
            TranslucencyRegistry.registerTransmittance(Blocks.stained_glass_pane, meta, r, g, b);
        }

        // Water/ice: slight uniform attenuation
        TranslucencyRegistry.registerTransmittance(Blocks.water, 12, 12, 12);
        TranslucencyRegistry.registerTransmittance(Blocks.flowing_water, 12, 12, 12);
        TranslucencyRegistry.registerTransmittance(Blocks.ice, 12, 12, 12);

        // Foliage: slight attenuation
        TranslucencyRegistry.registerTransmittance(Blocks.leaves, 14, 14, 14);
        TranslucencyRegistry.registerTransmittance(Blocks.leaves2, 14, 14, 14);
        TranslucencyRegistry.registerTransmittance(Blocks.web, 14, 14, 14);

        // Lava: fully transparent (lava emits light, doesn't absorb it)
        TranslucencyRegistry.registerTransmittance(Blocks.lava, 15, 15, 15);
        TranslucencyRegistry.registerTransmittance(Blocks.flowing_lava, 15, 15, 15);
    }

    private DefaultTranslucency() {}
}
