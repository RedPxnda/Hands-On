package com.redpxnda.handson.client.widgets.slot;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.util.Color;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.client.gui.InteractAbleWidget;
import smartin.miapi.config.MiapiConfig;

public class SlotWidget extends InteractAbleWidget {
    private static final int SIZE = 16;
    public final float width = 18;
    public final float height = 18;
    public final float depth = 8;

    public final float rimWidth = 1;
    public final float rimDepth = 1;

    public final int rimColor = Color.TEXT_DARK_GRAY.abgr();
    public final int insideColor = Color.GRAY.abgr();

    private final AbstractContainerMenu menu;
    private final Slot slot;
    public int highlightOffest = 0;

    public SlotWidget(
            AbstractContainerMenu menu,
            Slot slot,
            int x,
            int y
    ) {
        super(x, y, SIZE, SIZE, Component.empty());

        this.menu = menu;
        this.slot = slot;
    }

    public Slot getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return slot.getItem();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX()
               && mouseX < getX() + getWidth()
               && mouseY >= getY()
               && mouseY < getY() + getHeight();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY))
            return false;

        if (button != 0 && button != 1)
            return false;

        Minecraft mc = Minecraft.getInstance();

        if (mc.gameMode == null || mc.player == null)
            return false;

        if (mc.player.containerMenu != menu)
            return false;

        int slotId = menu.slots.indexOf(slot);

        if (slotId < 0)
            return false;


        mc.gameMode.handleInventoryMouseClick(
                menu.containerId,
                slotId,
                button,
                ClickType.PICKUP,
                mc.player
        );

        return true;
    }

    @Override
    public void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        ItemStack stack = slot.getItem();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 16);
        if (isDebug()) {
            graphics.pose().pushPose();
            graphics.pose().translate(getX() + 8, getY() + 8, 0);
            renderSlot(graphics);
            graphics.pose().popPose();
        }
        if (!stack.isEmpty()) {
            graphics.flush();
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, -150 + 2 + highlightOffest);
            //RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            renderItem(graphics, Minecraft.getInstance().player, Minecraft.getInstance().level, stack, getX(), getY(), 0, 0);
            //RenderSystem.disableDepthTest();
            graphics.flush();
            RenderSystem.depthMask(false);
            graphics.pose().popPose();
        }
        if (isMouseOver(mouseX, mouseY)) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, -8);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            graphics.fill(
                    getX(),
                    getY(),
                    getX() + 16,
                    getY() + 16,
                    0x40FFFFFF
            );
            graphics.flush();
            RenderSystem.enableDepthTest();
            graphics.pose().popPose();
        }
        if (!stack.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, -200 - 8 + 0.1f);
            graphics.renderItemDecorations(
                    Minecraft.getInstance().font,
                    stack,
                    getX(),
                    getY()
            );
            graphics.pose().popPose();
            graphics.flush();
        }
        graphics.pose().popPose();
    }

    public boolean isDebug() {
        return this.debug || (MiapiConfig.getServerConfig().other.developmentMode && Screen.hasAltDown());
    }

    private void renderItem(GuiGraphics graphics, @Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, int x, int y, int seed, int guiOffset) {
        if (!stack.isEmpty()) {
            BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(stack, level, entity, seed);
            graphics.pose().pushPose();
            graphics.pose().translate((float) (x + 8), (float) (y + 8), (float) (150 + (bakedModel.isGui3d() ? guiOffset : 0)));

            try {
                graphics.pose().scale(16.0F, -16.0F, -16.0F);
                boolean bl = !bakedModel.usesBlockLight();
                if (bl) {
                    Lighting.setupForFlatItems();
                } else {
                    Lighting.setupFor3DItems();
                }
                Lighting.setupLevel();
                //RenderSystem.disableDepthTest();
                Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.GUI, false, graphics.pose(), graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
                //RenderSystem.enableDepthTest();
                graphics.flush();
                if (bl) {
                    Lighting.setupFor3DItems();
                }
            } catch (Throwable var12) {
                CrashReport crashReport = CrashReport.forThrowable(var12, "Rendering item");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Item being rendered");
                crashReportCategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
                crashReportCategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
                crashReportCategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
                throw new ReportedException(crashReport);
            }

            graphics.pose().popPose();
        }
    }

    public float halfWidth() {
        return width / 2.0f;
    }

    public float halfHeight() {
        return height / 2.0f;
    }

    public float halfInsideWidth() {
        return (width - rimWidth * 2.0f) / 2.0f;
    }

    public float halfInsideHeight() {
        return (height - rimWidth * 2.0f) / 2.0f;
    }

    private void renderSlot(
            GuiGraphics graphics
    ) {
        PoseStack pose = graphics.pose();

        float outerX = halfWidth();
        float outerY = halfHeight();

        float innerX = halfInsideWidth();
        float innerY = halfInsideHeight();

        // Recessed center.
        CubeRenderHelper.render(
                pose,
                innerX,
                innerY,
                +depth + rimDepth,
                -innerX,
                -innerY,
                depth,
                insideColor
        );

        // Top bezel.
        CubeRenderHelper.render(
                pose,
                -outerX,
                -outerY,
                -depth,
                outerX,
                -innerY,
                depth,
                rimColor
        );

        // Bottom bezel.
        CubeRenderHelper.render(
                pose,
                -outerX,
                innerY,
                -depth,
                outerX,
                outerY,
                depth,
                rimColor
        );

        // Left bezel.
        CubeRenderHelper.render(
                pose,
                -outerX,
                -innerY,
                -depth,
                -innerX,
                innerY,
                depth,
                rimColor
        );

        // Right bezel.
        CubeRenderHelper.render(
                pose,
                innerX,
                -innerY,
                -depth,
                outerX,
                innerY,
                depth,
                rimColor
        );
    }


}