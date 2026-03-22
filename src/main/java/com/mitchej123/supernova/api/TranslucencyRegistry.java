package com.mitchej123.supernova.api;

import com.mitchej123.supernova.Supernova;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import java.util.Arrays;
import java.util.BitSet;
import java.util.function.BiConsumer;

/**
 * Block RGB translucency registry. Controls per-channel light absorption when light propagates through blocks.
 *
 * Lookup priority:
 * <ol>
 *   <li>{@link ColoredTranslucency} interface -- dynamic per-position transmittance</li>
 *   <li>Explicit registry entry (per-meta or wildcard)</li>
 *   <li>Vanilla fallback -- uniform absorption from {@code block.getLightOpacity()}</li>
 * </ol>
 *
 * Register entries during {@code FMLInitializationEvent} or {@code FMLPostInitializationEvent}.
 * API values are <b>transmittance</b> (15=transparent, 0=opaque). Internal engine queries
 * return <b>absorption</b> (0=transparent, 15=opaque).
 */
public final class TranslucencyRegistry {

    // packed absorption + 1 per meta (0 = unregistered). length==1 -> wildcard for all metas.
    private static int[][] REGISTRY_BY_ID = new int[0][];
    private static final BitSet HAS_ENTRY = new BitSet();

    /** Flat absorption cache indexed by blockId. UNCACHEABLE for positional/directional blocks. */
    private static int[] ABSORPTION_CACHE = new int[0];
    /** Per-meta absorption cache. Non-null entries hold meta 0-15 packed absorption values. */
    private static int[][] ABSORPTION_CACHE_PER_META = new int[0][];
    private static final int UNCACHEABLE = -1;
    /** Blocks marked uncacheable by external callers (e.g. FaceOcclusion for directional blocks). */
    private static final BitSet FORCE_UNCACHEABLE = new BitSet();

    private TranslucencyRegistry() {}

    /**
     * Register per-channel transmittance for all metas of a block.
     *
     * @param block target block
     * @param r     red transmittance (0-15, 15=fully transparent)
     * @param g     green transmittance
     * @param b     blue transmittance
     */
    public static void registerTransmittance(Block block, int r, int g, int b) {
        final int absorption = PackedColorLight.pack(15 - r, 15 - g, 15 - b);
        final int id = Block.getIdFromBlock(block);
        ensureCapacity(id);
        REGISTRY_BY_ID[id] = new int[] { absorption + 1 };
        markEntry(block);
    }

    /**
     * Register per-channel transmittance for a specific block + meta.
     *
     * @param block target block
     * @param meta  block metadata (&gt;= 0)
     * @param r     red transmittance (0-15, 15=fully transparent)
     * @param g     green transmittance
     * @param b     blue transmittance
     */
    public static void registerTransmittance(Block block, int meta, int r, int g, int b) {
        if (meta < 0) {
            throw new IllegalArgumentException("meta must be >= 0, got " + meta);
        }
        final int absorption = PackedColorLight.pack(15 - r, 15 - g, 15 - b);
        final int id = Block.getIdFromBlock(block);
        ensureCapacity(id);
        int[] entry = REGISTRY_BY_ID[id];
        if (entry == null) {
            entry = new int[Math.max(meta + 1, 16)];
        } else if (entry.length == 1) {
            final int wildcard = entry[0];
            entry = new int[Math.max(meta + 1, 16)];
            Arrays.fill(entry, wildcard);
        } else if (meta >= entry.length) {
            entry = Arrays.copyOf(entry, meta + 1);
        }
        entry[meta] = absorption + 1;
        REGISTRY_BY_ID[id] = entry;
        markEntry(block);
    }

    /**
     * Returns the packed absorption without checking the {@link ColoredTranslucency} interface. For blocks known not to implement it, or when coordinates are
     * unavailable.
     *
     * @param block block instance
     * @param meta  block metadata
     * @return packed absorption via {@link PackedColorLight#pack}
     */
    public static int getPackedAbsorptionNoInterface(Block block, int meta) {
        return getPackedAbsorptionNoInterface(Block.getIdFromBlock(block), block, meta);
    }

