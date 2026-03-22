package com.mitchej123.supernova.light.engine;

import net.minecraft.init.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.mitchej123.supernova.api.PackedColorLight.blue;
import static com.mitchej123.supernova.api.PackedColorLight.green;
import static com.mitchej123.supernova.api.PackedColorLight.red;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SkyEngineBFSTest {

    private TestableSkyEngine engine;

    @BeforeAll
    static void bootstrap() {
        MCBootstrap.init();
    }

    @BeforeEach
    void setup() {
        engine = new TestableSkyEngine(MCBootstrap.getServerWorld());
        SkyBFSTestHelper.setupCenter(engine);
        SkyBFSTestHelper.populateAirSection(engine, 0, 4, 0);
    }

    @Test
    void testSingleWhiteSkyFalloff() {
        SkyBFSTestHelper.populateAirSection(engine, 1, 4, 0);
        SkyBFSTestHelper.populateAirSection(engine, -1, 4, 0);
        SkyBFSTestHelper.populateAirSection(engine, 0, 4, 1);
        SkyBFSTestHelper.populateAirSection(engine, 0, 4, -1);
        SkyBFSTestHelper.populateAirSection(engine, 0, 5, 0);
        SkyBFSTestHelper.populateAirSection(engine, 0, 3, 0);

        SkyBFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        assertLight(8, 68, 8, 15, 15, 15);
        assertLight(9, 68, 8, 14, 14, 14);
        assertLight(7, 68, 8, 14, 14, 14);
        assertLight(8, 69, 8, 14, 14, 14);
        assertLight(8, 67, 8, 14, 14, 14);
        assertLight(10, 68, 8, 13, 13, 13);
        assertLight(8 + 14, 68, 8, 1, 1, 1);
        assertLight(8 + 15, 68, 8, 0, 0, 0);
    }

    @Test
    void testColoredSkyEmitter() {
        SkyBFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 0, 0);
        engine.callPerformLightIncrease();

        assertLight(8, 68, 8, 15, 0, 0);
        assertLight(9, 68, 8, 14, 0, 0);
        assertLight(10, 68, 8, 13, 0, 0);

        for (int d = 0; d <= 14; d++) {
            int light = SkyBFSTestHelper.getLight(engine, 8 + d, 68, 8);
            assertEquals(0, green(light), "green at distance " + d);
            assertEquals(0, blue(light), "blue at distance " + d);
        }
    }

    @Test
    void testOpaqueBlockBlocking() {
        SkyBFSTestHelper.setBlock(engine, 10, 68, 8, Blocks.stone);
        SkyBFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        assertLight(9, 68, 8, 14, 14, 14);
        assertLight(10, 68, 8, 0, 0, 0);
        // Light routes around: shortest path to (11,68,8) = 5 steps -> level 10
        assertLight(11, 68, 8, 10, 10, 10);
    }

    @Test
    void testWhiteSourceImmutableDuringDecrease() {
        // Key sky engine invariant: WHITE sources are never cleared by decrease BFS.
        SkyBFSTestHelper.populateAirSection(engine, 1, 4, 0);
        SkyBFSTestHelper.populateAirSection(engine, -1, 4, 0);
        SkyBFSTestHelper.populateAirSection(engine, 0, 4, 1);
        SkyBFSTestHelper.populateAirSection(engine, 0, 4, -1);
        SkyBFSTestHelper.populateAirSection(engine, 0, 5, 0);
        SkyBFSTestHelper.populateAirSection(engine, 0, 3, 0);

        // Two WHITE sources -- removing one should leave the other intact
        SkyBFSTestHelper.enqueueIncrease(engine, 4, 68, 8, 15, 15, 15);
        SkyBFSTestHelper.enqueueIncrease(engine, 12, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Remove source at (12,68,8)
        SkyBFSTestHelper.setLight(engine, 12, 68, 8, 0, 0, 0);
        SkyBFSTestHelper.enqueueDecrease(engine, 12, 68, 8, 15, 15, 15);
        engine.callPerformLightDecrease();

        // Surviving WHITE source must be fully intact
        assertLight(4, 68, 8, 15, 15, 15);
        assertLight(5, 68, 8, 14, 14, 14);
        assertLight(3, 68, 8, 14, 14, 14);
    }

    @Test
    void testDecreaseWithSurvivingWhiteSource() {
        SkyBFSTestHelper.populateAirSection(engine, 1, 4, 0);
        SkyBFSTestHelper.populateAirSection(engine, -1, 4, 0);

        // Two sources: white at (4,68,8), colored red at (12,68,8)
        SkyBFSTestHelper.enqueueIncrease(engine, 4, 68, 8, 15, 15, 15);
        SkyBFSTestHelper.enqueueIncrease(engine, 12, 68, 8, 15, 0, 0);
        engine.callPerformLightIncrease();

        // Remove the red source
        SkyBFSTestHelper.setLight(engine, 12, 68, 8, 0, 0, 0);
        SkyBFSTestHelper.enqueueDecrease(engine, 12, 68, 8, 15, 0, 0);
        engine.callPerformLightDecrease();

        // White source must be fully intact
        assertLight(4, 68, 8, 15, 15, 15);
        assertLight(5, 68, 8, 14, 14, 14);

        // Red should be gone at the removed source position
        assertEquals(
                0,
                red(SkyBFSTestHelper.getLight(engine, 12, 68, 8)) - green(SkyBFSTestHelper.getLight(engine, 12, 68, 8)),
                "no red excess over green at removed source (white still reaches here)");
    }

    @Test
    void testSectionBoundaryCrossing() {
        SkyBFSTestHelper.populateAirSection(engine, 0, 5, 0);

        SkyBFSTestHelper.enqueueIncrease(engine, 8, 79, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        assertLight(8, 79, 8, 15, 15, 15);
        assertLight(8, 78, 8, 14, 14, 14);
        // Crosses into section 5
        assertLight(8, 80, 8, 14, 14, 14);
        assertLight(8, 81, 8, 13, 13, 13);
    }

    @Test
    void testNullNibbleSkipped() {
        // Section 5 not populated -- light near top of section 4 should not crash
        SkyBFSTestHelper.enqueueIncrease(engine, 8, 78, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        assertLight(8, 78, 8, 15, 15, 15);
        assertLight(8, 77, 8, 14, 14, 14);
    }

    private void assertLight(int x, int y, int z, int expectedR, int expectedG, int expectedB) {
        int light = SkyBFSTestHelper.getLight(engine, x, y, z);
        assertEquals(expectedR, red(light), "R at (" + x + "," + y + "," + z + ")");
        assertEquals(expectedG, green(light), "G at (" + x + "," + y + "," + z + ")");
        assertEquals(expectedB, blue(light), "B at (" + x + "," + y + "," + z + ")");
    }
}
