package com.mitchej123.supernova.api;

import org.junit.jupiter.api.Test;

import static com.mitchej123.supernova.api.PackedColorLight.blue;
import static com.mitchej123.supernova.api.PackedColorLight.green;
import static com.mitchej123.supernova.api.PackedColorLight.pack;
import static com.mitchej123.supernova.api.PackedColorLight.red;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TranslucencyRegistryTest {

    @Test
    void testLookupWildcard() {
        // Wildcard: length-1 array, stored value = absorption + 1
        int absorption = pack(5, 10, 15);
        int[] entry = { absorption + 1 };
        assertEquals(absorption, TranslucencyRegistry.lookupRegistry(entry, 0));
        assertEquals(absorption, TranslucencyRegistry.lookupRegistry(entry, 7));
        assertEquals(absorption, TranslucencyRegistry.lookupRegistry(entry, 15));
    }

    @Test
    void testLookupPerMetaRegistered() {
        int abs0 = pack(1, 2, 3);
        int abs5 = pack(10, 11, 12);
        int[] entry = new int[16];
        entry[0] = abs0 + 1;
        entry[5] = abs5 + 1;

        assertEquals(abs0, TranslucencyRegistry.lookupRegistry(entry, 0));
        assertEquals(abs5, TranslucencyRegistry.lookupRegistry(entry, 5));
    }

    @Test
    void testLookupPerMetaUnregistered() {
        // Unregistered meta has value 0 -> returns -1
        int[] entry = new int[16];
        entry[0] = pack(1, 1, 1) + 1;
        assertEquals(-1, TranslucencyRegistry.lookupRegistry(entry, 3));
    }

    @Test
    void testLookupOutOfBounds() {
        int[] entry = new int[4];
        entry[0] = pack(1, 1, 1) + 1;
        assertEquals(-1, TranslucencyRegistry.lookupRegistry(entry, 10));
    }

    @Test
    void testLookupNegativeMeta() {
        int[] entry = new int[16];
        entry[0] = pack(1, 1, 1) + 1;
        assertEquals(-1, TranslucencyRegistry.lookupRegistry(entry, -1));
    }

    @Test
    void testLookupWildcardIgnoresMeta() {
        // Wildcard entries always return the same value regardless of meta
        int absorption = pack(7, 7, 7);
        int[] entry = { absorption + 1 };
        for (int meta = 0; meta <= 15; meta++) {
            assertEquals(absorption, TranslucencyRegistry.lookupRegistry(entry, meta),
                "meta=" + meta);
        }
        // Even negative/large meta
        assertEquals(absorption, TranslucencyRegistry.lookupRegistry(entry, -5));
        assertEquals(absorption, TranslucencyRegistry.lookupRegistry(entry, 100));
    }

    @Test
    void testLookupPerMetaAllRegistered() {
        int[] entry = new int[16];
        for (int meta = 0; meta < 16; meta++) {
            entry[meta] = pack(meta, 0, 0) + 1;
        }
        for (int meta = 0; meta < 16; meta++) {
            int result = TranslucencyRegistry.lookupRegistry(entry, meta);
            assertEquals(meta, red(result), "meta=" + meta);
            assertEquals(0, green(result), "meta=" + meta);
            assertEquals(0, blue(result), "meta=" + meta);
        }
    }

    @Test
    void testSentinelValueDistinctFromZeroAbsorption() {
        // Zero absorption (fully transparent) is pack(0,0,0)=0, stored as 0+1=1.
        // Unregistered is stored as 0. Verify they don't collide.
        int[] entry = new int[16];
        entry[0] = pack(0, 0, 0) + 1; // registered with zero absorption
        entry[1] = 0; // unregistered

        assertEquals(0, TranslucencyRegistry.lookupRegistry(entry, 0)); // pack(0,0,0) = 0
        assertEquals(-1, TranslucencyRegistry.lookupRegistry(entry, 1));
    }
}