    /**
     * ID-accepting overload -- avoids redundant {@code Block.getIdFromBlock()} when caller already has the ID.
     */
    public static int getPackedAbsorptionNoInterface(int blockId, Block block, int meta) {
        if (blockId >= 0 && blockId < REGISTRY_BY_ID.length) {
            final int[] entry = REGISTRY_BY_ID[blockId];
            if (entry != null) {
                final int result = lookupRegistry(entry, meta);
                if (result >= 0) return result;
            }
        }
        // Vanilla fallback: uniform opacity across all channels
        // Vanilla minimum-1 attenuation: light decays by at least 1 per block
        final int opacity = Math.max(1, block.getLightOpacity());
        return PackedColorLight.pack(opacity, opacity, opacity);
    }

    /**
     * Resolve the packed absorption for a block at a position. Full lookup chain:
     * {@link PositionalColoredTranslucency} -> {@link ColoredTranslucency} -> registry -> vanilla fallback.
     * Does not handle directional concerns (FaceLightOcclusion, sided transparency) -- use
     * {@code FaceOcclusion.resolveAbsorption()} for that.
     *
     * @param world block access for positional queries
     * @param block block instance
     * @param meta  block metadata
     * @param x     block x
     * @param y     block y
     * @param z     block z
     * @return packed absorption via {@link PackedColorLight#pack}
     */
    public static int getPackedAbsorption(IBlockAccess world, Block block, int meta, int x, int y, int z) {
        if (block instanceof PositionalColoredTranslucency) {
            return PackedColorLight.transmittanceToAbsorption(
                    ((PositionalColoredTranslucency) block).getColoredTransmittance(world, meta, x, y, z));
        }
        if (block instanceof ColoredTranslucency) {
            return PackedColorLight.transmittanceToAbsorption(
                    ((ColoredTranslucency) block).getColoredTransmittance(meta));
        }
        return getPackedAbsorptionNoInterface(block, meta);
    }

    /**
     * Fast-path absorption lookup using the flat cache. Falls back to full lookup on UNCACHEABLE.
     *
     * @param blockId numeric block ID
     * @param block   block instance
     * @param meta    block metadata
     * @return packed absorption via {@link PackedColorLight#pack}, or {@code -1} if the block is uncacheable
     *         (positional or directional) and requires full resolution
     */
    public static int getPackedAbsorptionCached(int blockId, Block block, int meta) {
        if (blockId >= 0 && blockId < ABSORPTION_CACHE.length) {
            final int cached = ABSORPTION_CACHE[blockId];
            if (cached != UNCACHEABLE) return cached;
            final int[] perMeta = ABSORPTION_CACHE_PER_META[blockId];
            if (perMeta != null && meta >= 0 && meta < perMeta.length) return perMeta[meta];
        }
        return UNCACHEABLE;
    }

    /**
     * Mark a block ID as uncacheable in the absorption cache.
     * Must be called before {@link #buildCache()}.
     *
     * @param blockId numeric block ID
     */
    public static void markUncacheable(int blockId) {
        if (blockId >= 0) FORCE_UNCACHEABLE.set(blockId);
    }

