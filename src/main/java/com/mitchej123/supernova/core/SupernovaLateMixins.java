package com.mitchej123.supernova.core;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;

import java.util.List;
import java.util.Set;

@LateMixin
@SuppressWarnings("unused")
public class SupernovaLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.supernova.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return IMixins.getLateMixins(LateMixins.class, loadedMods);
    }
}
