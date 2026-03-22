package com.mitchej123.supernova.light;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RenderUpdateQueueTest {

    @Test
    void testOfferAndDrain() {
        final RenderUpdateQueue queue = new RenderUpdateQueue(8);
        for (long i = 0; i < 5; i++) {
            queue.offer(i * 100);
        }
        final List<Long> drained = new ArrayList<>();
        final int count = queue.drain(drained::add);
        assertEquals(5, count);
        for (int i = 0; i < 5; i++) {
            assertEquals(i * 100L, drained.get(i));
        }
    }

    @Test
    void testEmptyDrain() {
        final RenderUpdateQueue queue = new RenderUpdateQueue(8);
        final int count = queue.drain(v -> fail("consumer should not be called"));
        assertEquals(0, count);
    }

    @Test
    void testDoubleBufferSwap() {
        final RenderUpdateQueue queue = new RenderUpdateQueue(4);
        queue.offer(1L);
        queue.offer(2L);
        final List<Long> first = new ArrayList<>();
        assertEquals(2, queue.drain(first::add));
        assertEquals(List.of(1L, 2L), first);

        queue.offer(3L);
        queue.offer(4L);
        final List<Long> second = new ArrayList<>();
        assertEquals(2, queue.drain(second::add));
        assertEquals(List.of(3L, 4L), second);
    }

    @Test
    void testGrowOnBurst() {
        final RenderUpdateQueue queue = new RenderUpdateQueue(4);
        for (long i = 0; i < 100; i++) {
            queue.offer(i);
        }
        final List<Long> drained = new ArrayList<>();
        assertEquals(100, queue.drain(drained::add));
        for (int i = 0; i < 100; i++) {
            assertEquals((long) i, drained.get(i));
        }
    }

    @Test
    void testIsEmpty() {
        final RenderUpdateQueue queue = new RenderUpdateQueue(4);
        assertTrue(queue.isEmpty());
        queue.offer(42L);
        assertFalse(queue.isEmpty());
        queue.drain(_ -> {});
        assertTrue(queue.isEmpty());
    }

    @Test
    void testConcurrentProducers() throws InterruptedException {
        final int threads = 4;
        final int perThread = 1000;
        final RenderUpdateQueue queue = new RenderUpdateQueue(16);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            new Thread(() -> {
                try {
                    start.await();
                } catch (final InterruptedException e) {
                    return;
                }
                for (int i = 0; i < perThread; i++) {
                    queue.offer(threadId * 100_000L + i);
                }
                done.countDown();
            }).start();
        }

        start.countDown();
        done.await();

        final List<Long> drained = new ArrayList<>();
        queue.drain(drained::add);
        assertEquals(threads * perThread, drained.size());

        // Verify all values present (order not guaranteed across threads)
        Collections.sort(drained);
        for (int t = 0; t < threads; t++) {
            for (int i = 0; i < perThread; i++) {
                assertTrue(drained.contains(t * 100_000L + i), "missing value from thread " + t + " index " + i);
            }
        }
    }
}
