package com.redpxnda.handson.mixin;

import com.redpxnda.handson.client.TinkeringScreen;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPosition(double x, double y, double z);

    @Shadow protected abstract void setRotation(float f, float g);

    @Shadow private boolean detached;

    @Shadow public abstract Vec3 getPosition();

    @Shadow protected abstract void setPosition(Vec3 vec3);

    @Inject(method = "setup", at = @At("TAIL"))
    private void postCameraSetup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof TinkeringScreen sc) {
            Vec3 pos = sc.getCamPos(getPosition());
            setPosition(pos);
            setRotation(sc.getWorkbenchDirection().toYRot(), 90);
            detached = true;
        }
    }
}
