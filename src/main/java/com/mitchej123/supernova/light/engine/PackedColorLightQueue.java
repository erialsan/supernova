package com.mitchej123.supernova.light.engine;

import com.mitchej123.supernova.api.PackedColorLight;

/**
 * Queue entry encoding/decoding for RGB light BFS
 * <p>
 * Bit layout:
 * <pre>
 * Bits  0-5:  x (6 bits, 0-63 relative to cache center)
 * Bits  6-11: z (6 bits)
 * Bits 12-27: y (16 bits, cubic-chunks ready)
 * Bits 28-31: R (4 bits)
 * Bits 32-35: G (4 bits)
 * Bits 36-39: B (4 bits)
 * Bits 40-45: direction bitset (6 bits)
 * Bits 46-60: unused (15 bits)
 * Bit  61:    FLAG_WRITE_LEVEL
 * Bit  62:    FLAG_RECHECK_LEVEL
 * Bit  63:    FLAG_HAS_SIDED_TRANSPARENT_BLOCKS
 * </pre>
 */
final class PackedColorLightQueue {

    private PackedColorLightQueue() {}

    static long encodeQueuePackedRGB(final int packedRGB) {
        // Direct bit rearrangement: packed 0x0R0G0B (channels 8 bits apart) -> queue bits 28/32/36 (4 bits apart)
        return ((long) (packedRGB & 0x0F0000) << 12)   // R: bit 16 -> 28
                | ((long) (packedRGB & 0x000F00) << 24)    // G: bit  8 -> 32
                | ((long) (packedRGB & 0x00000F) << 36);   // B: bit  0 -> 36
    }

    static int decodeQueueRGB(final long queueValue) {
        // Direct bit rearrangement: queue bits 28/32/36 -> packed 0x0R0G0B (channels 8 bits apart)
        return (int) ((queueValue >>> 12) & 0x0F0000)   // R: bit 28 -> 16
                | (int) ((queueValue >>> 24) & 0x000F00)    // G: bit 32 ->  8
                | (int) ((queueValue >>> 36) & 0x00000F);   // B: bit 36 ->  0
    }

    /**
     * Per-channel mask: returns 0xF in each channel where a > b, 0x0 otherwise. Used for decrease propagation to identify channels with other contributing sources.
     */
    static int channelMaskWhereGt(final int a, final int b) {
        final int diff = (b | PackedColorLight.SPACER_BITS) - (a & PackedColorLight.ALL_CHANNELS);
        final int bGeA = diff & PackedColorLight.SPACER_BITS;
        final int aGtB = ~bGeA & PackedColorLight.SPACER_BITS;
        return (aGtB >>> 4) * 0xF;
    }
}
