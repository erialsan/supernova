package com.mitchej123.supernova.mixin.early.engine;

import com.mitchej123.supernova.world.SupernovaWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Final
    @Shadow
    private Minecraft mc;

    @Inject(method = "onPlayerDestroyBlock", at = @At("HEAD"))
    private void supernova$onDestroyHead(int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (this.mc.theWorld != null) {
            ((SupernovaWorld) this.mc.theWorld).supernova$setPlayerAction(true);
        }
    }

    @Inject(method = "onPlayerDestroyBlock", at = @At("RETURN"))
    private void supernova$onDestroyReturn(int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (this.mc.theWorld != null) {
            ((SupernovaWorld) this.mc.theWorld).supernova$setPlayerAction(false);
        }
    }

    @Inject(method = "onPlayerRightClick", at = @At("HEAD"))
    private void supernova$onRightClickHead(CallbackInfoReturnable<Boolean> cir) {
        if (this.mc.theWorld != null) {
            ((SupernovaWorld) this.mc.theWorld).supernova$setPlayerAction(true);
        }
    }

    @Inject(method = "onPlayerRightClick", at = @At("RETURN"))
    private void supernova$onRightClickReturn(CallbackInfoReturnable<Boolean> cir) {
        if (this.mc.theWorld != null) {
            ((SupernovaWorld) this.mc.theWorld).supernova$setPlayerAction(false);
        }
    }
}
