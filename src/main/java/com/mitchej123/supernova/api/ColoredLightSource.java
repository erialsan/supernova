package com.mitchej123.supernova.api;

/**
 * Interface for blocks that emit colored light based on metadata alone. For position-dependent emission, implement {@link PositionalColoredLightSource}
 * instead.
 *
 * @see LightColorRegistry
 */
public interface ColoredLightSource {

    /**
     * Returns this block's colored light emission for the given metadata. Build the return value with {@link PackedColorLight#pack(int, int, int)}.
     *
     * @param meta block metadata
     * @return packed RGB via {@link PackedColorLight#pack}, or 0 for no emission
     */
    int getColoredLightEmission(int meta);
}
