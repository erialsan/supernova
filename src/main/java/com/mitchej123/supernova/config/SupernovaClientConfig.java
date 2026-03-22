package com.mitchej123.supernova.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.mitchej123.supernova.client.TintBlendMode;

@Config(modid = "supernova", filename = "supernova", category = "client")
public final class SupernovaClientConfig {

    @Config.Comment("Tint blend mode for combining block and sky light color.")
    @Config.DefaultEnum("SQUARED_WEIGHT")
    public static TintBlendMode tintBlendMode;
}
