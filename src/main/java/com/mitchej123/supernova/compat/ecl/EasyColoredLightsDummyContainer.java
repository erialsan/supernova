package com.mitchej123.supernova.compat.ecl;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;

import java.util.Collections;

/**
 * Fake mod container for {@code "easycoloredlights"} compat.
 */
public class EasyColoredLightsDummyContainer extends DummyModContainer {

    public EasyColoredLightsDummyContainer() {
        super(new ModMetadata());
        final ModMetadata meta = getMetadata();
        meta.modId = "easycoloredlights";
        meta.name = "Easy Colored Lights (Supernova Compat Stub)";
        meta.version = "1.0.0";
        meta.authorList = Collections.singletonList("Supernova");
        meta.description = "Stub provided by Supernova to leverage EasyColoredLights compat methods in other mods.";
    }
}
