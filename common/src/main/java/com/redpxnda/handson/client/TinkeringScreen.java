package com.redpxnda.handson.client;

import com.mojang.blaze3d.platform.Window;
import com.redpxnda.handson.block.WorkbenchBlock;
import com.redpxnda.handson.client.widgets.SimpleWorldWidget;
import com.redpxnda.handson.blockentity.TinkeringMenu;
import com.redpxnda.handson.blockentity.WorkbenchBlockEntity;
import com.redpxnda.handson.blockentity.render.WorkbenchBlockEntityRenderer;
import com.redpxnda.handson.client.widgets.slot.PlayerInventoryWidget;
import com.redpxnda.handson.client.widgets.slot.SlotWidget;
import com.redpxnda.handson.client.widgets.WorldWidget;
import com.redpxnda.nucleus.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import smartin.miapi.item.modular.Transform;

import java.util.List;

public class TinkeringScreen extends AbstractContainerScreen<TinkeringMenu> implements MovingCinematicScreen {

    protected Transform targetTransform;
    public WorldWidget backboardWidget;
    public WorldWidget tableTopWidget;
    public Direction baseDirection;
    public TinkeringMenu menu;
    public boolean detached;

    public TinkeringScreen(
            TinkeringMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.targetTransform = menu.getWorkbench().getLookingTransform(menu.getTargetBlock(), menu.getTargetBlockState());
        this.baseDirection = menu.getTargetBlockState().getValue(WorkbenchBlock.FACING);
        this.menu = menu;
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        this.imageWidth = window.getGuiScaledWidth();
        this.imageHeight = window.getGuiScaledHeight();


        this.leftPos = 0;
        this.topPos = 0;
    }

    @Override
    protected void init() {
        super.init();
        Vec3 initial = minecraft.gameRenderer.getMainCamera().getPosition();
        Vec3 target = getTargetPosition();

        minecraft.options.hideGui = true;
        detached = initial.distanceTo(target) > 0.5;
        backboardWidget = new WorldWidget(512, 256);
        tableTopWidget = new WorldWidget(512, 256);
        backboardWidget.setLocalWorldTransform(
                WorkbenchBlockEntityRenderer.createWorkbenchScreenTransform(
                        baseDirection,
                        new Vector3f(1.5f, 2.0f, 7f / 16f - 0.005f),
                        new Vector3f(0f, 0.0f, 180.0f), 256).toMatrix());
        backboardWidget.localWidgetTransform =
                WorkbenchBlockEntityRenderer.createWorkbenchScreenTransform(
                        baseDirection,
                        new Vector3f(1.5f, 2.0f, 7f / 16f - 0.005f),
                        new Vector3f(0f, 0.0f, 180.0f), 256).toMatrix();
        tableTopWidget.setLocalWorldTransform(
                WorkbenchBlockEntityRenderer.createWorkbenchScreenTransform(
                        baseDirection,
                        new Vector3f(1.5f, 1.505f, 0.5f),
                        new Vector3f(45f, 0.0f, 180.0f), 256).toMatrix());
        tableTopWidget.localWidgetTransform =
                WorkbenchBlockEntityRenderer.createWorkbenchScreenTransform(
                        baseDirection,
                        new Vector3f(1.5f, 1.505f, 0.5f),
                        new Vector3f(45f, 0.0f, 180.0f), 256).toMatrix();
        tableTopWidget.addChild(new SlotWidget(menu, menu.mainInteractableSlot, 256 - 9, 256 - 20));
        tableTopWidget.addChild(new PlayerInventoryWidget(menu, menu.player.getInventory(), 300, 100));
        addWidget(tableTopWidget);
        addWidget(backboardWidget);
    }

    @Override
    public void onClose() {
        closing = true;
    }

    public void onCloseReal() {
        super.onClose();
        minecraft.options.hideGui = false;
    }

    public int animationTicks = 4;
    public boolean closing = false;

    public @Override int getAnimationTick() {return animationTicks;}

    public @Override void setAnimationTick(int newTicks) {this.animationTicks = newTicks;}

    public @Override boolean isClosing() {return closing;}

    public @Override void setClosing(boolean isClosing) {this.closing = isClosing;}

    @Override
    public boolean isCamDetached() {
        return detached;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        animationTick();
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        worldWidgets().forEach(worldWidget -> {
            worldWidget.captureMouseXY(guiGraphics, mouseX, mouseY, partialTick);
        });
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        //WorldMouseDebug.renderGui(guiGraphics);
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
    }

    @Override
    public Transform getTargetTransform() {
        return targetTransform;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public Transform getLookingTransform() {
        return this.targetTransform;
    }

    private final List<WorldWidget> worldWidgets() {
        return List.of(
                tableTopWidget,
                backboardWidget
        );
    }
}