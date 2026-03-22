package com.mitchej123.supernova.core;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

import javax.annotation.Nonnull;

public enum Mixins implements IMixins {

    ENGINE(new MixinBuilder("Supernova light engine")
            .addCommonMixins(
                    "early.engine.MixinChunk",
                    "early.engine.MixinWorld",
                    "early.engine.MixinWorldServer")
            .addClientMixins(
                    "early.engine.MixinPlayerControllerMP"
            )
            .setPhase(Phase.EARLY)
    ),

    VANILLA_RENDERING(new MixinBuilder("Vanilla colored light rendering").
            addClientMixins("early.rendering.MixinRenderBlocks")
            .addExcludedMod(TargetedMod.ANGELICA)
            .setPhase(Phase.EARLY)
    ),

    ENTITY_RENDERING(new MixinBuilder("Entity colored light rendering")
            .addClientMixins("early.rendering.MixinRenderManager")
            .setPhase(Phase.EARLY)
    );

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
