package com.mitchej123.supernova.light;

import com.mitchej123.supernova.util.CoordinateUtils;
import net.minecraft.world.chunk.Chunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LightQueueTest {

    /** Non-null Chunk sentinel created via Unsafe (no constructor, no MC deps). */
    private static final Chunk DUMMY_CHUNK;

    static {
        try {
            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Unsafe unsafe = (Unsafe) f.get(null);
            DUMMY_CHUNK = (Chunk) unsafe.allocateInstance(Chunk.class);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private LightQueue queue;

    @BeforeEach
    void setup() {
        queue = new LightQueue();
    }

    @Test
    void testEmptyQueue() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.removeFirstTask());
        assertNull(queue.removeFirstBlockChangeTask());
        assertNull(queue.removeFirstInitialLightTask());
        assertFalse(queue.hasInitialLightTask());
    }

    @Test
    void testBlockChangePriority() {
        // Queue edge check first, then block change on different chunk
        queue.queueEdgeCheck(0, 0, 0, true);
        queue.queueBlockChange(16, 64, 16); // chunk (1, 1)

        // removeFirstBlockChangeTask should find the block change, skipping the edge-only task
        final ChunkTasks blockTask = queue.removeFirstBlockChangeTask();
        assertNotNull(blockTask);
        assertEquals(CoordinateUtils.getChunkKey(1, 1), blockTask.chunkCoordinate);
        assertNotNull(blockTask.changedPositions);
        assertFalse(blockTask.changedPositions.isEmpty());

        // Edge-only task still in queue
        final ChunkTasks edgeTask = queue.removeFirstTask();
        assertNotNull(edgeTask);
        assertEquals(CoordinateUtils.getChunkKey(0, 0), edgeTask.chunkCoordinate);
    }

    @Test
    void testInitialLightPriority() {
        // Queue edge check first, then initial light on different chunk
        queue.queueEdgeCheck(0, 0, 0, true);
        queue.queueChunkLight(1, 1, DUMMY_CHUNK, null);

        // removeFirstInitialLightTask should find the initial light task
        final ChunkTasks lightTask = queue.removeFirstInitialLightTask();
        assertNotNull(lightTask);
        assertEquals(CoordinateUtils.getChunkKey(1, 1), lightTask.chunkCoordinate);

        // Edge-only task still in queue
        assertNull(queue.removeFirstInitialLightTask());
        assertNotNull(queue.removeFirstTask());
    }

    @Test
    void testRemoveFirstTaskFIFO() {
        queue.queueEdgeCheck(0, 0, 0, true);
        queue.queueEdgeCheck(1, 1, 1, true);
        queue.queueEdgeCheck(2, 2, 2, true);

        assertEquals(CoordinateUtils.getChunkKey(0, 0), queue.removeFirstTask().chunkCoordinate);
        assertEquals(CoordinateUtils.getChunkKey(1, 1), queue.removeFirstTask().chunkCoordinate);
        assertEquals(CoordinateUtils.getChunkKey(2, 2), queue.removeFirstTask().chunkCoordinate);
        assertNull(queue.removeFirstTask());
    }

    @Test
    void testCoalescingBlockChanges() {
        // Two block changes in the same chunk should merge
        queue.queueBlockChange(5, 64, 7);   // pos1
        queue.queueBlockChange(10, 80, 3);  // pos2, same chunk (0, 0)

        assertEquals(1, queue.size());
        final ChunkTasks task = queue.removeFirstTask();
        assertNotNull(task.changedPositions);
        assertEquals(2, task.changedPositions.size());

        final int pos1 = (5 & 15) | ((7 & 15) << 4) | (64 << 8);
        final int pos2 = (10 & 15) | ((3 & 15) << 4) | (80 << 8);
        assertTrue(task.changedPositions.contains(pos1));
        assertTrue(task.changedPositions.contains(pos2));
    }

    @Test
    void testEdgeCheckCoalescing() {
        queue.queueEdgeCheck(0, 0, 3, true);
        queue.queueEdgeCheck(0, 0, 7, true);
        queue.queueEdgeCheck(0, 0, 3, false); // block edge, same section

        assertEquals(1, queue.size());
        final ChunkTasks task = queue.removeFirstTask();
        assertNotNull(task.queuedEdgeChecksSky);
        assertTrue(task.queuedEdgeChecksSky.contains(3));
        assertTrue(task.queuedEdgeChecksSky.contains(7));
        assertNotNull(task.queuedEdgeChecksBlock);
        assertTrue(task.queuedEdgeChecksBlock.contains(3));
    }

    @Test
    void testRemoveChunk() {
        queue.queueBlockChange(5, 64, 7);
        assertTrue(queue.hasPendingWork(0, 0));

        queue.removeChunk(0, 0);
        assertFalse(queue.hasPendingWork(0, 0));
        assertTrue(queue.isEmpty());
    }

    @Test
    void testBlockChangeNotReturnedByInitialLightRemove() {
        // Block-change-only task should not be returned by removeFirstInitialLightTask
        queue.queueBlockChange(5, 64, 7);
        assertNull(queue.removeFirstInitialLightTask());
        // But should still be in queue
        assertFalse(queue.isEmpty());
    }

    @Test
    void testHasInitialLightTask() {
        queue.queueBlockChange(5, 64, 7);
        assertFalse(queue.hasInitialLightTask());

        queue.queueChunkLight(1, 1, DUMMY_CHUNK, null);
        assertTrue(queue.hasInitialLightTask());
    }

    @Test
    void testRequeueIncrementsAttempts() {
        queue.requeueChunkLight(0, 0, DUMMY_CHUNK, null, 0);
        ChunkTasks task = queue.removeFirstTask();
        assertEquals(1, task.relightAttempts);

        queue.requeueChunkLight(0, 0, DUMMY_CHUNK, null, 2);
        task = queue.removeFirstTask();
        assertEquals(3, task.relightAttempts);
    }

    @Test
    void testMixedTaskCoalescing() {
        // Same chunk gets block change + initial light + edge check -> single task with all fields
        queue.queueBlockChange(5, 64, 7);      // chunk (0, 0)
        queue.queueChunkLight(0, 0, DUMMY_CHUNK, null);
        queue.queueEdgeCheck(0, 0, 4, true);

        assertEquals(1, queue.size());
        final ChunkTasks task = queue.removeFirstTask();
        assertNotNull(task.changedPositions);
        assertNotNull(task.initialLightChunk);
        assertNotNull(task.queuedEdgeChecksSky);
    }
}
