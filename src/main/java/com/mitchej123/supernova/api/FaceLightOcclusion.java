package com.mitchej123.supernova.api;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Interface for blocks with direction-dependent light opacity. Checked before the precomputed {@code isSideSolid} lookup table.
 */
public interface FaceLightOcclusion {

    /**
     * Returns the light opacity for a specific face of this block.
     *
     * @param meta      block metadata
     * @param direction face being queried (never {@code UNKNOWN})
     * @return 0-255. Use 0 or 1 for transparent faces, {@code getLightOpacity()} for opaque faces.
     */
    int getDirectionalLightOpacity(int meta, ForgeDirection direction);

    /**
     * Returns per-channel absorption for a specific face as packed RGB ({@code 0x0R0G0B}). Default wraps the scalar {@link #getDirectionalLightOpacity} into
     * uniform RGB.
     *
     * @param meta      block metadata
     * @param direction face being queried (never {@code UNKNOWN})
     * @return packed absorption via {@link PackedColorLight#pack}
     */
    default int getDirectionalLightAbsorption(int meta, ForgeDirection direction) {
        final int scalar = getDirectionalLightOpacity(meta, direction);
        return PackedColorLight.pack(scalar, scalar, scalar);
    }
}
