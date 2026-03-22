package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.LightColors;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Et Futurum Requiem -- backported modern-MC blocks.
 */
public final class EtFuturumColors {

    private static final String MOD = "etfuturum";

    public static void register() {
        int count = 0;

        // Uniform (wildcard) blocks
        count += ColorRegistrationHelper.registerBlock(MOD, "end_rod", 15, 10, 15);
        count += ColorRegistrationHelper.registerBlock(MOD, "soul_lantern", LightColors.BRIGHT_CYAN);
        count += ColorRegistrationHelper.registerBlock(MOD, "soul_torch", LightColors.BRIGHT_CYAN);
        count += ColorRegistrationHelper.registerBlock(MOD, "lantern", 15, 12, 5);
        count += ColorRegistrationHelper.registerBlock(MOD, "magma", 5, 1, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "lava_cauldron", LightColors.DYE_ORANGE);
        count += ColorRegistrationHelper.registerBlock(MOD, "lit_blast_furnace", 13, 12, 10);
        count += ColorRegistrationHelper.registerBlock(MOD, "lit_smoker", 13, 12, 10);
        count += ColorRegistrationHelper.registerBlock(MOD, "deepslate_lit_redstone_ore", 5, 1, 1);
        count += ColorRegistrationHelper.registerBlock(MOD, "glow_lichen", 5, 7, 3);
        count += ColorRegistrationHelper.registerBlock(MOD, "beacon", LightColors.BRIGHT_GRAY);
        count += ColorRegistrationHelper.registerBlock(MOD, "brewing_stand", 1, 1, 0);
        count += ColorRegistrationHelper.registerBlock(MOD, "sea_lantern", 10, 15, 15);

        // Per-meta: amethyst_cluster_1 -- small/medium buds
        Block cluster1 = GameRegistry.findBlock(MOD, "amethyst_cluster_1");
        if (cluster1 != null) {
            for (int meta = 0; meta <= 5; meta++) {
                LightColorRegistry.register(cluster1, meta, 1, 0, 1);
                count++;
            }
            for (int meta = 6; meta <= 11; meta++) {
                LightColorRegistry.register(cluster1, meta, 2, 1, 2);
                count++;
            }
        }

        // Per-meta: amethyst_cluster_2 -- large buds/clusters
        Block cluster2 = GameRegistry.findBlock(MOD, "amethyst_cluster_2");
        if (cluster2 != null) {
            for (int meta = 0; meta <= 5; meta++) {
                LightColorRegistry.register(cluster2, meta, 3, 2, 4);
                count++;
            }
            for (int meta = 6; meta <= 11; meta++) {
                LightColorRegistry.register(cluster2, meta, 4, 3, 5);
                count++;
            }
        }

        if (count > 0) {
            Supernova.LOG.info("Registered {} Et Futurum light colors", count);
        }
    }

    private EtFuturumColors() {}
}
