package com.mitchej123.supernova.api;

/**
 * Named color palette for common light sources and dye colors. All values are packed via {@link PackedColorLight#pack}.
 */
public final class LightColors {

    public static final int WARM_AMBER = PackedColorLight.pack(13, 10, 8);
    public static final int GOLDEN = PackedColorLight.pack(15, 13, 3);
    public static final int BRIGHT_CYAN = PackedColorLight.pack(9, 14, 15);
    public static final int VIVID_PURPLE = PackedColorLight.pack(14, 6, 15);
    public static final int WARM_PINK = PackedColorLight.pack(15, 10, 13);
    public static final int DIM_GRAY = PackedColorLight.pack(9, 9, 9);
    public static final int BRIGHT_GRAY = PackedColorLight.pack(12, 12, 12);
    public static final int WARM_WHITE = PackedColorLight.pack(15, 14, 11);

    public static final int DYE_WHITE = PackedColorLight.pack(15, 15, 15);
    public static final int DYE_ORANGE = PackedColorLight.pack(15, 12, 10);
    public static final int DYE_MAGENTA = PackedColorLight.pack(15, 0, 15);
    public static final int DYE_LIGHT_BLUE = PackedColorLight.pack(0, 8, 15);
    public static final int DYE_YELLOW = PackedColorLight.pack(15, 15, 0);
    public static final int DYE_LIME = PackedColorLight.pack(8, 15, 0);
    public static final int DYE_PINK = PackedColorLight.pack(15, 7, 10);
    public static final int DYE_GRAY = PackedColorLight.pack(5, 5, 5);
    public static final int DYE_LIGHT_GRAY = PackedColorLight.pack(10, 10, 10);
    public static final int DYE_CYAN = PackedColorLight.pack(0, 15, 15);
    public static final int DYE_PURPLE = PackedColorLight.pack(10, 0, 15);
    public static final int DYE_BLUE = PackedColorLight.pack(0, 0, 15);
    public static final int DYE_BROWN = PackedColorLight.pack(8, 3, 0);
    public static final int DYE_GREEN = PackedColorLight.pack(0, 15, 0);
    public static final int DYE_RED = PackedColorLight.pack(15, 0, 0);
    public static final int DYE_BLACK = PackedColorLight.pack(1, 1, 1);

    /** All 16 dye colors at full brightness, indexed by wool meta (0=white .. 15=black). */
    public static final int[] BRIGHT_DYE_PALETTE = {
        DYE_WHITE, DYE_ORANGE, DYE_MAGENTA, DYE_LIGHT_BLUE,
        DYE_YELLOW, DYE_LIME, DYE_PINK, DYE_GRAY,
        DYE_LIGHT_GRAY, DYE_CYAN, DYE_PURPLE, DYE_BLUE,
        DYE_BROWN, DYE_GREEN, DYE_RED, DYE_BLACK,
    };

    /** All 16 dye colors at half brightness, indexed by wool meta (0=white .. 15=black). */
    public static final int[] DIM_DYE_PALETTE = new int[16];

    static {
        for (int i = 0; i < 16; i++) {
            DIM_DYE_PALETTE[i] = dim(BRIGHT_DYE_PALETTE[i]);
        }
    }

    /** MC dye damage order -> wool meta index. dye 0=inkSack(black)=wool 15, dye 15=boneMeal(white)=wool 0. */
    public static final int[] DYE_TO_WOOL = { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

    /**
     * Halve each channel, rounding up: {@code (c + 1) >> 1}.
     *
     * @param packed packed RGB
     * @return packed RGB with each channel halved
     */
    public static int dim(int packed) {
        int r = (PackedColorLight.red(packed) + 1) >> 1;
        int g = (PackedColorLight.green(packed) + 1) >> 1;
        int b = (PackedColorLight.blue(packed) + 1) >> 1;
        return PackedColorLight.pack(r, g, b);
    }


    private LightColors() {}
}
