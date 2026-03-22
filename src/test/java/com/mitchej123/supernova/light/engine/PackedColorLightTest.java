package com.mitchej123.supernova.light.engine;

import org.junit.jupiter.api.Test;

import static com.mitchej123.supernova.api.PackedColorLight.ALL_CHANNELS;
import static com.mitchej123.supernova.api.PackedColorLight.BLUE_MASK;
import static com.mitchej123.supernova.api.PackedColorLight.GREEN_MASK;
import static com.mitchej123.supernova.api.PackedColorLight.RED_MASK;
import static com.mitchej123.supernova.api.PackedColorLight.anyComponentGreater;
import static com.mitchej123.supernova.api.PackedColorLight.anyNonZero;
import static com.mitchej123.supernova.api.PackedColorLight.blue;
import static com.mitchej123.supernova.api.PackedColorLight.channelPresenceMask;
import static com.mitchej123.supernova.api.PackedColorLight.green;
import static com.mitchej123.supernova.api.PackedColorLight.maxComponent;
import static com.mitchej123.supernova.api.PackedColorLight.pack;
import static com.mitchej123.supernova.api.PackedColorLight.packedMax;
import static com.mitchej123.supernova.api.PackedColorLight.packedSub;
import static com.mitchej123.supernova.api.PackedColorLight.packedSubRGB;
import static com.mitchej123.supernova.api.PackedColorLight.red;
import static com.mitchej123.supernova.api.PackedColorLight.transmittanceToAbsorption;
import static com.mitchej123.supernova.light.engine.PackedColorLightQueue.channelMaskWhereGt;
import static com.mitchej123.supernova.light.engine.PackedColorLightQueue.decodeQueueRGB;
import static com.mitchej123.supernova.light.engine.PackedColorLightQueue.encodeQueuePackedRGB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackedColorLightTest {

    @Test
    void testPackAndExtract() {
        int packed = pack(15, 7, 3);
        assertEquals(15, red(packed));
        assertEquals(7, green(packed));
        assertEquals(3, blue(packed));
    }

    @Test
    void testPackZero() {
        int packed = pack(0, 0, 0);
        assertEquals(0, packed);
        assertEquals(0, red(packed));
        assertEquals(0, green(packed));
        assertEquals(0, blue(packed));
    }

    @Test
    void testPackMax() {
        int packed = pack(15, 15, 15);
        assertEquals(15, red(packed));
        assertEquals(15, green(packed));
        assertEquals(15, blue(packed));
    }

    @Test
    void testPackedSubNoUnderflow() {
        int rgb = pack(10, 8, 6);
        int result = packedSub(rgb, 3);
        assertEquals(7, red(result));
        assertEquals(5, green(result));
        assertEquals(3, blue(result));
    }

    @Test
    void testPackedSubWithUnderflow() {
        int rgb = pack(2, 1, 0);
        int result = packedSub(rgb, 3);
        assertEquals(0, red(result));
        assertEquals(0, green(result));
        assertEquals(0, blue(result));
    }

    @Test
    void testPackedSubPartialUnderflow() {
        int rgb = pack(5, 3, 1);
        int result = packedSub(rgb, 3);
        assertEquals(2, red(result));
        assertEquals(0, green(result));
        assertEquals(0, blue(result));
    }

    @Test
    void testPackedSubZeroOpacity() {
        int rgb = pack(10, 8, 6);
        int result = packedSub(rgb, 0);
        assertEquals(10, red(result));
        assertEquals(8, green(result));
        assertEquals(6, blue(result));
    }

    @Test
    void testPackedMaxBasic() {
        int a = pack(10, 3, 7);
        int b = pack(5, 8, 7);
        int result = packedMax(a, b);
        assertEquals(10, red(result));
        assertEquals(8, green(result));
        assertEquals(7, blue(result));
    }

    @Test
    void testPackedMaxAllZero() {
        assertEquals(0, packedMax(0, 0));
    }

    @Test
    void testPackedMaxSymmetry() {
        int a = pack(3, 12, 0);
        int b = pack(15, 0, 8);
        assertEquals(packedMax(a, b), packedMax(b, a));
        int result = packedMax(a, b);
        assertEquals(15, red(result));
        assertEquals(12, green(result));
        assertEquals(8, blue(result));
    }

    @Test
    void testAnyComponentGreater() {
        assertTrue(anyComponentGreater(pack(5, 0, 0), pack(4, 0, 0)));
        assertTrue(anyComponentGreater(pack(0, 5, 0), pack(0, 4, 0)));
        assertTrue(anyComponentGreater(pack(0, 0, 5), pack(0, 0, 4)));
        assertFalse(anyComponentGreater(pack(4, 4, 4), pack(4, 4, 4)));
        assertFalse(anyComponentGreater(pack(3, 3, 3), pack(4, 4, 4)));
        assertTrue(anyComponentGreater(pack(5, 3, 3), pack(4, 4, 4)));
    }

    @Test
    void testMaxComponent() {
        assertEquals(15, maxComponent(pack(15, 7, 3)));
        assertEquals(12, maxComponent(pack(3, 12, 8)));
        assertEquals(9, maxComponent(pack(0, 0, 9)));
        assertEquals(0, maxComponent(pack(0, 0, 0)));
    }

    @Test
    void testQueueEncoding() {
        int packed = pack(12, 7, 3);
        long encoded = encodeQueuePackedRGB(packed);
        int decoded = decodeQueueRGB(encoded);
        assertEquals(12, red(decoded));
        assertEquals(7, green(decoded));
        assertEquals(3, blue(decoded));
        assertEquals(packed, decoded);
    }

    @Test
    void testPackedSubExhaustive() {
        // Test all combinations of single-channel values
        for (int val = 0; val <= 15; val++) {
            for (int opacity = 0; opacity <= 15; opacity++) {
                int rgb = pack(val, val, val);
                int result = packedSub(rgb, opacity);
                int expected = Math.max(0, val - opacity);
                assertEquals(expected, red(result), "r: val=" + val + " opacity=" + opacity);
                assertEquals(expected, green(result), "g: val=" + val + " opacity=" + opacity);
                assertEquals(expected, blue(result), "b: val=" + val + " opacity=" + opacity);
            }
        }
    }

    @Test
    void testChannelMaskWhereGt() {
        // R > target, G == target, B < target
        int a = pack(10, 5, 2);
        int b = pack(8, 5, 4);
        int mask = channelMaskWhereGt(a, b);
        assertEquals(RED_MASK, mask, "only R should be greater");

        // All greater
        mask = channelMaskWhereGt(pack(10, 10, 10), pack(5, 5, 5));
        assertEquals(ALL_CHANNELS, mask);

        // None greater
        mask = channelMaskWhereGt(pack(3, 3, 3), pack(5, 5, 5));
        assertEquals(0, mask);

        // Equal = not greater
        mask = channelMaskWhereGt(pack(5, 5, 5), pack(5, 5, 5));
        assertEquals(0, mask);
    }

    @Test
    void testChannelMaskWhereGtExhaustive() {
        for (int a = 0; a <= 15; a++) {
            for (int b = 0; b <= 15; b++) {
                int pa = pack(a, 15 - a, a);
                int pb = pack(b, 15 - b, b);
                int mask = channelMaskWhereGt(pa, pb);
                boolean rGt = a > b;
                boolean gGt = (15 - a) > (15 - b);
                boolean bGt = a > b;
                assertEquals(rGt ? RED_MASK : 0, mask & RED_MASK, "r: a=" + a + " b=" + b);
                assertEquals(gGt ? GREEN_MASK : 0, mask & GREEN_MASK, "g: a=" + a + " b=" + b);
                assertEquals(bGt ? BLUE_MASK : 0, mask & BLUE_MASK, "b: a=" + a + " b=" + b);
            }
        }
    }

    @Test
    void testTransmittanceToAbsorption() {
        // Fully transparent -> zero absorption
        int result = transmittanceToAbsorption(pack(15, 15, 15));
        assertEquals(0, red(result));
        assertEquals(0, green(result));
        assertEquals(0, blue(result));

        // Fully opaque -> max absorption
        result = transmittanceToAbsorption(pack(0, 0, 0));
        assertEquals(15, red(result));
        assertEquals(15, green(result));
        assertEquals(15, blue(result));

        // Mixed
        result = transmittanceToAbsorption(pack(10, 5, 0));
        assertEquals(5, red(result));
        assertEquals(10, green(result));
        assertEquals(15, blue(result));
    }

    @Test
    void testTransmittanceToAbsorptionExhaustive() {
        for (int r = 0; r <= 15; r++) {
            for (int g = 0; g <= 15; g++) {
                for (int b = 0; b <= 15; b++) {
                    int result = transmittanceToAbsorption(pack(r, g, b));
                    assertEquals(15 - r, red(result), "r=" + r + " g=" + g + " b=" + b);
                    assertEquals(15 - g, green(result), "r=" + r + " g=" + g + " b=" + b);
                    assertEquals(15 - b, blue(result), "r=" + r + " g=" + g + " b=" + b);
                }
            }
        }
    }

    @Test
    void testPackedSubRGBNonUniform() {
        // Different absorption per channel
        int rgb = pack(10, 8, 6);
        int absorption = pack(3, 5, 8);
        int result = packedSubRGB(rgb, absorption);
        assertEquals(7, red(result));
        assertEquals(3, green(result));
        assertEquals(0, blue(result)); // 6-8 underflows to 0
    }

    @Test
    void testPackedSubRGBZeroAbsorption() {
        int rgb = pack(10, 8, 6);
        int result = packedSubRGB(rgb, pack(0, 0, 0));
        assertEquals(10, red(result));
        assertEquals(8, green(result));
        assertEquals(6, blue(result));
    }

    @Test
    void testPackedSubRGBMaxAbsorption() {
        int rgb = pack(10, 8, 6);
        int result = packedSubRGB(rgb, pack(15, 15, 15));
        assertEquals(0, red(result));
        assertEquals(0, green(result));
        assertEquals(0, blue(result));
    }

    @Test
    void testPackedSubRGBUnderflowMix() {
        // R survives, G exact zero, B underflows
        int rgb = pack(10, 5, 2);
        int absorption = pack(3, 5, 7);
        int result = packedSubRGB(rgb, absorption);
        assertEquals(7, red(result));
        assertEquals(0, green(result));
        assertEquals(0, blue(result));
    }

    @Test
    void testPackedSubRGBExhaustive() {
        // Test all combos for one channel varying against all absorptions
        for (int val = 0; val <= 15; val++) {
            for (int absR = 0; absR <= 15; absR++) {
                for (int absG = 0; absG <= 15; absG++) {
                    int rgb = pack(val, val, val);
                    int absorption = pack(absR, absG, 0);
                    int result = packedSubRGB(rgb, absorption);
                    assertEquals(Math.max(0, val - absR), red(result), "val=" + val + " absR=" + absR + " absG=" + absG);
                    assertEquals(Math.max(0, val - absG), green(result), "val=" + val + " absR=" + absR + " absG=" + absG);
                    assertEquals(val, blue(result), "val=" + val + " absR=" + absR + " absG=" + absG);
                }
            }
        }
    }

    @Test
    void testPackedSubDelegatesToPackedSubRGB() {
        // Verify packedSub(packed, opacity) == packedSubRGB(packed, pack(opacity, opacity, opacity))
        for (int r = 0; r <= 15; r += 3) {
            for (int g = 0; g <= 15; g += 3) {
                for (int b = 0; b <= 15; b += 3) {
                    for (int opacity = 0; opacity <= 15; opacity++) {
                        int rgb = pack(r, g, b);
                        assertEquals(
                                packedSubRGB(rgb, pack(opacity, opacity, opacity)),
                                packedSub(rgb, opacity),
                                "r=" + r + " g=" + g + " b=" + b + " op=" + opacity);
                    }
                }
            }
        }
    }

    @Test
    void testChannelPresenceMask() {
        // All zero -> no channels present
        assertEquals(0, channelPresenceMask(pack(0, 0, 0)));

        // All non-zero -> all channels present
        assertEquals(ALL_CHANNELS, channelPresenceMask(pack(5, 3, 8)));
        assertEquals(ALL_CHANNELS, channelPresenceMask(pack(15, 15, 15)));
        assertEquals(ALL_CHANNELS, channelPresenceMask(pack(1, 1, 1)));

        // Single channel present
        assertEquals(RED_MASK, channelPresenceMask(pack(7, 0, 0)));
        assertEquals(GREEN_MASK, channelPresenceMask(pack(0, 3, 0)));
        assertEquals(BLUE_MASK, channelPresenceMask(pack(0, 0, 12)));

        // Two channels present
        assertEquals(RED_MASK | GREEN_MASK, channelPresenceMask(pack(5, 3, 0)));
        assertEquals(RED_MASK | BLUE_MASK, channelPresenceMask(pack(5, 0, 3)));
        assertEquals(GREEN_MASK | BLUE_MASK, channelPresenceMask(pack(0, 5, 3)));
    }

    @Test
    void testChannelPresenceMaskExhaustive() {
        for (int r = 0; r <= 15; r++) {
            for (int g = 0; g <= 15; g++) {
                for (int b = 0; b <= 15; b++) {
                    int packed = pack(r, g, b);
                    int mask = channelPresenceMask(packed);
                    assertEquals(r != 0 ? RED_MASK : 0, mask & RED_MASK, "r=" + r + " g=" + g + " b=" + b);
                    assertEquals(g != 0 ? GREEN_MASK : 0, mask & GREEN_MASK, "r=" + r + " g=" + g + " b=" + b);
                    assertEquals(b != 0 ? BLUE_MASK : 0, mask & BLUE_MASK, "r=" + r + " g=" + g + " b=" + b);
                }
            }
        }
    }

    @Test
    void testFullQueueEntryRoundTrip() {
        final int x = 15, z = 20, y = 200;
        final int encodeOffset = 0;
        final int r = 12, g = 7, b = 3;
        final int directionBitset = 0b101010; // +x, +z, +y
        final int rgbDirShift = 40;

        long entry = SupernovaEngine.encodeCoords(x, z, y, encodeOffset) | PackedColorLightQueue.encodeQueuePackedRGB(pack(r, g, b)) | ((long) directionBitset
                << rgbDirShift);

        // Decode coordinates
        assertEquals(x, (int) (entry & 0x3F));
        assertEquals(z, (int) ((entry >>> 6) & 0x3F));
        assertEquals(y, (int) ((entry >>> 12) & 0xFFFF));

        // Decode RGB
        int decoded = decodeQueueRGB(entry);
        assertEquals(r, red(decoded));
        assertEquals(g, green(decoded));
        assertEquals(b, blue(decoded));

        // Decode direction
        assertEquals(directionBitset, (int) ((entry >>> rgbDirShift) & 0x3F));
    }

    @Test
    void testQueueEncodingBoundaryValues() {
        final int rgbDirShift = 40;

        // Max coordinates
        long entry = SupernovaEngine.encodeCoords(63, 63, 65535, 0) | PackedColorLightQueue.encodeQueuePackedRGB(pack(15, 15, 15)) | ((long) 0x3F
                << rgbDirShift) | SupernovaEngine.FLAG_WRITE_LEVEL | SupernovaEngine.FLAG_RECHECK_LEVEL | SupernovaEngine.FLAG_HAS_SIDED_TRANSPARENT_BLOCKS;

        assertEquals(63, (int) (entry & 0x3F));
        assertEquals(63, (int) ((entry >>> 6) & 0x3F));
        assertEquals(65535, (int) ((entry >>> 12) & 0xFFFF));
        int rgb = decodeQueueRGB(entry);
        assertEquals(15, red(rgb));
        assertEquals(15, green(rgb));
        assertEquals(15, blue(rgb));
        assertEquals(0x3F, (int) ((entry >>> rgbDirShift) & 0x3F));
        assertTrue((entry & SupernovaEngine.FLAG_WRITE_LEVEL) != 0);
        assertTrue((entry & SupernovaEngine.FLAG_RECHECK_LEVEL) != 0);
        assertTrue((entry & SupernovaEngine.FLAG_HAS_SIDED_TRANSPARENT_BLOCKS) != 0);

        // Min coordinates, zero RGB, no flags
        entry = SupernovaEngine.encodeCoords(0, 0, 0, 0) | PackedColorLightQueue.encodeQueuePackedRGB(pack(0, 0, 0));
        assertEquals(0, (int) (entry & 0x3F));
        assertEquals(0, (int) ((entry >>> 6) & 0x3F));
        assertEquals(0, (int) ((entry >>> 12) & 0xFFFF));
        rgb = decodeQueueRGB(entry);
        assertEquals(0, red(rgb));
        assertEquals(0, green(rgb));
        assertEquals(0, blue(rgb));
    }

    @Test
    void testQueueFieldsNoOverlap() {
        final int rgbDirShift = 40;

        // Set only coordinates -- no RGB or flags should be set
        long coordsOnly = SupernovaEngine.encodeCoords(63, 63, 65535, 0);
        assertEquals(0, decodeQueueRGB(coordsOnly));
        assertEquals(0, (int) ((coordsOnly >>> rgbDirShift) & 0x3F));

        // Set only RGB -- no coordinates or flags should be set
        long rgbOnly = PackedColorLightQueue.encodeQueuePackedRGB(pack(15, 15, 15));
        assertEquals(0, (int) (rgbOnly & 0x3F)); // x
        assertEquals(0, (int) ((rgbOnly >>> 6) & 0x3F)); // z
        assertEquals(0, (int) ((rgbOnly >>> 12) & 0xFFFF)); // y
        assertEquals(0, (int) ((rgbOnly >>> rgbDirShift) & 0x3F)); // direction

        // Set only direction -- no coordinates or RGB should be set
        long dirOnly = ((long) 0x3F << rgbDirShift);
        assertEquals(0, (int) (dirOnly & SupernovaEngine.COORD_MASK));
        assertEquals(0, decodeQueueRGB(dirOnly));

        // Set only flags -- no coordinates, RGB, or direction should be set
        long flagsOnly = SupernovaEngine.FLAG_WRITE_LEVEL | SupernovaEngine.FLAG_RECHECK_LEVEL | SupernovaEngine.FLAG_HAS_SIDED_TRANSPARENT_BLOCKS;
        assertEquals(0, (int) (flagsOnly & SupernovaEngine.COORD_MASK));
        assertEquals(0, decodeQueueRGB(flagsOnly));
        assertEquals(0, (int) ((flagsOnly >>> rgbDirShift) & 0x3F));
    }

    @Test
    void testAnyNonZero() {
        assertFalse(anyNonZero(0));
        assertTrue(anyNonZero(pack(1, 0, 0)));
        assertTrue(anyNonZero(pack(0, 1, 0)));
        assertTrue(anyNonZero(pack(0, 0, 1)));
        assertTrue(anyNonZero(pack(15, 15, 15)));
    }

    @Test
    void testPackedMaxExhaustive() {
        for (int a = 0; a <= 15; a++) {
            for (int b = 0; b <= 15; b++) {
                int pa = pack(a, 15 - a, a);
                int pb = pack(b, 15 - b, b);
                int result = packedMax(pa, pb);
                assertEquals(Math.max(a, b), red(result), "r: a=" + a + " b=" + b);
                assertEquals(Math.max(15 - a, 15 - b), green(result), "g: a=" + a + " b=" + b);
                assertEquals(Math.max(a, b), blue(result), "b: a=" + a + " b=" + b);
            }
        }
    }
}
