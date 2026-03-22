package com.mitchej123.supernova.mixin.early.rendering;

import com.mitchej123.supernova.client.EntityColoredLightHelper;
import com.mitchej123.supernova.config.SupernovaConfig;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Unique
    private int supernova$tintDepth = 0;

    /**
     * Apply colored light tint before entity rendering. Entities that render block models internally (falling blocks, TNT, minecarts) are excluded to avoid
     * double-tinting with MixinRenderBlocks.
     */
    @Inject(method = "func_147939_a", at = @At("HEAD"))
    private void supernova$applyTint(Entity entity, double x, double y, double z, float yaw, float partialTicks, boolean pass,
            CallbackInfoReturnable<Boolean> cir) {
        if (SupernovaConfig.isScalarMode()) return;
        if (entity instanceof EntityFallingBlock || entity instanceof EntityTNTPrimed || entity instanceof EntityMinecart) {
            return;
        }

        final float[] tint = EntityColoredLightHelper.getEntityTint(entity.worldObj, entity.posX, entity.posY, entity.posZ, entity.getEyeHeight());
        if (tint[0] < 1f || tint[1] < 1f || tint[2] < 1f) {
            EntityColoredLightHelper.applyEntityTint(tint);
            supernova$tintDepth++;
        }
    }

    @Inject(method = "func_147939_a", at = @At("RETURN"))
    private void supernova$removeTint(Entity entity, double x, double y, double z, float yaw, float partialTicks, boolean pass,
            CallbackInfoReturnable<Boolean> cir) {
        if (supernova$tintDepth > 0) {
            supernova$tintDepth--;
            if (supernova$tintDepth == 0) {
                EntityColoredLightHelper.removeEntityTint();
            }
        }
    }
}
