package com.mitchej123.supernova.world;

import com.mitchej123.supernova.api.ExtendedWorld;
import com.mitchej123.supernova.light.WorldLightManager;

/**
 * Internal extension of {@link ExtendedWorld} exposing engine internals. Mixed into {@code net.minecraft.world.World}.
 */
public interface SupernovaWorld extends ExtendedWorld {

    WorldLightManager supernova$getLightManager();

    void supernova$shutdown();

    void supernova$setPlayerAction(boolean value);

    boolean supernova$isPlayerAction();
}
