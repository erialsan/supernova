package coloredlightscore.src.api;

import net.minecraft.block.Block;

/**
 * Stub implementation of the EasyColoredLights / ColoredLightsCore API.
 * <p>
 * Original API by CptSpaceToaster.
 */
@SuppressWarnings("unused")
public class CLApi {

    public static float[] l = new float[] { 0F, 1F / 15, 2F / 15, 3F / 15, 4F / 15, 5F / 15, 6F / 15, 7F / 15, 8F / 15, 9F / 15, 10F / 15, 11F / 15, 12F / 15, 13F / 15, 14F / 15, 1F };

    public static int[] r = new int[] { 0, 15, 0, 8, 0, 10, 0, 10, 5, 15, 8, 15, 0, 15, 15, 15 };
    public static int[] g = new int[] { 0, 0, 15, 3, 0, 0, 15, 10, 5, 10, 15, 15, 8, 0, 12, 15 };
    public static int[] b = new int[] { 0, 0, 0, 0, 15, 15, 15, 10, 5, 13, 0, 0, 15, 15, 10, 15 };

    /** Packed format: {@code 0RRRR 0GGGG 0BBBB 0LLLL}. Brightness = max(R,G,B). */
    public static int makeRGBLightValue(float r, float g, float b) {
        r = Math.max(0f, Math.min(1f, r));
        g = Math.max(0f, Math.min(1f, g));
        b = Math.max(0f, Math.min(1f, b));
        int brightness = (int) (15.0f * Math.max(Math.max(r, g), b));
        return brightness | ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
    }

    /** Packed format: {@code 0RRRR 0GGGG 0BBBB 0LLLL}. Brightness = max(R,G,B). */
    public static int makeRGBLightValue(int r, int g, int b) {
        r = Math.max(0, Math.min(15, r));
        g = Math.max(0, Math.min(15, g));
        b = Math.max(0, Math.min(15, b));
        int brightness = Math.max(Math.max(r, g), b);
        return brightness | ((b << 15) + (g << 10) + (r << 5));
    }

    @Deprecated
    public static int makeRGBLightValue(float r, float g, float b, float currentLightValue) {
        r = Math.max(0f, Math.min(1f, r));
        g = Math.max(0f, Math.min(1f, g));
        b = Math.max(0f, Math.min(1f, b));
        int brightness = (int) (currentLightValue * 15.0f) & 0xf;
        return brightness | ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
    }

    @Deprecated
    public static int makeRGBLightValue(int r, int g, int b, int brightness) {
        r = Math.max(0, Math.min(15, r));
        g = Math.max(0, Math.min(15, g));
        b = Math.max(0, Math.min(15, b));
        brightness &= 0xf;
        return brightness | ((b << 15) + (g << 10) + (r << 5));
    }

    public static Block setBlockColorRGB(Block block, int r, int g, int b) {
        block.setLightLevel(((float) makeRGBLightValue(r, g, b)) / 15F);
        return block;
    }

    public static Block setBlockColorRGB(Block block, float r, float g, float b) {
        block.setLightLevel(((float) makeRGBLightValue(r, g, b)) / 15F);
        return block;
    }

    @Deprecated
    public static Block setBlockColorRGB(Block block, int r, int g, int b, int lightValue) {
        block.setLightLevel(((float) makeRGBLightValue(r, g, b, lightValue)) / 15F);
        return block;
    }

    @Deprecated
    public static Block setBlockColorRGB(Block block, float r, float g, float b, float lightValue) {
        block.setLightLevel(((float) makeRGBLightValue(r, g, b, lightValue)) / 15F);
        return block;
    }
}
