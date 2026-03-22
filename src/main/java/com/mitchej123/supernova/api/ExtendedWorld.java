package com.mitchej123.supernova.api;

import net.minecraft.world.chunk.Chunk;

/**
 * Supernova extension of {@code World}, injected at runtime.
 */
public interface ExtendedWorld {

    /**
     * Returns the chunk if loaded, {@code null} otherwise.
     *
     * @param chunkX chunk X coordinate
     * @param chunkZ chunk Z coordinate
     * @return the chunk, or {@code null}
     */
    Chunk supernova$getAnyChunkImmediately(int chunkX, int chunkZ);

    /**
     * Returns {@code true} if the chunk is queued for initial lighting but BFS has not yet completed. Use to defer gameplay logic (e.g. mob spawning) until
     * lighting is ready.
     *
     * @param chunkX chunk X coordinate
     * @param chunkZ chunk Z coordinate
     */
    boolean supernova$hasChunkPendingLight(int chunkX, int chunkZ);
}
