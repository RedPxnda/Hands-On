package com.redpxnda.handson.client.widgets;

import com.redpxnda.nucleus.math.InterpolateMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import smartin.miapi.client.gui.TransformableWidget;

public class InterpolatingTransformableWidget extends TransformableWidget {
    private final Matrix4f previousTransform = new Matrix4f();
    private final Matrix4f targetTransform = new Matrix4f();

    private InterpolateMode interpolation = InterpolateMode.COS;
    private float progress = 1.0f;

    public InterpolatingTransformableWidget(
            int x,
            int y,
            int width,
            int height,
            Component title
    ) {
        super(x, y, width, height, title);

        previousTransform.set(rawProjection);
        targetTransform.set(rawProjection);
    }

    public void setInterpolation(InterpolateMode interpolation) {
        this.interpolation = interpolation;
    }

    public InterpolateMode getInterpolation() {
        return interpolation;
    }

    public void setTransform(Matrix4f transform) {
        previousTransform.set(targetTransform);
        targetTransform.set(transform);
        progress = 0.0f;
    }

    public void setTransform(Matrix4f transform, boolean instant) {
        if (instant) {
            previousTransform.set(transform);
            targetTransform.set(transform);
            rawProjection.set(transform);
            progress = 1.0f;
        } else {
            setTransform(transform);
        }
    }

    public void tick() {
        if (progress < 1.0f) {
            progress = Math.min(progress + 0.1f, 1.0f);
        }
    }

    @Override
    public void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        updateTransform(partialTick);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }

    private void updateTransform(float partialTick) {
        float delta = Math.min(progress + partialTick * 0.1f, 1.0f);

        Vector3f previousTranslation = previousTransform.getTranslation(new Vector3f());
        Vector3f targetTranslation = targetTransform.getTranslation(new Vector3f());

        Vector3f previousScale = previousTransform.getScale(new Vector3f());
        Vector3f targetScale = targetTransform.getScale(new Vector3f());

        Quaternionf previousRotation = previousTransform.getUnnormalizedRotation(new Quaternionf());
        Quaternionf targetRotation = targetTransform.getUnnormalizedRotation(new Quaternionf());

        float t = (float) interpolation.interpolate(
                delta,
                0.0,
                1.0
        );

        Vector3f translation = new Vector3f(
                (float) interpolation.interpolate(t, previousTranslation.x, targetTranslation.x),
                (float) interpolation.interpolate(t, previousTranslation.y, targetTranslation.y),
                (float) interpolation.interpolate(t, previousTranslation.z, targetTranslation.z)
        );

        Vector3f scale = new Vector3f(
                (float) interpolation.interpolate(t, previousScale.x, targetScale.x),
                (float) interpolation.interpolate(t, previousScale.y, targetScale.y),
                (float) interpolation.interpolate(t, previousScale.z, targetScale.z)
        );

        Quaternionf rotation = previousRotation.slerp(
                targetRotation,
                t,
                new Quaternionf()
        );

        rawProjection.identity()
                .translate(translation)
                .rotate(rotation)
                .scale(scale);
    }
}