package com.mitchej123.supernova.compat.endlessids;

import com.falsepattern.endlessids.mixin.helpers.SubChunkBlockHook;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class EndlessIDsCompat {

    private static final class Holder {
        static final boolean PRESENT = EndlessIDsCompat.class.getClassLoader().getResource("com/falsepattern/endlessids/mixin/helpers/SubChunkBlockHook.class") != null;
    }

    public static boolean isPresent() {
        return Holder.PRESENT;
    }

    /**
     * Populate EID block-ID cache arrays from a section.
     */
    public static void populateCache(final ExtendedBlockStorage section, final int idx, final byte[][] blockB1Cache, final NibbleArray[] blockB2LowCache,
            final int[] blockMaskCache) {
        if (Holder.PRESENT && section != null) {
            populateCacheEID(section, idx, blockB1Cache, blockB2LowCache, blockMaskCache);
        } else {
            blockB1Cache[idx] = null;
            blockB2LowCache[idx] = null;
            blockMaskCache[idx] = 0;
        }
    }

    private static void populateCacheEID(final ExtendedBlockStorage section, final int idx, final byte[][] blockB1Cache, final NibbleArray[] blockB2LowCache,
            final int[] blockMaskCache) {
        if (section instanceof SubChunkBlockHook hook) {
            blockB1Cache[idx] = hook.eid$getB1();
            blockB2LowCache[idx] = hook.eid$getB2Low();
            blockMaskCache[idx] = hook.eid$getBlockMask();
        } else {
            blockB1Cache[idx] = null;
            blockB2LowCache[idx] = null;
            blockMaskCache[idx] = 0;
        }
    }
}
