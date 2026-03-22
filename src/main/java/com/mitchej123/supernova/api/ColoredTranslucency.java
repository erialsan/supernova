package com.mitchej123.supernova.api;

/**
 * Interface for blocks with per-channel light absorption based on metadata alone. For position-dependent absorption, implement
 * {@link PositionalColoredTranslucency} instead. Build return values with {@link PackedColorLight#pack(int, int, int)}.
 *
 * @see TranslucencyRegistry
 */
public interface ColoredTranslucency {

    /**
     * Returns per-channel transmittance as packed RGB (0-15 per channel, 15=fully transparent, 0=fully opaque).
     *
     * @param meta block metadata
     * @return packed RGB transmittance via {@link PackedColorLight#pack}
     */
    int getColoredTransmittance(int meta);
}
