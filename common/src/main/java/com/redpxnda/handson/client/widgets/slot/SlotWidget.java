package com.redpxnda.handson.client.widgets.slot;

import com.mojang.blaze3d.systems.RenderSystem;
import com.redpxnda.handson.client.DebugHelper;
import com.redpxnda.handson.client.ModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import smartin.miapi.client.gui.InteractAbleWidget;
import smartin.miapi.config.MiapiConfig;

public class SlotWidget extends InteractAbleWidget {
    private static final int SIZE = 16;
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
            DebugHelper.renderSlot(
                    graphics,
                    9.0f,
                    9.0f,
                    8.0f,
                    8.0f,
                    8.0f,
                    1.0f
            );
            graphics.pose().popPose();
        }
        if (!stack.isEmpty()) {
            graphics.flush();
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, -150 + 2 + highlightOffest);
            //RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            ModelHelper.renderItem(graphics, Minecraft.getInstance().player, Minecraft.getInstance().level, stack, getX(), getY(), 0, 0);
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


}