package com.redpxnda.handson.client.widgets.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import smartin.miapi.client.gui.InteractAbleWidget;

public class SlotWidget extends InteractAbleWidget {
    private static final int SIZE = 16;

    private final AbstractContainerMenu menu;
    private final Slot slot;

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

        if (!stack.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, -150);
            graphics.renderItem(stack, getX(), getY(), 0, 0);

            graphics.renderItemDecorations(
                    Minecraft.getInstance().font,
                    stack,
                    getX(),
                    getY()
            );
            graphics.pose().popPose();
        }
        if (isMouseOver(mouseX, mouseY)) {
            graphics.fill(
                    getX(),
                    getY(),
                    getX() + SIZE,
                    getY() + SIZE,
                    0x40FFFFFF
            );
        }
    }
}