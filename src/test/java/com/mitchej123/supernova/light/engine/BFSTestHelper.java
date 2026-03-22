package com.mitchej123.supernova.light.engine;

import com.mitchej123.supernova.api.PackedColorLight;
import com.mitchej123.supernova.light.SWMRNibbleArray;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * Utilities for populating engine caches and reading/writing light in BFS tests.
 */
final class BFSTestHelper {

    private static final int RGB_DIR_SHIFT = 40;

    private BFSTestHelper() {}

    /**
     * Initialize the engine for tests centered on chunk (0,0) at worldY=64.
     * Sets up encode offsets so world coords 0-15 map to the center chunk.
     */
    static void setupCenter(TestableBlockEngine engine) {
        engine.callSetupEncodeOffset(7, 64, 7);
    }

    /**
     * Populate a section with air blocks and initialized nibble arrays.
     * chunkX/chunkZ are world chunk coords (not cache-relative).
     */
    static void populateAirSection(TestableBlockEngine engine, int chunkX, int sectionY, int chunkZ) {
        populateSection(engine, chunkX, sectionY, chunkZ, Blocks.air);
    }

    /**
     * Populate a section with a specific block and initialized nibble arrays.
     */
    static void populateSection(TestableBlockEngine engine, int chunkX, int sectionY, int chunkZ, Block block) {
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

        // Initialize nibble arrays for all 3 channels + base cache
        SWMRNibbleArray nibR = new SWMRNibbleArray();
        engine.getNibbleCache()[idx] = nibR;
        engine.getNibbleCacheR()[idx] = nibR;
        engine.getNibbleCacheG()[idx] = new SWMRNibbleArray();
        engine.getNibbleCacheB()[idx] = new SWMRNibbleArray();
    }

    /**
     * Set a specific block at world coordinates in the section cache.
     */
    static void setBlock(TestableBlockEngine engine, int worldX, int worldY, int worldZ, Block block) {
        setBlock(engine, worldX, worldY, worldZ, block, 0);
    }

    static void setBlock(TestableBlockEngine engine, int worldX, int worldY, int worldZ, Block block, int meta) {
        int idx = sectionIndex(engine, worldX >> 4, worldY >> 4, worldZ >> 4);
        ExtendedBlockStorage section = engine.getSectionCache()[idx];
        if (section == null) {
            throw new IllegalStateException("Section not populated at chunk (" + (worldX >> 4) + ", " + (worldY >> 4) + ", " + (worldZ >> 4) + ")");
        }
        section.func_150818_a(worldX & 15, worldY & 15, worldZ & 15, block);
        if (meta != 0) {
            section.setExtBlockMetadata(worldX & 15, worldY & 15, worldZ & 15, meta);
        }
    }

    /**
     * Write RGB light values through the engine's setLightLevel (handles packed cache).
     */
    static void setLight(TestableBlockEngine engine, int worldX, int worldY, int worldZ, int r, int g, int b) {
        engine.setLightAt(worldX, worldY, worldZ, PackedColorLight.pack(r, g, b));
    }

    /**
     * Read packed RGB through the engine's getLightLevel (handles packed cache).
     */
    static int getLight(TestableBlockEngine engine, int worldX, int worldY, int worldZ) {
        return engine.getLightAt(worldX, worldY, worldZ);
    }

    /**
     * Enqueue a light increase at world coords with given RGB level.
     * Sets the nibble values AND enqueues for propagation.
     */
    static void enqueueIncrease(TestableBlockEngine engine, int worldX, int worldY, int worldZ, int r, int g, int b) {
        setLight(engine, worldX, worldY, worldZ, r, g, b);
        int packedRGB = PackedColorLight.pack(r, g, b);
        Block block = getBlockAt(engine, worldX, worldY, worldZ);
        long entry = SupernovaEngine.encodeCoords(worldX, worldZ, worldY, engine.getCoordinateOffset())
            | PackedColorLightQueue.encodeQueuePackedRGB(packedRGB)
            | (((long) SupernovaEngine.ALL_DIRECTIONS_BITSET) << RGB_DIR_SHIFT)
            | SupernovaEngine.sidedFlag(block);
        engine.enqueueIncrease(entry);
    }

    /**
     * Enqueue a light decrease at world coords with given RGB level.
     */
    static void enqueueDecrease(TestableBlockEngine engine, int worldX, int worldY, int worldZ, int r, int g, int b) {
        int packedRGB = PackedColorLight.pack(r, g, b);
        Block block = getBlockAt(engine, worldX, worldY, worldZ);
        long entry = SupernovaEngine.encodeCoords(worldX, worldZ, worldY, engine.getCoordinateOffset())
            | PackedColorLightQueue.encodeQueuePackedRGB(packedRGB)
            | (((long) SupernovaEngine.ALL_DIRECTIONS_BITSET) << RGB_DIR_SHIFT)
            | SupernovaEngine.sidedFlag(block);
        engine.enqueueDecrease(entry);
    }

    private static Block getBlockAt(TestableBlockEngine engine, int worldX, int worldY, int worldZ) {
        int idx = sectionIndex(engine, worldX >> 4, worldY >> 4, worldZ >> 4);
        ExtendedBlockStorage section = engine.getSectionCache()[idx];
        if (section == null) return Blocks.air;
        return section.getBlockByExtId(worldX & 15, worldY & 15, worldZ & 15);
    }

    static int sectionIndex(TestableBlockEngine engine, int chunkX, int sectionY, int chunkZ) {
        return chunkX + 5 * chunkZ + (5 * 5) * sectionY + engine.getChunkSectionIndexOffset();
    }

    private static int localIndex(int worldX, int worldY, int worldZ) {
        return (worldX & 15) | ((worldZ & 15) << 4) | ((worldY & 15) << 8);
    }
}
