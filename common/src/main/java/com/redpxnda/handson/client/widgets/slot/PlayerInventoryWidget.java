package com.redpxnda.handson.client.widgets.slot;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.client.ModelHelper;
import com.redpxnda.handson.client.TransformEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import smartin.miapi.client.gui.InteractAbleWidget;
import smartin.miapi.item.modular.Transform;

public class PlayerInventoryWidget extends InteractAbleWidget {

    public static final int SLOT_SIZE = 18;
    private static final ResourceLocation INVENTORY_MODEL =
            ResourceLocation.fromNamespaceAndPath(
                    "tm_handson",
                    "block/inventory3d"
            );

    private BakedModel getInventoryModel() {
        return Minecraft.getInstance()
                .getModelManager()
                .getModel(ModelResourceLocation.inventory(INVENTORY_MODEL));
        // Minecraft.getInstance().getModelManager().bakedRegistry.keySet().stream().filter(key->key.toString().contains("tm")&&key.toString().contains("inventory")).toList()
    }

    private final AbstractContainerMenu menu;
    private final Inventory inventory;
    TransformEditor editor;
    Transform modelTransform = new Transform(
            new Vector3f(-0, 0, 180),
            new Vector3f(88 - 110, 2 - 180, 88),
            new Vector3f(1, 1, 1));

    public PlayerInventoryWidget(
            AbstractContainerMenu menu,
            Inventory inventory,
            int x,
            int y
    ) {
        super(
                x,
                y,
                9 * SLOT_SIZE,
                4 * SLOT_SIZE,
                Component.empty()
        );
        this.menu = menu;
        this.inventory = inventory;
        Transform transform;
        //editor = new TransformEditor();
        buildSlots();
    }

    public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        drawContext.pose().pushPose();
        drawContext.pose().translate(getX(), getY(), 0);
        renderInventoryModel(drawContext, getInventoryModel());
        drawContext.pose().popPose();
        super.renderWidget(drawContext, mouseX, mouseY, delta);
    }

    private void renderInventoryModel(
            GuiGraphics graphics,
            BakedModel model
    ) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack1 = new ItemStack(HandsOnRegistries.quadBenchItem);

        graphics.pose().pushPose();
        //editor.getTransform().applyPosition(graphics.pose());
        modelTransform.applyPosition(graphics.pose());
        //graphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
        graphics.pose().scale(10, 10, 10);

        graphics.pose().scale(
                16.0f,
                16.0f,
                16.0f
        );
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        //Lighting.setupForFlatItems();
        BakedModel bakedModel = ModelHelper.INVENTORY_MODEL.get();
        Lighting.setupLevel();
        mc.getItemRenderer().render(
                stack1,
                ItemDisplayContext.GUI,
                false,
                graphics.pose(),
                graphics.bufferSource(),
                15728880,
                OverlayTexture.NO_OVERLAY,
                bakedModel
        );
        graphics.flush();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        /*
        BakedModel bakedModelb = Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(Items.DIAMOND_AXE), Minecraft.getInstance().player.clientLevel, Minecraft.getInstance().player, 0);
        mc.getItemRenderer().render(
                stack1,
                ItemDisplayContext.GUI,
                false,
                graphics.pose(),
                graphics.bufferSource(),
                15728880,
                OverlayTexture.NO_OVERLAY,
                bakedModelb
        );

         */

        graphics.flush();

        graphics.pose().popPose();
    }

    private void buildSlots() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addPlayerSlot(
                        9 + row * 9 + column,
                        column,
                        0,
                        row
                );
            }
        }

        for (int column = 0; column < 9; column++) {
            addPlayerSlot(
                    column,
                    column,
                    4,
                    3
            );
        }
    }

    private void addPlayerSlot(
            int inventoryIndex,
            int column,
            int yOffset,
            int row
    ) {
        Slot menuSlot = findPlayerSlot(inventoryIndex);

        if (menuSlot == null)
            return;

        addChild(
                new SlotWidget(
                        menu,
                        menuSlot,
                        column * SLOT_SIZE,
                        row * SLOT_SIZE + yOffset
                )
        );
    }

    @Nullable
    private Slot findPlayerSlot(int inventoryIndex) {
        for (Slot slot : menu.slots) {
            if (slot.container == inventory &&
                slot.getContainerSlot() == inventoryIndex) {
                return slot;
            }
        }

        return null;
    }
}