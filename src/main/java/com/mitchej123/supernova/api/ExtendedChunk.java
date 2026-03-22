package com.mitchej123.supernova.api;

/**
 * Supernova extension of {@code Chunk}, injected at runtime.
 */
public interface ExtendedChunk {

    /**
     * Returns {@code true} if initial light propagation has completed for this chunk. Light values are unreliable until this returns {@code true}.
     */
    boolean isLightReady();
}