    /**
     * Build flat absorption caches.
     */
    @SuppressWarnings("unchecked")
    public static void buildCache() {
        int maxId = 0;
        for (final Block block : (Iterable<Block>) GameData.getBlockRegistry()) {
            final int bid = Block.getIdFromBlock(block);
            if (bid > maxId) maxId = bid;
        }

        ABSORPTION_CACHE = new int[maxId + 1];
        ABSORPTION_CACHE_PER_META = new int[maxId + 1][];
        Arrays.fill(ABSORPTION_CACHE, UNCACHEABLE);

        int cachedUniform = 0, cachedPerMeta = 0;

        for (final Block block : (Iterable<Block>) GameData.getBlockRegistry()) {
            final int bid = Block.getIdFromBlock(block);
            if (bid < 0) continue;

            // PositionalColoredTranslucency -- fully uncacheable (needs world + position)
            if (block instanceof PositionalColoredTranslucency) {
                ABSORPTION_CACHE[bid] = UNCACHEABLE;
                continue;
            }

            // Externally marked uncacheable (FaceLightOcclusion, sided transparency)
            if (FORCE_UNCACHEABLE.get(bid)) {
                ABSORPTION_CACHE[bid] = UNCACHEABLE;
                continue;
            }

            // Static ColoredTranslucency -- probe meta 0-15, cache results
            if (block instanceof ColoredTranslucency ct) {
                boolean allSame = true;
                final int abs0 = PackedColorLight.transmittanceToAbsorption(ct.getColoredTransmittance(0));
                final int[] perMeta = new int[16];
                perMeta[0] = abs0;
                for (int m = 1; m < 16; m++) {
                    perMeta[m] = PackedColorLight.transmittanceToAbsorption(ct.getColoredTransmittance(m));
                    if (perMeta[m] != abs0) allSame = false;
                }
                if (allSame) {
                    ABSORPTION_CACHE[bid] = abs0;
                    cachedUniform++;
                } else {
                    ABSORPTION_CACHE[bid] = UNCACHEABLE;
                    ABSORPTION_CACHE_PER_META[bid] = perMeta;
                    cachedPerMeta++;
                }
                continue;
            }

            // Explicit registry per-meta entries
            if (hasExplicitEntry(bid)) {
                boolean allSame = true;
                final int abs0 = getPackedAbsorptionNoInterface(bid, block, 0);
                final int[] perMeta = new int[16];
                perMeta[0] = abs0;
                for (int m = 1; m < 16; m++) {
                    perMeta[m] = getPackedAbsorptionNoInterface(bid, block, m);
                    if (perMeta[m] != abs0) allSame = false;
                }
                if (allSame) {
                    ABSORPTION_CACHE[bid] = abs0;
                    cachedUniform++;
                } else {
                    ABSORPTION_CACHE[bid] = UNCACHEABLE;
                    ABSORPTION_CACHE_PER_META[bid] = perMeta;
                    cachedPerMeta++;
                }
                continue;
            }

            // Uniform scalar opacity -- no interface, no registry
            ABSORPTION_CACHE[bid] = getPackedAbsorptionNoInterface(bid, block, 0);
            cachedUniform++;
        }

        Supernova.LOG.info("TranslucencyRegistry: cached absorption for {} uniform + {} per-meta blocks", cachedUniform, cachedPerMeta);
    }

    /**
     * Returns {@code true} if the block has an explicit registry entry.
     */
    public static boolean hasExplicitEntry(Block block) {
        final int id = Block.getIdFromBlock(block);
        return id >= 0 && HAS_ENTRY.get(id);
    }

    /**
     * Returns {@code true} if the block ID has an explicit registry entry.
     */
    public static boolean hasExplicitEntry(int blockId) {
        return blockId >= 0 && HAS_ENTRY.get(blockId);
    }

    /** @return packed absorption, or -1 if not found */
    static int lookupRegistry(final int[] entry, final int meta) {
        if (entry.length == 1) return entry[0] - 1;
        if (meta >= 0 && meta < entry.length) {
            final int v = entry[meta];
            if (v != 0) return v - 1;
        }
        return -1;
    }

    /**
     * Iterate all explicit registry entries. Raw entry array values are {@code packedAbsorption + 1};
     * 0 marks unregistered slots. Length 1 = wildcard, length N = per-meta.
     */
    public static void forEach(BiConsumer<Block, int[]> consumer) {
        for (int id = HAS_ENTRY.nextSetBit(0); id >= 0; id = HAS_ENTRY.nextSetBit(id + 1)) {
            if (id >= REGISTRY_BY_ID.length) break;
            final int[] entry = REGISTRY_BY_ID[id];
            if (entry == null) continue;
            final Block block = Block.getBlockById(id);
            if (block != null) consumer.accept(block, entry);
        }
    }

    private static void markEntry(Block block) {
        final int id = Block.getIdFromBlock(block);
        if (id >= 0) HAS_ENTRY.set(id);
    }

    private static void ensureCapacity(int id) {
        if (id >= 0 && id >= REGISTRY_BY_ID.length) {
            REGISTRY_BY_ID = Arrays.copyOf(REGISTRY_BY_ID, Math.max(id + 1, REGISTRY_BY_ID.length * 2));
        }
    }
}
