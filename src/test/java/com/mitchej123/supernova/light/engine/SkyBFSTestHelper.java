package com.mitchej123.supernova.light.engine;

import com.mitchej123.supernova.api.PackedColorLight;
import com.mitchej123.supernova.light.SWMRNibbleArray;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * Utilities for populating sky engine caches and reading/writing light in BFS tests.
 */
final class SkyBFSTestHelper {

    private static final int RGB_DIR_SHIFT = 40;

    private SkyBFSTestHelper() {}

    static void setupCenter(TestableSkyEngine engine) {
        engine.callSetupEncodeOffset(7, 64, 7);
    }

    static void populateAirSection(TestableSkyEngine engine, int chunkX, int sectionY, int chunkZ) {
        populateSection(engine, chunkX, sectionY, chunkZ, Blocks.air);
    }

    static void populateSection(TestableSkyEngine engine, int chunkX, int sectionY, int chunkZ, Block block) {
        int idx = sectionIndex(engine, chunkX, sectionY, chunkZ);

        ExtendedBlockStorage section = new ExtendedBlockStorage(sectionY << 4, false);
        if (block != Blocks.air) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        section.func_150818_a(x, y, z, block);
                    }
                }
            }
        }
        engine.getSectionCache()[idx] = section;

        SWMRNibbleArray nibR = new SWMRNibbleArray();
        engine.getNibbleCache()[idx] = nibR;
        engine.getNibbleCacheR()[idx] = nibR;
        engine.getNibbleCacheG()[idx] = new SWMRNibbleArray();
        engine.getNibbleCacheB()[idx] = new SWMRNibbleArray();
    }

    static void setBlock(TestableSkyEngine engine, int worldX, int worldY, int worldZ, Block block) {
        int idx = sectionIndex(engine, worldX >> 4, worldY >> 4, worldZ >> 4);
        ExtendedBlockStorage section = engine.getSectionCache()[idx];
        if (section == null) {
            throw new IllegalStateException("Section not populated at chunk (" + (worldX >> 4) + ", " + (worldY >> 4) + ", " + (worldZ >> 4) + ")");
        }
        section.func_150818_a(worldX & 15, worldY & 15, worldZ & 15, block);
    }

    static void setLight(TestableSkyEngine engine, int worldX, int worldY, int worldZ, int r, int g, int b) {
        engine.setLightAt(worldX, worldY, worldZ, PackedColorLight.pack(r, g, b));
    }

    static int getLight(TestableSkyEngine engine, int worldX, int worldY, int worldZ) {
        return engine.getLightAt(worldX, worldY, worldZ);
    }

    static void enqueueIncrease(TestableSkyEngine engine, int worldX, int worldY, int worldZ, int r, int g, int b) {
        setLight(engine, worldX, worldY, worldZ, r, g, b);
        int packedRGB = PackedColorLight.pack(r, g, b);
        Block block = getBlockAt(engine, worldX, worldY, worldZ);
        long entry = SupernovaEngine.encodeCoords(worldX, worldZ, worldY, engine.getCoordinateOffset())
                | PackedColorLightQueue.encodeQueuePackedRGB(packedRGB)
                | (((long) SupernovaEngine.ALL_DIRECTIONS_BITSET) << RGB_DIR_SHIFT)
                | SupernovaEngine.sidedFlag(block);
        engine.enqueueIncrease(entry);
    }

    static void enqueueDecrease(TestableSkyEngine engine, int worldX, int worldY, int worldZ, int r, int g, int b) {
        int packedRGB = PackedColorLight.pack(r, g, b);
        Block block = getBlockAt(engine, worldX, worldY, worldZ);
        long entry = SupernovaEngine.encodeCoords(worldX, worldZ, worldY, engine.getCoordinateOffset())
                | PackedColorLightQueue.encodeQueuePackedRGB(packedRGB)
                | (((long) SupernovaEngine.ALL_DIRECTIONS_BITSET) << RGB_DIR_SHIFT)
                | SupernovaEngine.sidedFlag(block);
        engine.enqueueDecrease(entry);
    }

    private static Block getBlockAt(TestableSkyEngine engine, int worldX, int worldY, int worldZ) {
        int idx = sectionIndex(engine, worldX >> 4, worldY >> 4, worldZ >> 4);
        ExtendedBlockStorage section = engine.getSectionCache()[idx];
        if (section == null) return Blocks.air;
        return section.getBlockByExtId(worldX & 15, worldY & 15, worldZ & 15);
    }

    static int sectionIndex(TestableSkyEngine engine, int chunkX, int sectionY, int chunkZ) {
        return chunkX + 5 * chunkZ + (5 * 5) * sectionY + engine.getChunkSectionIndexOffset();
    }
}
