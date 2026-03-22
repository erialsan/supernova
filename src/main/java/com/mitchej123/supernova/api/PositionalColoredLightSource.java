package com.mitchej123.supernova.api;

import net.minecraft.world.IBlockAccess;

/**
 * Extension of {@link ColoredLightSource} for blocks whose colored emission varies by position or neighbors.
 *
 * @see LightColorRegistry
 */
public interface PositionalColoredLightSource extends ColoredLightSource {

    /**
     * Returns this block's colored light emission at the given position. Build the return value with {@link PackedColorLight#pack(int, int, int)}.
     *
     * @param world block access
     * @param meta  block metadata
     * @param x     block x
     * @param y     block y
     * @param z     block z
     * @return packed RGB via {@link PackedColorLight#pack}, or 0 for no emission
     */
    int getColoredLightEmission(IBlockAccess world, int meta, int x, int y, int z);
}
