package com.redpxnda.handson.client;

import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import smartin.miapi.item.modular.Transform;

public interface MovingCinematicScreen {
    InterpolateMode START_ANIM =
            new InterpolateMode.EaseInOut(4);

    void onCloseReal();

    default Vec3 getCamPos(Vec3 initial) {
        Vec3 target = getTargetPosition();

        double targetX = target.x;
        double targetY = target.y;
        double targetZ = target.z;

        if (getAnimationTick() < 15) {
            float partial = Minecraft.getInstance()
                    .getTimer()
                    .getGameTimeDeltaPartialTick(true);

            if (isClosing()) {
                partial = -partial;
            }

            float prog = Math.min((getAnimationTick() + partial) / 15f, 1f);

            return new Vec3(
                    START_ANIM.interpolate(prog, initial.x, targetX),
                    START_ANIM.interpolate(prog, initial.y, targetY),
                    START_ANIM.interpolate(prog, initial.z, targetZ)
            );
        }
        Minecraft minecraft = Minecraft.getInstance();
        double mX = minecraft.mouseHandler.xpos();
        double mY = minecraft.mouseHandler.ypos();

        double width = minecraft.getWindow().getWidth();
        double height = minecraft.getWindow().getHeight();

        Vec2 mouse = new Vec2(
                (float) (mX - width / 2),
                (float) (mY - height / 2)
        );

        Vec2 normalized = mouse.normalized();
        float prog = mouse.length() / 2000f;

        Vec3 right = getRightVector();
        Vec3 down = getDownVector();

        Vec3 offset = right.scale(normalized.x).scale(0.3f)
                .add(down.scale(normalized.y));

        return new Vec3(
                MathUtil.lerp(prog, targetX, targetX - offset.x),
                MathUtil.lerp(prog, targetY, targetY - offset.y),
                MathUtil.lerp(prog, targetZ, targetZ - offset.z)
        );
    }

    int getAnimationTick();
    void setAnimationTick(int newTicks);
    boolean isClosing();
    void setClosing(boolean isClosing);

    /**
     * Gets the target position from the Transform.
     * <p>
     * Transform uses Vector3f internally.
     * Camera positions use Minecraft's Vec3.
     */
    default Vec3 getTargetPosition() {
        var translation = getTargetTransform().getTranslation();

        return new Vec3(translation.x(), translation.y(), translation.z());
    }

    default Matrix4f getInverseCinematicMatrix() {
        Vec3 pos = getTargetPosition();
        Vector2f angles = getCamAngles();

        float yaw = angles.x();
        float pitch = angles.y();

        return new Matrix4f()
                .translate((float) -pos.x, (float) -pos.y, (float) -pos.z)
                .rotateY((float) Math.toRadians(yaw))
                .rotateX((float) Math.toRadians(pitch));
    }

    default Vec3 getForwardVector() {
        float pitch = getTargetTransform().getRotation().x();
        float yaw = getTargetTransform().getRotation().y();

        double pitchRad = Math.toRadians(pitch);
        double yawRad = Math.toRadians(yaw);

        return new Vec3(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                -Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        ).normalize();
    }

    default Vec3 getRightVector() {
        Vec3 forward = getForwardVector();

        // World up × forward = camera right.
        return new Vec3(0, 1, 0)
                .cross(forward)
                .normalize();
    }

    default Vector2f getCamAngles(){

        var transform = this.getLookingTransform();
        var rotation = transform.getRotation();
        if (Math.abs(Math.abs(rotation.z()) - 180f) < 1 && Math.abs(Math.abs(rotation.z()) - 180f) > -1) {
            rotation.x = rotation.x + 180f;
            rotation.y = rotation.y + 180f;
        }
        return new Vector2f(rotation.y(), rotation.x());
    }

    default Vec3 getDownVector() {
        Vec3 forward = getForwardVector();
        Vec3 right = getRightVector();

        // Forward × right = camera down.
        return forward.cross(right).normalize();
    }

    default void animationTick(){
        if (getAnimationTick() < 15 && !isClosing()) {
            setAnimationTick(getAnimationTick()+1);
        } else if (isClosing()) {
            if (getAnimationTick() > 4) {
                setAnimationTick(getAnimationTick()-1);
            } else {
                onCloseReal();
            }
        }
    }

    Transform getTargetTransform();

    boolean isPauseScreen();

    Transform getLookingTransform();
}
