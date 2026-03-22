package com.mitchej123.supernova.light.engine;

import com.mitchej123.supernova.light.SWMRNibbleArray;
import com.mitchej123.supernova.util.SnapshotChunkMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.lang.reflect.Field;

/**
 * Test subclass of SupernovaBlockEngine that exposes internals for cache population and assertions.
 */
class TestableBlockEngine extends SupernovaBlockEngine {

    private static final Field NIBBLE_CACHE_R;
    private static final Field NIBBLE_CACHE_G;
    private static final Field NIBBLE_CACHE_B;

    static {
        try {
            NIBBLE_CACHE_R = SupernovaRGBEngine.class.getDeclaredField("nibbleCacheR");
            NIBBLE_CACHE_R.setAccessible(true);
            NIBBLE_CACHE_G = SupernovaRGBEngine.class.getDeclaredField("nibbleCacheG");
            NIBBLE_CACHE_G.setAccessible(true);
            NIBBLE_CACHE_B = SupernovaRGBEngine.class.getDeclaredField("nibbleCacheB");
            NIBBLE_CACHE_B.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    TestableBlockEngine(World world) {
        super(world, new SnapshotChunkMap());
        this.suppressRenderNotify = true;
    }

    SWMRNibbleArray[] getNibbleCacheR() {
        try { return (SWMRNibbleArray[]) NIBBLE_CACHE_R.get(this); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    SWMRNibbleArray[] getNibbleCacheG() {
        try { return (SWMRNibbleArray[]) NIBBLE_CACHE_G.get(this); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    SWMRNibbleArray[] getNibbleCacheB() {
        try { return (SWMRNibbleArray[]) NIBBLE_CACHE_B.get(this); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    ExtendedBlockStorage[] getSectionCache() {
        return this.sectionCache;
    }

    SWMRNibbleArray[] getNibbleCache() {
        return this.nibbleCache;
    }

    int getChunkSectionIndexOffset() {
        return this.chunkSectionIndexOffset;
    }

    int getCoordinateOffset() {
        return this.coordinateOffset;
    }

    void callSetupEncodeOffset(int centerX, int centerY, int centerZ) {
        this.setupEncodeOffset(centerX, centerY, centerZ);
    }

    void callPerformLightIncrease() {
        this.performLightIncrease();
    }

    void callPerformLightDecrease() {
        this.performLightDecrease();
    }

    void enqueueIncrease(long value) {
        this.appendToIncreaseQueue(value);
    }

    void enqueueDecrease(long value) {
        this.appendToDecreaseQueue(value);
    }

    int getIncreaseQueueLength() {
        return this.increaseQueueInitialLength;
    }

    int getDecreaseQueueLength() {
        return this.decreaseQueueInitialLength;
    }

    int getLightAt(int worldX, int worldY, int worldZ) {
        return this.getLightLevel(worldX, worldY, worldZ);
    }

    void setLightAt(int worldX, int worldY, int worldZ, int packedRGB) {
        this.setLightLevel(worldX, worldY, worldZ, packedRGB);
    }
}
