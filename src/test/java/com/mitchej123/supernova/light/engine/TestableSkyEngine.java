package com.mitchej123.supernova.light.engine;

import com.mitchej123.supernova.light.SWMRNibbleArray;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * Test subclass of SupernovaSkyEngine that exposes internals for cache population and assertions.
 */
class TestableSkyEngine extends SupernovaSkyEngine {

    TestableSkyEngine(World world) {
        super(world);
        this.suppressRenderNotify = true;
    }

    SWMRNibbleArray[] getNibbleCacheR() {
        return this.nibbleCacheR;
    }

    SWMRNibbleArray[] getNibbleCacheG() {
        return this.nibbleCacheG;
    }

    SWMRNibbleArray[] getNibbleCacheB() {
        return this.nibbleCacheB;
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

    int getLightAt(int worldX, int worldY, int worldZ) {
        return this.getLightLevel(worldX, worldY, worldZ);
    }

    void setLightAt(int worldX, int worldY, int worldZ, int packedRGB) {
        this.setLightLevel(worldX, worldY, worldZ, packedRGB);
    }
}
