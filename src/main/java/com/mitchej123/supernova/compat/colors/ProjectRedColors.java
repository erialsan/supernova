package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.LightColors;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * ProjectRed Illumination -- lamps and floating light blocks. Meta 0-15 follows wool metadata order (White, Orange, Magenta, ..., Black). With EndlessIDs, bit 4
 * is a lit/inverted flag -- register meta 0-31.
 */
public final class ProjectRedColors {

    public static void register() {
        final Block lamp = GameRegistry.findBlock("ProjRed|Illumination", "projectred.illumination.lamp");
        final Block airous = GameRegistry.findBlock("ProjRed|Illumination", "projectred.illumination.airousLight");
        int count = 0;
        for (int variant = 0; variant < 32; variant++) {
            final int colorIdx = variant & 0xF;
            final int packed = LightColors.BRIGHT_DYE_PALETTE[colorIdx];
            if (lamp != null) {
                LightColorRegistry.register(lamp, variant, packed);
                count++;
            }
            if (airous != null) {
                LightColorRegistry.register(airous, variant, packed);
                count++;
            }
        }
        if (count > 0) {
            Supernova.LOG.info("Registered {} ProjectRed Illumination light colors", count);
        }
    }

    private ProjectRedColors() {}
}
