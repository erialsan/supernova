package com.mitchej123.supernova.mixin.early.rendering;

import com.mitchej123.supernova.client.ColoredLightHelper;
import com.mitchej123.supernova.config.SupernovaConfig;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks {

    @Shadow
    public IBlockAccess blockAccess;

    // Vertex color fields set per-face by the AO methods
    @Shadow
    public float colorRedTopLeft;
    @Shadow
    public float colorRedBottomLeft;
    @Shadow
    public float colorRedBottomRight;
    @Shadow
    public float colorRedTopRight;
    @Shadow
    public float colorGreenTopLeft;
    @Shadow
    public float colorGreenBottomLeft;
    @Shadow
    public float colorGreenBottomRight;
    @Shadow
    public float colorGreenTopRight;
    @Shadow
    public float colorBlueTopLeft;
    @Shadow
    public float colorBlueBottomLeft;
    @Shadow
    public float colorBlueBottomRight;
    @Shadow
    public float colorBlueTopRight;

    @Shadow
    public abstract void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon);

    @Shadow
    public abstract void renderFaceYPos(Block block, double x, double y, double z, IIcon icon);

    @Shadow
    public abstract void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon);

    @Shadow
    public abstract void renderFaceZPos(Block block, double x, double y, double z, IIcon icon);

    @Shadow
    public abstract void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon);

    @Shadow
    public abstract void renderFaceXPos(Block block, double x, double y, double z, IIcon icon);

    @Shadow
    public abstract boolean renderStandardBlockWithColorMultiplier(Block block, int x, int y, int z, float r, float g, float b);

    @Redirect(
            method = "renderStandardBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderStandardBlockWithColorMultiplier(Lnet/minecraft/block/Block;IIIFFF)Z"))
    private boolean supernova$tintFlat(RenderBlocks self, Block block, int x, int y, int z, float r, float g, float b) {
        if (SupernovaConfig.isScalarMode()) {
            return this.renderStandardBlockWithColorMultiplier(block, x, y, z, r, g, b);
        }
        final float[] tint = ColoredLightHelper.getBlockTint(x, y, z);
        return this.renderStandardBlockWithColorMultiplier(block, x, y, z, r * tint[0], g * tint[1], b * tint[2]);
    }


    private void supernova$applyVertexTints(int x, int y, int z, int face) {
        if (SupernovaConfig.isScalarMode()) return;
        final float[][] tints = ColoredLightHelper.computeVertexTints(x, y, z, face);
        this.colorRedTopLeft *= tints[0][0];
        this.colorGreenTopLeft *= tints[0][1];
        this.colorBlueTopLeft *= tints[0][2];
        this.colorRedBottomLeft *= tints[1][0];
        this.colorGreenBottomLeft *= tints[1][1];
        this.colorBlueBottomLeft *= tints[1][2];
        this.colorRedBottomRight *= tints[2][0];
        this.colorGreenBottomRight *= tints[2][1];
        this.colorBlueBottomRight *= tints[2][2];
        this.colorRedTopRight *= tints[3][0];
        this.colorGreenTopRight *= tints[3][1];
        this.colorBlueTopRight *= tints[3][2];
    }

    @Redirect(
            method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceYNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V",
            ordinal = 0))
    private void supernova$tintAOYNeg(RenderBlocks self, Block block, double x, double y, double z, IIcon icon) {
        supernova$applyVertexTints(MathHelper.floor_double(x), (int) y, MathHelper.floor_double(z), 0);
        this.renderFaceYNeg(block, x, y, z, icon);
    }

    @Redirect(
            method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceYPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V",
            ordinal = 0))
    private void supernova$tintAOYPos(RenderBlocks self, Block block, double x, double y, double z, IIcon icon) {
        supernova$applyVertexTints(MathHelper.floor_double(x), (int) y, MathHelper.floor_double(z), 1);
        this.renderFaceYPos(block, x, y, z, icon);
    }

    @Redirect(
            method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceZNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V",
            ordinal = 0))
    private void supernova$tintAOZNeg(RenderBlocks self, Block block, double x, double y, double z, IIcon icon) {
        supernova$applyVertexTints(MathHelper.floor_double(x), (int) y, MathHelper.floor_double(z), 2);
        this.renderFaceZNeg(block, x, y, z, icon);
    }

    @Redirect(
            method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceZPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V",
            ordinal = 0))
    private void supernova$tintAOZPos(RenderBlocks self, Block block, double x, double y, double z, IIcon icon) {
        supernova$applyVertexTints(MathHelper.floor_double(x), (int) y, MathHelper.floor_double(z), 3);
        this.renderFaceZPos(block, x, y, z, icon);
    }

    @Redirect(
            method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceXNeg(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V",
            ordinal = 0))
    private void supernova$tintAOXNeg(RenderBlocks self, Block block, double x, double y, double z, IIcon icon) {
        supernova$applyVertexTints(MathHelper.floor_double(x), (int) y, MathHelper.floor_double(z), 4);
        this.renderFaceXNeg(block, x, y, z, icon);
    }

    @Redirect(
            method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderFaceXPos(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V",
            ordinal = 0))
    private void supernova$tintAOXPos(RenderBlocks self, Block block, double x, double y, double z, IIcon icon) {
        supernova$applyVertexTints(MathHelper.floor_double(x), (int) y, MathHelper.floor_double(z), 5);
        this.renderFaceXPos(block, x, y, z, icon);
    }
}
