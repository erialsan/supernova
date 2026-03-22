package com.mitchej123.supernova.config;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColorRegistry;
import com.mitchej123.supernova.api.PackedColorLight;
import net.minecraft.block.Block;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Loads user-defined block RGB light colors from {@code config/supernova-colors.cfg}. Entries override built-in defaults from {@link DefaultColors}.
 */
public final class BlockColorConfig {

    private static final String FILE_NAME = "supernova-colors.cfg";

    private BlockColorConfig() {}

    public static void load(File configDir) {
        final File file = new File(configDir, FILE_NAME);
        if (!file.exists()) {
            writeTemplate(file);
            return;
        }
        loadFile(file);
    }

    public static int reload(File configDir) {
        final File file = new File(configDir, FILE_NAME);
        if (!file.exists()) return 0;
        return loadFile(file);
    }

    /**
     * Dump all current registry entries to the config file, sorted alphabetically.
     *
     * @return number of entries written
     */
    public static int dump(File configDir) {
        final File file = new File(configDir, FILE_NAME);
        final List<String> lines = new ArrayList<>();

        LightColorRegistry.forEach((block, entry) -> {
            final String blockId = (String) Block.blockRegistry.getNameForObject(block);
            if (blockId == null) return;

            if (entry.length == 1) {
                final int packed = entry[0] - 1;
                lines.add(BlockConfigParser.formatEntry(blockId, -1,
                    PackedColorLight.red(packed), PackedColorLight.green(packed), PackedColorLight.blue(packed)));
            } else {
                for (int meta = 0; meta < entry.length; meta++) {
                    if (entry[meta] == 0) continue;
                    final int packed = entry[meta] - 1;
                    lines.add(BlockConfigParser.formatEntry(blockId, meta,
                        PackedColorLight.red(packed), PackedColorLight.green(packed), PackedColorLight.blue(packed)));
                }
            }
        });

        lines.sort(Comparator.naturalOrder());

        try (PrintWriter w = new PrintWriter(file)) {
            writeHeader(w);
            for (String line : lines) {
                w.println(line);
            }
        } catch (IOException e) {
            Supernova.LOG.error("Failed to write {}", FILE_NAME, e);
            return 0;
        }

        Supernova.LOG.info("Dumped {} light color entries to {}", lines.size(), FILE_NAME);
        return lines.size();
    }

    private static int loadFile(File file) {
        int loaded = BlockConfigParser.loadFile(file, FILE_NAME, (block, meta, r, g, b) -> {
            if (meta >= 0) {
                LightColorRegistry.register(block, meta, r, g, b);
            } else {
                LightColorRegistry.register(block, r, g, b);
            }
        });
        if (loaded > 0) {
            Supernova.LOG.info("Loaded {} custom light color(s) from {}", loaded, FILE_NAME);
        }
        return loaded;
    }

    private static void writeTemplate(File file) {
        try (PrintWriter w = new PrintWriter(file)) {
            writeHeader(w);
            w.println("# Examples:");
            w.println("# minecraft:torch = 15, 13, 10");
            w.println("# minecraft:glowstone = 15, 14, 10");
            w.println("# ProjRed|Illumination:projectred.illumination.lamp:0 = 15, 15, 15");
            w.println("#");
            w.println("# Use /supernova colors dump to export all current entries to this file.");
        } catch (IOException e) {
            Supernova.LOG.error("Failed to write default {}", FILE_NAME, e);
        }
    }

    private static void writeHeader(PrintWriter w) {
        w.println("# Supernova Block Light Colors");
        w.println("#");
        w.println("# Format: modid:blockname = r, g, b");
        w.println("#         modid:blockname:meta = r, g, b");
        w.println("#");
        w.println("# r, g, b are 0-15 (light intensity per channel).");
        w.println("# Omit :meta to apply to all metadata values.");
        w.println("# Entries here override built-in defaults.");
        w.println("# Blocks with vanilla light emission but no entry here");
        w.println("# automatically emit white light at vanilla intensity.");
        w.println("#");
    }
}
