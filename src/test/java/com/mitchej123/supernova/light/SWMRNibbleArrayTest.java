package com.mitchej123.supernova.light;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SWMRNibbleArrayTest {

    @Test
    void testDefaultIsUninitialised() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        assertTrue(arr.isUninitialisedUpdating());
        assertTrue(arr.isUninitialisedVisible());
        assertEquals(0, arr.getUpdating(0, 0, 0));
    }

    @Test
    void testNullNibble() {
        SWMRNibbleArray arr = new SWMRNibbleArray(null, true);
        assertTrue(arr.isNullNibbleUpdating());
        assertTrue(arr.isNullNibbleVisible());
        assertEquals(0, arr.getUpdating(0, 0, 0));
    }

    @Test
    void testSetAndGet() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        arr.set(3, 5, 7, 12);
        assertEquals(12, arr.getUpdating(3, 5, 7));
        // Visible should still be 0 until updateVisible
        assertEquals(0, arr.getVisible(3, 5, 7));
    }

    @Test
    void testUpdateVisible() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        arr.set(0, 0, 0, 15);
        assertTrue(arr.isDirty());
        assertTrue(arr.updateVisible());
        assertEquals(15, arr.getVisible(0, 0, 0));
        assertFalse(arr.isDirty());
    }

    @Test
    void testSetFull() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        arr.setFull();
        for (int i = 0; i < 4096; i++) {
            assertEquals(15, arr.getUpdating(i));
        }
    }

    @Test
    void testSetZero() {
        SWMRNibbleArray arr = new SWMRNibbleArray(new byte[SWMRNibbleArray.ARRAY_SIZE]);
        arr.set(0, 0, 0, 10);
        arr.setZero();
        assertEquals(0, arr.getUpdating(0, 0, 0));
    }

    @Test
    void testNullTransition() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        assertFalse(arr.isNullNibbleUpdating());
        arr.setNull();
        assertTrue(arr.isNullNibbleUpdating());
    }

    @Test
    void testHiddenState() {
        SWMRNibbleArray arr = new SWMRNibbleArray(new byte[SWMRNibbleArray.ARRAY_SIZE]);
        arr.set(5, 5, 5, 8);
        assertTrue(arr.isInitialisedUpdating());
        arr.setHidden();
        assertTrue(arr.isHiddenUpdating());
        // Data still accessible
        assertEquals(8, arr.getUpdating(5, 5, 5));
    }

    @Test
    void testSaveStateNull() {
        SWMRNibbleArray arr = new SWMRNibbleArray(null, true);
        arr.updateVisible();
        assertNull(arr.getSaveState());
    }

    @Test
    void testSaveStateUninit() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        arr.updateVisible();
        SWMRNibbleArray.SaveState state = arr.getSaveState();
        assertNotNull(state);
        assertNull(state.data);
    }

    @Test
    void testSaveStateWithData() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        arr.set(0, 0, 0, 10);
        arr.updateVisible();
        SWMRNibbleArray.SaveState state = arr.getSaveState();
        assertNotNull(state);
        assertNotNull(state.data);
        assertEquals(SWMRNibbleArray.ARRAY_SIZE, state.data.length);
    }

    @Test
    void testAllPositions() {
        SWMRNibbleArray arr = new SWMRNibbleArray();
        // Set a few specific positions and verify no cross-talk
        arr.set(0, 0, 0, 1);
        arr.set(15, 0, 0, 2);
        arr.set(0, 15, 0, 3);
        arr.set(0, 0, 15, 4);
        arr.set(15, 15, 15, 5);

        assertEquals(1, arr.getUpdating(0, 0, 0));
        assertEquals(2, arr.getUpdating(15, 0, 0));
        assertEquals(3, arr.getUpdating(0, 15, 0));
        assertEquals(4, arr.getUpdating(0, 0, 15));
        assertEquals(5, arr.getUpdating(15, 15, 15));
    }

    @Test
    void testSetNonNull() {
        SWMRNibbleArray arr = new SWMRNibbleArray(null, true);
        assertTrue(arr.isNullNibbleUpdating());
        arr.setNonNull();
        assertTrue(arr.isUninitialisedUpdating());
    }

    @Test
    void testFromBytes() {
        byte[] data = new byte[SWMRNibbleArray.ARRAY_SIZE];
        data[0] = (byte) 0xAB; // index 0 = 0xB, index 1 = 0xA
        SWMRNibbleArray arr = new SWMRNibbleArray(data);
        assertTrue(arr.isInitialisedUpdating());
        assertEquals(0xB, arr.getUpdating(0));
        assertEquals(0xA, arr.getUpdating(1));
    }

    @Test
    void testBadLength() {
        assertThrows(IllegalArgumentException.class, () -> new SWMRNibbleArray(new byte[100]));
    }

    @Test
    void testExtrudeLowerPreservesFullFlag() {
        SWMRNibbleArray source = new SWMRNibbleArray();
        source.setFull();
        assertTrue(source.isFullUpdating());
        assertFalse(source.isZeroUpdating());

        SWMRNibbleArray target = new SWMRNibbleArray();
        target.extrudeLower(source);
        assertTrue(target.isFullUpdating(), "extrudeLower from full source should preserve fullFlag");
        assertFalse(target.isZeroUpdating());
        // Verify data is actually all 15
        for (int i = 0; i < 4096; i++) {
            assertEquals(15, target.getUpdating(i));
        }
    }

    @Test
    void testExtrudeLowerPreservesZeroFlag() {
        // UNINIT source (storageUpdating == null) takes the setUninitialised() path
        // Use an INIT source with zeroFlag set via setZero()
        SWMRNibbleArray source = new SWMRNibbleArray();
        source.setZero();
        assertTrue(source.isZeroUpdating());
        assertFalse(source.isFullUpdating());

        SWMRNibbleArray target = new SWMRNibbleArray();
        target.extrudeLower(source);
        assertTrue(target.isZeroUpdating(), "extrudeLower from zero source should preserve zeroFlag");
        assertFalse(target.isFullUpdating());
        for (int i = 0; i < 4096; i++) {
            assertEquals(0, target.getUpdating(i));
        }
    }

    @Test
    void testExtrudeLowerNonUniformClearsFlags() {
        SWMRNibbleArray source = new SWMRNibbleArray();
        source.setFull();
        source.set(0, 0, 0, 5); // breaks uniformity, clears fullFlag
        assertFalse(source.isFullUpdating());
        assertFalse(source.isZeroUpdating());

        SWMRNibbleArray target = new SWMRNibbleArray();
        target.extrudeLower(source);
        assertFalse(target.isFullUpdating(), "extrudeLower from non-uniform source should not set fullFlag");
        assertFalse(target.isZeroUpdating());
    }
}
