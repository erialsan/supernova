package com.mitchej123.supernova.light.engine;

import com.mitchej123.supernova.api.PackedColorLight;
import net.minecraft.init.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.mitchej123.supernova.api.PackedColorLight.blue;
import static com.mitchej123.supernova.api.PackedColorLight.green;
import static com.mitchej123.supernova.api.PackedColorLight.pack;
import static com.mitchej123.supernova.api.PackedColorLight.red;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockEngineBFSTest {

    private TestableBlockEngine engine;

    @BeforeAll
    static void bootstrap() {
        MCBootstrap.init();
    }

    @BeforeEach
    void setup() {
        engine = new TestableBlockEngine(MCBootstrap.getServerWorld());
        BFSTestHelper.setupCenter(engine);
        // Populate center chunk section at Y=4 (world Y 64-79)
        BFSTestHelper.populateAirSection(engine, 0, 4, 0);
    }

    @Test
    void testSingleWhiteEmitterFalloff() {
        // Populate adjacent chunk sections so light can propagate past chunk boundaries
        BFSTestHelper.populateAirSection(engine, 1, 4, 0);
        BFSTestHelper.populateAirSection(engine, -1, 4, 0);
        BFSTestHelper.populateAirSection(engine, 0, 4, 1);
        BFSTestHelper.populateAirSection(engine, 0, 4, -1);
        BFSTestHelper.populateAirSection(engine, 0, 5, 0);
        BFSTestHelper.populateAirSection(engine, 0, 3, 0);

        // White light at (8,68,8), verify diamond falloff
        BFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Source position should be 15
        assertLight(8, 68, 8, 15, 15, 15);

        // Adjacent (distance 1) = 14
        assertLight(9, 68, 8, 14, 14, 14);
        assertLight(7, 68, 8, 14, 14, 14);
        assertLight(8, 69, 8, 14, 14, 14);
        assertLight(8, 67, 8, 14, 14, 14);
        assertLight(8, 68, 9, 14, 14, 14);
        assertLight(8, 68, 7, 14, 14, 14);

        // Distance 2 = 13
        assertLight(10, 68, 8, 13, 13, 13);
        assertLight(8, 70, 8, 13, 13, 13);

        // Distance 14 = 1 (should still exist)
        assertLight(8 + 14, 68, 8, 1, 1, 1);

        // Distance 15 = 0 (below threshold, not propagated)
        assertLight(8 + 15, 68, 8, 0, 0, 0);
    }

    @Test
    void testSingleColoredEmitter() {
        // Pure red light at (8,68,8)
        BFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 0, 0);
        engine.callPerformLightIncrease();

        assertLight(8, 68, 8, 15, 0, 0);
        assertLight(9, 68, 8, 14, 0, 0);
        assertLight(10, 68, 8, 13, 0, 0);

        // Green and blue stay zero everywhere
        for (int d = 0; d <= 14; d++) {
            int light = BFSTestHelper.getLight(engine, 8 + d, 68, 8);
            assertEquals(0, green(light), "green at distance " + d);
            assertEquals(0, blue(light), "blue at distance " + d);
        }
    }

    @Test
    void testOpaqueBlockBlocking() {
        // Light at (8,68,8), stone wall at x=10
        BFSTestHelper.setBlock(engine, 10, 68, 8, Blocks.stone);
        BFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Light reaches x=9 (distance 1 from stone)
        assertLight(9, 68, 8, 14, 14, 14);

        assertLight(10, 68, 8, 0, 0, 0);

        // Behind stone -- light goes around via adjacent air blocks (shortest path = 5 steps)
        // Path: (8,68,8)->(9,68,8)->(9,68,9)->(10,68,9)->(11,68,9)->(11,68,8) = level 10
        assertLight(11, 68, 8, 10, 10, 10);

        // Light goes around stone in other directions
        assertTrue(red(BFSTestHelper.getLight(engine, 8, 68, 10)) > 0, "light should go +Z");
        assertTrue(red(BFSTestHelper.getLight(engine, 8, 69, 8)) > 0, "light should go +Y");
    }

    @Test
    void testPropagationStopsAtThreshold() {
        // Light level 2: should propagate one step to level 1, then stop
        BFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 2, 0, 0);
        engine.callPerformLightIncrease();

        assertLight(8, 68, 8, 2, 0, 0);
        assertLight(9, 68, 8, 1, 0, 0);
        // Level 1 doesn't propagate further (maxComponent(1) <= 1)
        assertLight(10, 68, 8, 0, 0, 0);
    }

    @Test
    void testTwoEmittersDifferentColors() {
        // Red at (4,68,8), blue at (12,68,8)
        BFSTestHelper.enqueueIncrease(engine, 4, 68, 8, 15, 0, 0);
        BFSTestHelper.enqueueIncrease(engine, 12, 68, 8, 0, 0, 15);
        engine.callPerformLightIncrease();

        // At midpoint (8,68,8): red attenuated by 4, blue attenuated by 4
        int mid = BFSTestHelper.getLight(engine, 8, 68, 8);
        assertEquals(11, red(mid), "red at midpoint");
        assertEquals(0, green(mid), "green at midpoint");
        assertEquals(11, blue(mid), "blue at midpoint");

        // Near red source: dominated by red
        int nearRed = BFSTestHelper.getLight(engine, 5, 68, 8);
        assertEquals(14, red(nearRed));
        assertEquals(0, green(nearRed));
        assertTrue(blue(nearRed) < 10, "blue should be weak near red source");
    }

    @Test
    void testTwoEmittersOverlapMax() {
        // Two white sources close together -- verify packedMax behavior
        BFSTestHelper.enqueueIncrease(engine, 7, 68, 8, 15, 15, 15);
        BFSTestHelper.enqueueIncrease(engine, 9, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // At (8,68,8): distance 1 from both -> 14 from each -> max = 14
        assertLight(8, 68, 8, 14, 14, 14);

        // At (7,68,8): distance 0 from first = 15, distance 2 from second = 13 -> max = 15
        assertLight(7, 68, 8, 15, 15, 15);
    }

    @Test
    void testDecreaseRemovesAllLight() {
        // Set up lit state: source at (8,68,8) with level 15
        BFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Verify light exists
        assertTrue(red(BFSTestHelper.getLight(engine, 10, 68, 8)) > 0);

        // Now remove: clear source and enqueue decrease
        BFSTestHelper.setLight(engine, 8, 68, 8, 0, 0, 0);
        BFSTestHelper.enqueueDecrease(engine, 8, 68, 8, 15, 15, 15);
        engine.callPerformLightDecrease();

        // All light should be gone
        for (int d = 0; d <= 14; d++) {
            assertLight(8 + d, 68, 8, 0, 0, 0);
            assertLight(8 - d, 68, 8, 0, 0, 0);
        }
    }

    @Test
    void testDecreaseWithSurvivingSource() {
        // Two sources: A at (4,68,8) and B at (12,68,8), both white
        BFSTestHelper.enqueueIncrease(engine, 4, 68, 8, 15, 15, 15);
        BFSTestHelper.enqueueIncrease(engine, 12, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Remove source A: clear its light and enqueue decrease
        BFSTestHelper.setLight(engine, 4, 68, 8, 0, 0, 0);
        BFSTestHelper.enqueueDecrease(engine, 4, 68, 8, 15, 15, 15);
        engine.callPerformLightDecrease();

        // Source B should be intact
        assertLight(12, 68, 8, 15, 15, 15);
        assertLight(11, 68, 8, 14, 14, 14);
        assertLight(13, 68, 8, 14, 14, 14);

        // Source A position should now only have light from B (distance 8 -> level 7)
        int atA = BFSTestHelper.getLight(engine, 4, 68, 8);
        assertEquals(7, red(atA), "light from surviving source B");
    }


    @Test
    void testNullNibbleSkipped() {
        // Don't populate section at sectionY=5 (world Y 80-95)
        // Light at (8,78,8) near top of section 4 -- should not crash when trying to enter section 5
        BFSTestHelper.enqueueIncrease(engine, 8, 78, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Light propagates within section 4
        assertLight(8, 78, 8, 15, 15, 15);
        assertLight(8, 77, 8, 14, 14, 14);
        // Does not crash trying to enter section 5 (no nibble -> skipped)
    }

    @Test
    void testNullSectionTreatedAsAir() {
        // Remove the section from cache but keep nibbles
        // This simulates a section with nibbles but no block storage
        int idx = BFSTestHelper.sectionIndex(engine, 0, 4, 0);
        engine.getSectionCache()[idx] = null;

        // Nibbles are still there from setup -- engine should treat null section as air
        BFSTestHelper.enqueueIncrease(engine, 8, 68, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Should propagate with air absorption (1,1,1 per step)
        assertLight(8, 68, 8, 15, 15, 15);
        assertLight(9, 68, 8, 14, 14, 14);
    }

    @Test
    void testSectionBoundaryCrossing() {
        // Populate section 5 (Y 80-95) adjacent to section 4
        BFSTestHelper.populateAirSection(engine, 0, 5, 0);

        // Light near top of section 4
        BFSTestHelper.enqueueIncrease(engine, 8, 79, 8, 15, 15, 15);
        engine.callPerformLightIncrease();

        // Light in section 4
        assertLight(8, 79, 8, 15, 15, 15);
        assertLight(8, 78, 8, 14, 14, 14);

        // Light crosses into section 5
        assertLight(8, 80, 8, 14, 14, 14);
        assertLight(8, 81, 8, 13, 13, 13);
    }

    @Test
    void testDecreaseMultiColorDoesNotExplode() {
        // Set up 3 differently-colored sources in a small area to create multi-channel overlap.
        // This is the scenario that caused BFS explosion: overlapping colors mean the decrease
        // for one source's channel cascades through blocks where that channel was already zero.
        BFSTestHelper.populateAirSection(engine, 1, 4, 0);
        BFSTestHelper.populateAirSection(engine, -1, 4, 0);
        BFSTestHelper.populateAirSection(engine, 0, 4, 1);
        BFSTestHelper.populateAirSection(engine, 0, 4, -1);
        BFSTestHelper.populateAirSection(engine, 0, 5, 0);
        BFSTestHelper.populateAirSection(engine, 0, 3, 0);

        // Red at (4,68,8), green at (8,68,4), blue at (12,68,8)
        BFSTestHelper.enqueueIncrease(engine, 4, 68, 8, 15, 0, 0);
        BFSTestHelper.enqueueIncrease(engine, 8, 68, 4, 0, 15, 0);
        BFSTestHelper.enqueueIncrease(engine, 12, 68, 8, 0, 0, 15);
        engine.callPerformLightIncrease();

        // Now remove the red source
        BFSTestHelper.setLight(engine, 4, 68, 8, 0, 0, 0);
        BFSTestHelper.enqueueDecrease(engine, 4, 68, 8, 15, 0, 0);
        engine.lastBfsDecreaseTotal = 0;
        engine.lastBfsIncreaseTotal = 0;
        engine.callPerformLightDecrease();

        // Red should be gone everywhere its range reached
        for (int d = 0; d <= 14; d++) {
            int light = BFSTestHelper.getLight(engine, 4 + d, 68, 8);
            assertEquals(0, red(light), "red should be cleared at distance " + d);
        }

        // Green and blue sources should be intact (may have spillover from each other)
        assertEquals(0, red(BFSTestHelper.getLight(engine, 8, 68, 4)), "red at green source");
        assertEquals(15, green(BFSTestHelper.getLight(engine, 8, 68, 4)), "green at green source");
        assertEquals(0, red(BFSTestHelper.getLight(engine, 12, 68, 8)), "red at blue source");
        assertEquals(15, blue(BFSTestHelper.getLight(engine, 12, 68, 8)), "blue at blue source");

        // BFS should be bounded: a single red source at level 15 in open air affects a sphere
        // of radius 15. With the revisit fix, the decrease visits each block at most once per
        // channel (only R is non-zero in the decrease path). Volume of a diamond/Manhattan
        // radius 15 is at most ~15000 blocks. Allow generous margin for BFS overhead.
        assertTrue(engine.lastBfsDecreaseTotal < 30_000,
            "decrease BFS should be bounded, was: " + engine.lastBfsDecreaseTotal);
    }

    @Test
    void testDecreaseWithDifferentColorSurvival() {
        // Red source at (4,68,8), blue source at (12,68,8).
        // Remove red. Blue must survive untouched -- the decrease for red should not
        // cascade through blue-only channels.
        BFSTestHelper.populateAirSection(engine, 1, 4, 0);
        BFSTestHelper.populateAirSection(engine, -1, 4, 0);
        BFSTestHelper.populateAirSection(engine, 0, 4, 1);
        BFSTestHelper.populateAirSection(engine, 0, 4, -1);
        BFSTestHelper.populateAirSection(engine, 0, 5, 0);
        BFSTestHelper.populateAirSection(engine, 0, 3, 0);

        BFSTestHelper.enqueueIncrease(engine, 4, 68, 8, 15, 0, 0);
        BFSTestHelper.enqueueIncrease(engine, 12, 68, 8, 0, 0, 15);
        engine.callPerformLightIncrease();

        // Verify overlap zone has both colors
        int mid = BFSTestHelper.getLight(engine, 8, 68, 8);
        assertEquals(11, red(mid), "red at midpoint before decrease");
        assertEquals(11, blue(mid), "blue at midpoint before decrease");

        // Remove red source
        BFSTestHelper.setLight(engine, 4, 68, 8, 0, 0, 0);
        BFSTestHelper.enqueueDecrease(engine, 4, 68, 8, 15, 0, 0);
        engine.callPerformLightDecrease();

        // Midpoint should have blue only now
        mid = BFSTestHelper.getLight(engine, 8, 68, 8);
        assertEquals(0, red(mid), "red cleared at midpoint");
        assertEquals(11, blue(mid), "blue preserved at midpoint");

        // Blue source intact
        assertLight(12, 68, 8, 0, 0, 15);
        assertLight(11, 68, 8, 0, 0, 14);
    }

    private void assertLight(int x, int y, int z, int expectedR, int expectedG, int expectedB) {
        int light = BFSTestHelper.getLight(engine, x, y, z);
        assertEquals(expectedR, red(light), "R at (" + x + "," + y + "," + z + ")");
        assertEquals(expectedG, green(light), "G at (" + x + "," + y + "," + z + ")");
        assertEquals(expectedB, blue(light), "B at (" + x + "," + y + "," + z + ")");
    }
}
