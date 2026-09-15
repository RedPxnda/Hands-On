package com.redpxnda.handson.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import smartin.miapi.Miapi;
import smartin.miapi.item.modular.Transform;

/**
 * Utilities for converting between framebuffer coordinates and world-space
 * coordinates using the exact camera matrices of the current renderFromBer frame.
 * <p>
 * Minecraft world rendering uses camera-relative coordinates. The methods
 * here expose world-space results by adding the current camera position.
 */
public final class WorldUiProjection {

    private WorldUiProjection() {
    }

    /**
     * A world-space ray.
     */
    public record Ray(Vector3d origin, Vector3d direction) {
    }

    /**
     * A snapshot of the matrices used for one world-renderFromBer frame.
     * <p>
     * Capture this during world rendering and use the same instance for all
     * UI interaction during that frame.
     */
    public record Frame(
            Matrix4f projection,
            Matrix4f modelView,
            Matrix4f inverseViewProjection,
            Vector3d cameraPosition,
            int framebufferWidth,
            int framebufferHeight
    ) {

        public Frame {
            projection = new Matrix4f(projection);
            modelView = new Matrix4f(modelView);
            inverseViewProjection = new Matrix4f(inverseViewProjection);
            cameraPosition = new Vector3d(cameraPosition);
        }

        /**
         * Create a frame from the matrices currently installed in RenderSystem.
         * <p>
         * Call this while the world-renderFromBer matrices are active.
         */
        public static Frame capture(Camera camera, PoseStack poseStack) {
            Matrix4f projection = new Matrix4f(
                    new Matrix4f(RenderSystem.getProjectionMatrix()).mul0(RenderSystem.getModelViewMatrix())
            );

            Matrix4f modelView = new Matrix4f(
                    poseStack.last().pose()
            );

            Matrix4f inverse = new Matrix4f(projection)
                    .mul(modelView)
                    .invert();

            Minecraft minecraft = Minecraft.getInstance();

            return new Frame(
                    projection,
                    modelView,
                    inverse,
                    from(camera.getPosition()),
                    minecraft.getWindow().getWidth(),
                    minecraft.getWindow().getHeight()
            );
        }

        public static Vector3d from(Vec3 v) {
            return new Vector3d(v.x, v.y, v.z);
        }


        /**
         * Convert framebuffer coordinates into a camera-relative world ray.
         * <p>
         * mouseX and mouseY must be framebuffer coordinates.
         */
        public Ray screenToWorld(double mouseX, double mouseY) {
            return WorldUiProjection.screenToWorld(
                    mouseX,
                    mouseY,
                    inverseViewProjection,
                    cameraPosition,
                    framebufferWidth,
                    framebufferHeight
            );
        }

        /**
         * Convert logical Minecraft mouse coordinates into framebuffer
         * coordinates.
         */
        public double framebufferX(double mouseX) {
            return mouseX * framebufferWidth /
                   (double) Minecraft.getInstance().getWindow().getGuiScaledWidth();
        }

        public double framebufferY(double mouseY) {
            return mouseY * framebufferHeight /
                   (double) Minecraft.getInstance().getWindow().getGuiScaledHeight();
        }

        /**
         * Convert normal Screen mouse coordinates directly into a world ray.
         */
        public Ray guiMouseToWorld(double mouseX, double mouseY) {
            return screenToWorld(
                    framebufferX(mouseX),
                    framebufferY(mouseY)
            );
        }
    }

    /**
     * Capture the current world-renderFromBer camera state.
     */
    public static Frame capture(Camera camera, PoseStack poseStack) {
        return Frame.capture(camera, poseStack);
    }

    /**
     * Convert framebuffer coordinates to a world-space ray.
     */
    public static Ray screenToWorld(
            double mouseX,
            double mouseY,
            Matrix4f inverseViewProjection,
            Vector3d cameraPosition,
            int framebufferWidth,
            int framebufferHeight
    ) {
        float ndcX = (float) (
                mouseX / framebufferWidth * 2.0 - 1.0
        );

        float ndcY = (float) (
                1.0 - mouseY / framebufferHeight * 2.0
        );

        Vector4f near = new Vector4f(ndcX, ndcY, -1.0f, 1.0f);
        Vector4f far = new Vector4f(ndcX, ndcY, 1.0f, 1.0f);

        inverseViewProjection.transform(near);
        inverseViewProjection.transform(far);

        near.div(near.w);
        far.div(far.w);

        //Vector3d nearWorld = new Vector3d(near.x + cameraPosition.x, near.y + cameraPosition.y, near.z + cameraPosition.z);
        //Vector3d farWorld = new Vector3d(far.x + cameraPosition.x, far.y + cameraPosition.y, far.z + cameraPosition.z);

        Vector3d nearWorld = new Vector3d(near.x, near.y, near.z);
        Vector3d farWorld = new Vector3d(far.x, far.y, far.z);


        return new Ray(
                nearWorld,
                new Vector3d(farWorld)
                        .sub(nearWorld)
                        .normalize()
        );
    }

    /**
     * Intersect a world ray with a plane.
     *
     * @param ray         the world-space ray
     * @param planeOrigin a point on the plane
     * @param planeNormal normalized plane normal
     * @return intersection point, or null if there is no intersection
     */
    public static Vector3d intersectPlane(
            Ray ray,
            Vector3d planeOrigin,
            Vector3d planeNormal
    ) {
        double denominator = ray.direction().dot(planeNormal);

        if (Math.abs(denominator) < 1.0E-8) {
            return null;
        }

        Vector3d toPlane = new Vector3d(planeOrigin)
                .sub(ray.origin());

        double t = toPlane.dot(planeNormal);

        t /= denominator;

        if (t < 0.0) {
            return null;
        }

        return new Vector3d(ray.origin())
                .fma(t, ray.direction());
    }

    /**
     * Convert a world point into local coordinates using the inverse of
     * the supplied world transform.
     */
    public static Vector3f worldToLocal(
            Vector3d world,
            Matrix4f worldTransform
    ) {
        Matrix4f inverse = new Matrix4f(worldTransform).invert();

        Vector4f point = new Vector4f(
                (float) world.x,
                (float) world.y,
                (float) world.z,
                1.0f
        );

        inverse.transform(point);

        point.div(point.w);

        return new Vector3f(
                point.x,
                point.y,
                point.z
        );
    }

    /**
     * Convert a local point to world coordinates.
     */
    public static Vector3d localToWorld(
            Vector3f local,
            Matrix4f worldTransform
    ) {
        Vector4f point = new Vector4f(
                local.x,
                local.y,
                local.z,
                1.0f
        );

        worldTransform.transform(point);

        point.div(point.w);

        return new Vector3d(
                point.x,
                point.y,
                point.z
        );
    }

    /**
     * Build the world transform of a UI plane.
     * <p>
     * The local UI plane is:
     * <p>
     * X = right
     * Y = down
     * Z = toward the viewer
     * <p>
     * The supplied transform determines where the plane exists in the world.
     */
    public static Matrix4f createPlaneTransform(
            Vector3d origin,
            Vector3f right,
            Vector3f down,
            float scale
    ) {
        Vector3f normal = new Vector3f(right)
                .cross(down)
                .normalize();

        return new Matrix4f()
                .identity()
                .m00(right.x * scale)
                .m01(down.x * scale)
                .m02(normal.x * scale)
                .m10(right.y * scale)
                .m11(down.y * scale)
                .m12(normal.y * scale)
                .m20(right.z * scale)
                .m21(down.z * scale)
                .m22(normal.z * scale)
                .m30((float) origin.x)
                .m31((float) origin.y)
                .m32((float) origin.z);
    }
}