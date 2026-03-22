package com.mitchej123.supernova.storage;

import com.mitchej123.supernova.light.SWMRNibbleArray;
import com.mitchej123.supernova.util.WorldUtil;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataManagerBufferTest {

    private static final int TOTAL_LIGHT_SECTIONS = WorldUtil.getTotalLightSections();

    private static SWMRNibbleArray[] makeNibbleArray() {
        SWMRNibbleArray[] nibbles = new SWMRNibbleArray[TOTAL_LIGHT_SECTIONS];
        return nibbles;
    }

    private static SWMRNibbleArray makeInitNibble() {
        SWMRNibbleArray nib = new SWMRNibbleArray();
        nib.updateVisible(); // make data available on visible side
        return nib;
    }

    @Test
    void testRoundTripAllChannels() {
        SWMRNibbleArray[] rIn = makeNibbleArray();
        SWMRNibbleArray[] gIn = makeNibbleArray();
        SWMRNibbleArray[] bIn = makeNibbleArray();

        int sectionY = 5;
        int idx = AbstractSupernovaDataManager.nibbleIndex(sectionY);

        // Set specific values in each channel
        rIn[idx] = makeInitNibble();
        rIn[idx].set(0, 10);
        rIn[idx].updateVisible();
        gIn[idx] = makeInitNibble();
        gIn[idx].set(0, 7);
        gIn[idx].updateVisible();
        bIn[idx] = makeInitNibble();
        bIn[idx].set(0, 3);
        bIn[idx].updateVisible();

        // Write
        ByteBuffer buf = ByteBuffer.allocate(1 + 3 * SWMRNibbleArray.ARRAY_SIZE);
        AbstractSupernovaDataManager.writeSectionToBuffer(buf, rIn, gIn, bIn, sectionY);
        buf.flip();

        // Read
        SWMRNibbleArray[] rOut = makeNibbleArray();
        SWMRNibbleArray[] gOut = makeNibbleArray();
        SWMRNibbleArray[] bOut = makeNibbleArray();
        AbstractSupernovaDataManager.readSectionFromBuffer(buf, rOut, gOut, bOut, sectionY);

        // Verify
        assertNotNull(rOut[idx]);
        assertNotNull(gOut[idx]);
        assertNotNull(bOut[idx]);
        assertEquals(10, rOut[idx].getUpdating(0));
        assertEquals(7, gOut[idx].getUpdating(0));
        assertEquals(3, bOut[idx].getUpdating(0));
    }

    @Test
    void testRoundTripPartialChannels() {
        SWMRNibbleArray[] rIn = makeNibbleArray();
        int sectionY = 3;
        int idx = AbstractSupernovaDataManager.nibbleIndex(sectionY);

        rIn[idx] = makeInitNibble();
        rIn[idx].set(42, 12);
        rIn[idx].updateVisible();

        // Only R present, G/B are null arrays
        ByteBuffer buf = ByteBuffer.allocate(1 + 3 * SWMRNibbleArray.ARRAY_SIZE);
        AbstractSupernovaDataManager.writeSectionToBuffer(buf, rIn, null, null, sectionY);

        // Check flag byte is FLAG_R only (0x01)
        assertEquals(0x01, buf.get(0) & 0xFF);

        buf.flip();

        SWMRNibbleArray[] rOut = makeNibbleArray();
        SWMRNibbleArray[] gOut = makeNibbleArray();
        SWMRNibbleArray[] bOut = makeNibbleArray();
        AbstractSupernovaDataManager.readSectionFromBuffer(buf, rOut, gOut, bOut, sectionY);

        assertNotNull(rOut[idx]);
        // When only R is present, RGB mode clones R to G/B (scalar->RGB conversion)
        assertNotNull(gOut[idx]);
        assertNotNull(bOut[idx]);
        assertEquals(12, rOut[idx].getUpdating(42));
        assertEquals(12, gOut[idx].getUpdating(42));
        assertEquals(12, bOut[idx].getUpdating(42));
    }

    @Test
    void testRoundTripNoData() {
        int sectionY = 0;

        ByteBuffer buf = ByteBuffer.allocate(1 + 3 * SWMRNibbleArray.ARRAY_SIZE);
        AbstractSupernovaDataManager.writeSectionToBuffer(buf, null, null, null, sectionY);

        // Flag byte should be 0
        assertEquals(0, buf.get(0) & 0xFF);
        // Only 1 byte written (the flag)
        assertEquals(1, buf.position());

        buf.flip();

        SWMRNibbleArray[] rOut = makeNibbleArray();
        SWMRNibbleArray[] gOut = makeNibbleArray();
        SWMRNibbleArray[] bOut = makeNibbleArray();
        AbstractSupernovaDataManager.readSectionFromBuffer(buf, rOut, gOut, bOut, sectionY);

        int idx = AbstractSupernovaDataManager.nibbleIndex(sectionY);
        assertNull(rOut[idx]);
        assertNull(gOut[idx]);
        assertNull(bOut[idx]);
    }

    @Test
    void testNibbleValuesPreserved() {
        SWMRNibbleArray[] rIn = makeNibbleArray();
        SWMRNibbleArray[] gIn = makeNibbleArray();
        SWMRNibbleArray[] bIn = makeNibbleArray();

        int sectionY = 10;
        int idx = AbstractSupernovaDataManager.nibbleIndex(sectionY);

        rIn[idx] = makeInitNibble();
        gIn[idx] = makeInitNibble();
        bIn[idx] = makeInitNibble();

        // Set a pattern of values across the section
        for (int i = 0; i < 4096; i++) {
            rIn[idx].set(i, i % 16);
            gIn[idx].set(i, (i + 5) % 16);
            bIn[idx].set(i, (i + 10) % 16);
        }
        rIn[idx].updateVisible();
        gIn[idx].updateVisible();
        bIn[idx].updateVisible();

        ByteBuffer buf = ByteBuffer.allocate(1 + 3 * SWMRNibbleArray.ARRAY_SIZE);
        AbstractSupernovaDataManager.writeSectionToBuffer(buf, rIn, gIn, bIn, sectionY);
        buf.flip();

        SWMRNibbleArray[] rOut = makeNibbleArray();
        SWMRNibbleArray[] gOut = makeNibbleArray();
        SWMRNibbleArray[] bOut = makeNibbleArray();
        AbstractSupernovaDataManager.readSectionFromBuffer(buf, rOut, gOut, bOut, sectionY);

        for (int i = 0; i < 4096; i++) {
            assertEquals(i % 16, rOut[idx].getUpdating(i), "R at " + i);
            assertEquals((i + 5) % 16, gOut[idx].getUpdating(i), "G at " + i);
            assertEquals((i + 10) % 16, bOut[idx].getUpdating(i), "B at " + i);
        }
    }

    @Test
    void testMultipleSectionsSequential() {
        SWMRNibbleArray[] rIn = makeNibbleArray();
        SWMRNibbleArray[] gIn = makeNibbleArray();
        SWMRNibbleArray[] bIn = makeNibbleArray();

        // Populate sections 2 and 8
        for (int sy : new int[] { 2, 8 }) {
            int idx = AbstractSupernovaDataManager.nibbleIndex(sy);
            rIn[idx] = makeInitNibble();
            rIn[idx].set(0, sy);
            rIn[idx].updateVisible();
            gIn[idx] = makeInitNibble();
            gIn[idx].set(0, sy + 1);
            gIn[idx].updateVisible();
        }

        ByteBuffer buf = ByteBuffer.allocate(2 * (1 + 3 * SWMRNibbleArray.ARRAY_SIZE));
        AbstractSupernovaDataManager.writeSectionToBuffer(buf, rIn, gIn, bIn, 2);
        AbstractSupernovaDataManager.writeSectionToBuffer(buf, rIn, gIn, bIn, 8);
        buf.flip();

        SWMRNibbleArray[] rOut = makeNibbleArray();
        SWMRNibbleArray[] gOut = makeNibbleArray();
        SWMRNibbleArray[] bOut = makeNibbleArray();
        AbstractSupernovaDataManager.readSectionFromBuffer(buf, rOut, gOut, bOut, 2);
        AbstractSupernovaDataManager.readSectionFromBuffer(buf, rOut, gOut, bOut, 8);

        assertEquals(2, rOut[AbstractSupernovaDataManager.nibbleIndex(2)].getUpdating(0));
        assertEquals(3, gOut[AbstractSupernovaDataManager.nibbleIndex(2)].getUpdating(0));
        assertEquals(8, rOut[AbstractSupernovaDataManager.nibbleIndex(8)].getUpdating(0));
        assertEquals(9, gOut[AbstractSupernovaDataManager.nibbleIndex(8)].getUpdating(0));
    }

    @Test
    void testNibbleIndex() {
        // minLightSection = -1, so sectionY=0 -> index=1
        assertEquals(1, AbstractSupernovaDataManager.nibbleIndex(0));
        assertEquals(0, AbstractSupernovaDataManager.nibbleIndex(-1));
        assertEquals(16, AbstractSupernovaDataManager.nibbleIndex(15));
    }
}
