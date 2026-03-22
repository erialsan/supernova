package com.mitchej123.supernova.api;

import com.mitchej123.supernova.light.engine.MCBootstrap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.BitSet;


import static com.mitchej123.supernova.api.PackedColorLight.blue;
import static com.mitchej123.supernova.api.PackedColorLight.green;
import static com.mitchej123.supernova.api.PackedColorLight.red;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LightColorRegistryTest {

    private static Unsafe unsafe;

    @BeforeAll
    static void bootstrap() {
        MCBootstrap.init();
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void resetRegistry() throws Exception {
        // Clear static state between tests
        Field regField = LightColorRegistry.class.getDeclaredField("REGISTRY_BY_ID");
        regField.setAccessible(true);
        regField.set(null, new int[0][]);

        Field bitField = LightColorRegistry.class.getDeclaredField("HAS_ENTRY");
        bitField.setAccessible(true);
        ((BitSet) bitField.get(null)).clear();
    }

    @Test
    void testWildcardRegistration() {
        Block block = Blocks.stone;
        LightColorRegistry.register(block, 10, 5, 3);

        // Same result for all metas
        for (int meta = 0; meta <= 15; meta++) {
            int emission = LightColorRegistry.getPackedEmissionNoWorld(block, meta);
            assertEquals(10, red(emission), "red at meta " + meta);
            assertEquals(5, green(emission), "green at meta " + meta);
            assertEquals(3, blue(emission), "blue at meta " + meta);
        }
    }

    @Test
    void testPerMetaRegistration() {
        Block block = Blocks.stone;
        LightColorRegistry.register(block, 0, 15, 0, 0);  // meta 0 = red
        LightColorRegistry.register(block, 5, 0, 15, 0);  // meta 5 = green

        int m0 = LightColorRegistry.getPackedEmissionNoWorld(block, 0);
        assertEquals(15, red(m0));
        assertEquals(0, green(m0));

        int m5 = LightColorRegistry.getPackedEmissionNoWorld(block, 5);
        assertEquals(0, red(m5));
        assertEquals(15, green(m5));

        // Unregistered meta falls through to vanilla fallback (stone has lightValue=0 -> no emission)
        int m3 = LightColorRegistry.getPackedEmissionNoWorld(block, 3);
        assertEquals(0, m3);
    }

    @Test
    void testPerMetaOverridesWildcard() {
        Block block = Blocks.stone;
        LightColorRegistry.register(block, 5, 5, 5);      // wildcard
        LightColorRegistry.register(block, 3, 15, 0, 0);  // override meta 3

        // Meta 3 should return the override
        int m3 = LightColorRegistry.getPackedEmissionNoWorld(block, 3);
        assertEquals(15, red(m3));
        assertEquals(0, green(m3));

        // Other metas should return the wildcard (expanded)
        int m0 = LightColorRegistry.getPackedEmissionNoWorld(block, 0);
        assertEquals(5, red(m0));
        assertEquals(5, green(m0));
        assertEquals(5, blue(m0));
    }

    @Test
    void testHasExplicitEntry() {
        assertFalse(LightColorRegistry.hasExplicitEntry(Blocks.stone));
        LightColorRegistry.register(Blocks.stone, 10, 10, 10);
        assertTrue(LightColorRegistry.hasExplicitEntry(Blocks.stone));

        // By ID
        int id = Block.getIdFromBlock(Blocks.stone);
        assertTrue(LightColorRegistry.hasExplicitEntry(id));
    }

    @Test
    void testVanillaFallback() {
        // Create a block with lightValue=10 via Unsafe
        Block emitter = createBlockWithLightValue(10);
        int emission = LightColorRegistry.getPackedEmissionNoWorld(emitter, 0);
        // Vanilla fallback = white at intensity 10
        assertEquals(10, red(emission));
        assertEquals(10, green(emission));
        assertEquals(10, blue(emission));
    }

    @Test
    void testNoEmission() {
        // Stone has lightValue=0, no registry entry -> 0
        int emission = LightColorRegistry.getPackedEmissionNoWorld(Blocks.stone, 0);
        assertEquals(0, emission);
    }

    private Block createBlockWithLightValue(int lightValue) {
        try {
            Block block = (Block) unsafe.allocateInstance(Block.class);
            Field f = Block.class.getDeclaredField("lightValue");
            unsafe.putInt(block, unsafe.objectFieldOffset(f), lightValue);
            return block;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
