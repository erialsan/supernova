package com.mitchej123.supernova.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.chunk.Chunk;

public final class SnapshotChunkMap {

    private final Long2ObjectOpenHashMap<Chunk> map = new Long2ObjectOpenHashMap<>();
    private volatile Long2ObjectOpenHashMap<Chunk> snapshot = new Long2ObjectOpenHashMap<>();
    private final Thread ownerThread = Thread.currentThread();

    public void put(final long key, final Chunk value) {
        map.put(key, value);
        snapshot = map.clone();
    }

    public Chunk remove(final long key) {
        final Chunk removed = map.remove(key);
        snapshot = map.clone();
        return removed;
    }

    public Chunk get(final long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.get(key);
        }
        return snapshot.get(key);
    }

}
