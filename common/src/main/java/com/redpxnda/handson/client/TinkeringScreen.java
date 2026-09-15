package com.redpxnda.handson.client;

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
import org.joml.Vector3f;
import smartin.miapi.item.modular.Transform;

import java.util.List;

public class TinkeringScreen extends AbstractContainerScreen<TinkeringMenu> implements MovingCinematicScreen {

    protected Transform targetTransform;
    public SimpleWorldWidget backboardWidget;
    public SimpleWorldWidget tableTopWidget;
    public Direction baseDirection;
    public TinkeringMenu menu;

    public TinkeringScreen(
            TinkeringMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.targetTransform = menu.getWorkbench().getLookingTransform(menu.getTargetBlock(), menu.getTargetBlockState());
        this.baseDirection = menu.getTargetBlockState().getValue(WorkbenchBlock.FACING);
        this.menu = menu;
    }

    @Override
    protected void init() {
        super.init();
        minecraft.options.hideGui = true;
        backboardWidget = new SimpleWorldWidget(512, 256, Component.literal("test"));
        tableTopWidget = new SimpleWorldWidget(512, 256, Component.literal("test"));
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
        //tableTopWidget.addChild(new SlotWidget(menu, menu.mainInteractableSlot, 256 - 9, 256 - 20));
        //tableTopWidget.addChild(new PlayerInventoryWidget(menu, menu.player.getInventory(), 300, 100));
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
        /*
        super.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

         */
        worldWidgets().forEach(worldWidget -> {
            worldWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        });
        //WorldMouseDebug.renderGui(guiGraphics);
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