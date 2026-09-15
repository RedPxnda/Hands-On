package com.redpxnda.handson.client.widgets;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import smartin.miapi.client.gui.InteractAbleWidget;

import java.lang.Math;
import java.util.List;

public class WorldWidget extends InteractAbleWidget {

    protected final Matrix4f localWorldTransform = new Matrix4f();
    public Matrix4f localWidgetTransform = new Matrix4f();
    UiAnchor topLeftProjection = new UiAnchor(new Matrix4f(), new Matrix4f(), new Matrix4f(), new Vector3f());

    protected WorldWidget(
            int width,
            int height,
            Component title
    ) {
        super(0, 0, width, height, title);
    }

    public WorldWidget(int width, int height) {
        this(width, height, Component.empty());
    }

    public void setLocalWorldTransform(Matrix4f transform) {
        localWorldTransform.set(transform);
    }

    int currentMouseX;
    int currentMouseY;
    int lastMouseX;
    int lastMouseY;

    public final void renderInWorld(
            GuiGraphics guiGraphics,
            float partialTick
    ) {
        guiGraphics.fill(0, 0, getWidth(), getHeight(), Color.GREEN.withAlpha(0.3f).argb());
        topLeftProjection = getPoint(getX(), getY(), guiGraphics);
        guiGraphics.fill(currentMouseX, currentMouseY, currentMouseX + 10, currentMouseY + 10, Color.RED.argb());
        super.renderWidget(guiGraphics, currentMouseX, currentMouseY, partialTick);
    }

    @Override
    public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        captureMouseXY(drawContext, mouseX, mouseY, delta);
    }

    private @NotNull UiAnchor getPoint(int x, int y, GuiGraphics guiGraphics) {
        return new UiAnchor(
                new Matrix4f(guiGraphics.pose().last().pose()),
                new Matrix4f(RenderSystem.getModelViewMatrix()),
                new Matrix4f(RenderSystem.getProjectionMatrix()),
                new Vector3f(x, y, 0.0f)
        );
    }

    record UiAnchor(
            Matrix4f model,
            Matrix4f view,
            Matrix4f projection,
            Vector3f localPos
    ) {
        public Vector2i getWorldPosFromScreen(int screenX, int screenY) {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();

            float width = window.getGuiScaledWidth();
            float height = window.getGuiScaledHeight();

            // UI -> NDC
            float ndcX = (screenX / width) * 2.0f - 1.0f;
            float ndcY = 1.0f - (screenY / height) * 2.0f;

            // Combined transformation:
            // local -> model -> view -> projection
            Matrix4f inverse = new Matrix4f(this.projection())
                    .mul(this.view())
                    .mul(this.model())
                    .invert();

            // Near and far points on the screen ray.
            Vector4f near = new Vector4f(ndcX, ndcY, -1.0f, 1.0f)
                    .mul(inverse);

            Vector4f far = new Vector4f(ndcX, ndcY, 1.0f, 1.0f)
                    .mul(inverse);

            near.div(near.w);
            far.div(far.w);

            // Ray:
            // P(t) = near + t * (far - near)
            //
            // Find t where z = 0.

            float dz = far.z - near.z;

            if (Math.abs(dz) < 1e-6f) {
                return null; // Ray is parallel to z=0
            }

            float t = -near.z / dz;

            return new Vector2i(
                    (int) (near.x + t * (far.x - near.x)),
                    (int) (near.y + t * (far.y - near.y))
            );
        }
    }


    public void captureMouseXY(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        Vector2i localMouse = topLeftProjection.getWorldPosFromScreen(mouseX, mouseY);
        lastMouseX = currentMouseX;
        lastMouseY = currentMouseY;
        currentMouseX = localMouse.x;
        currentMouseY = localMouse.y;
    }

    public static void renderFromBer(
            BlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay,
            List<? extends WorldWidget> widgets
    ) {
        if (widgets.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();

        BufferSource bufferSource =
                buffers instanceof BufferSource source
                        ? source
                        : minecraft.renderBuffers().bufferSource();

        GuiGraphics graphics = new GuiGraphics(minecraft, bufferSource);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        for (WorldWidget widget : widgets) {
            if (widget == null) {
                continue;
            }
            graphics.pose().pushPose();
            graphics.pose().mulPose(poseStack.last().pose());
            graphics.pose().mulPose(widget.localWorldTransform);

            widget.renderInWorld(
                    graphics,
                    partialTick
            );
            graphics.pose().popPose();
            graphics.flush();
        }
        RenderSystem.depthMask(true);

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (GuiEventListener child : this.children()) {
            if (child.isMouseOver(currentMouseX, currentMouseY)) {
                child.mouseMoved(currentMouseX, currentMouseY);
            }
        }
        super.mouseMoved(currentMouseX, currentMouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (GuiEventListener child : this.children()) {
            if (child.mouseClicked(currentMouseX, currentMouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (GuiEventListener child : this.children()) {
            if (child.isMouseOver(currentMouseX, currentMouseY)
                && child.mouseReleased(currentMouseX, currentMouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double localDeltaX = currentMouseX - lastMouseX;
        double localDeltaY = currentMouseY - lastMouseY;

        for (GuiEventListener child : this.children()) {
            if (child.isMouseOver(currentMouseX, currentMouseY)
                && child.mouseDragged(
                    currentMouseX,
                    currentMouseY,
                    button,
                    localDeltaX,
                    localDeltaY
            )) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (GuiEventListener child : this.children()) {
            if (child.mouseScrolled(currentMouseX, currentMouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(currentMouseX, currentMouseY);
    }
}