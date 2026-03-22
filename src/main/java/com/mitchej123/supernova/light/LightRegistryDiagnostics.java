package com.mitchej123.supernova.light;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.ColoredLightSource;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.PositionalColoredLightSource;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Startup diagnostics for the light color registry. When {@code -Dsupernova.debug=true} is set, scans all registered blocks and writes unregistered emitters to
 * {@code config/supernova-unregistered.cfg}.
 */
public final class LightRegistryDiagnostics {

    private static final String FILE_NAME = "supernova-unregistered.cfg";

    private LightRegistryDiagnostics() {}

    /**
     * Scan all registered blocks for unregistered emitters and write them to {@code config/supernova-unregistered.cfg}. Gated behind
     * {@code -Dsupernova.debug=true}. {@link ColoredLightSource} blocks without registry entries always produce a log warning regardless of the debug flag.
     */
    @SuppressWarnings("unchecked")
    public static void dumpUnregistered(File configDir) {
        final List<String> vanillaLines = new ArrayList<>();
        final List<String> coloredLines = new ArrayList<>();
        int coloredMissing = 0;

        for (final Block block : (Iterable<Block>) GameData.getBlockRegistry()) {
            if (LightColorRegistry.hasExplicitEntry(block)) continue;
            final String name = Block.blockRegistry.getNameForObject(block);
            if (block instanceof PositionalColoredLightSource) {
                Supernova.LOG.warn("Block {} implements PositionalColoredLightSource but has no registry entry", name);
                coloredLines.add("# WARNING: " + name + " implements PositionalColoredLightSource but has no registry entry");
                coloredMissing++;
            } else if (block instanceof ColoredLightSource) {
                Supernova.LOG.warn("Block {} implements ColoredLightSource but has no registry entry", name);
                coloredLines.add("# WARNING: " + name + " implements ColoredLightSource but has no registry entry");
                coloredMissing++;
            } else if (block.getLightValue() > 0) {
                final int level = block.getLightValue();
                vanillaLines.add("# " + name + " = " + level + ", " + level + ", " + level);
            }
        }

        if (coloredMissing > 0) {
            Supernova.LOG.warn("{} ColoredLightSource blocks missing registry entries", coloredMissing);
        }

        if (!Boolean.getBoolean("supernova.debug")) return;

        vanillaLines.sort(Comparator.naturalOrder());
        coloredLines.sort(Comparator.naturalOrder());

        final int total = vanillaLines.size() + coloredLines.size();
        if (total == 0) return;

        final File file = new File(configDir, FILE_NAME);
        try (PrintWriter w = new PrintWriter(file)) {
            w.println("# Supernova Unregistered Emitters");
            w.println("#");
            w.println("# Blocks that emit light but have no entry in the Supernova color registry.");
            w.println("# Uncomment and edit lines below, then copy them into supernova-colors.cfg.");
            w.println("#");
            w.println("# Format: modid:blockname = r, g, b");
            w.println("#         modid:blockname:meta = r, g, b");
            w.println("#");
            w.println("# r, g, b are 0-15 (light intensity per channel).");
            w.println("#");
            if (!vanillaLines.isEmpty()) {
                w.println("# --- Vanilla emitters (defaulting to white at vanilla intensity) ---");
                for (String line : vanillaLines) {
                    w.println(line);
                }
                w.println();
            }
            if (!coloredLines.isEmpty()) {
                w.println("# --- ColoredLightSource blocks missing registry entries ---");
                for (String line : coloredLines) {
                    w.println(line);
                }
            }
        } catch (IOException e) {
            Supernova.LOG.error("Failed to write {}", FILE_NAME, e);
            return;
        }

        Supernova.LOG.info("Wrote {} unregistered emitters to {}", total, FILE_NAME);
    }
}
