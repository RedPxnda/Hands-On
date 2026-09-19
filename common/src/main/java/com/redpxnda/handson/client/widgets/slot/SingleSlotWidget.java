package com.redpxnda.handson.client.widgets.slot;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.client.ModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static org.lwjgl.opengl.GL11C.GL_ALWAYS;

public class SingleSlotWidget extends SlotWidget {
    public SingleSlotWidget(AbstractContainerMenu menu, Slot slot, int x, int y) {
        super(menu, slot, x, y);
        this.highlightOffest = -8;
    }


    private void renderInventoryModel(
            GuiGraphics graphics
    ) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack1 = new ItemStack(HandsOnRegistries.quadBenchItem);
        graphics.pose().pushPose();
        graphics.flush();
        graphics.pose().translate(8, 8, 4);
        graphics.pose().scale(16, 16, 8);
        BakedModel bakedModel = ModelHelper.SINGLE_SLOT_MODEL.get();
        //graphics.pose().pushPose();
        //graphics.pose().translate(-0.51F, -0.51F, -0.51F);
        //renderModelDepth(graphics.pose(), bakedModel, stack1, graphics.bufferSource());
        //graphics.flush();
        //RenderSystem.colorMask(true, true, true, true);
        //RenderSystem.depthMask(false);
        //RenderSystem.enableDepthTest();
        //graphics.pose().popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        mc.getItemRenderer().render(stack1, ItemDisplayContext.GUI, false, graphics.pose(), graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
        graphics.flush();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);

        graphics.pose().popPose();
    }

    public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        drawContext.pose().pushPose();
        drawContext.pose().translate(getX(), getY(), 0);
        renderInventoryModel(drawContext);
        drawContext.pose().popPose();
        drawContext.pose().pushPose();
        drawContext.pose().translate(0, 0, -8);
        //drawContext.pose().scale(1,1,0.5f);
        super.renderWidget(drawContext, mouseX, mouseY, delta);
        drawContext.pose().popPose();
    }

    /*
    this stuff didnt work
    ive tried to write larger numbers to the Depth buffer so i can render stuff in stuff.
    afaik it should work, but maybe opengl weird
     */
    private void renderModelDepth(
            PoseStack poseStack,
            BakedModel model,
            ItemStack stack,
            MultiBufferSource bufferSource
    ) {
        boolean cull = true;

        RenderType renderType = RenderType.gui();
                //ItemBlockRenderTypes.getRenderType(stack, cull);
        // this is a vanilla thing that overwrites the depth buffer LOL
        //VertexConsumer buffer = bufferSource.getBuffer(RenderType.dragonRaysDepth());
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.guiGhostRecipeOverlay());


        //RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL_ALWAYS);
        //RenderSystem.colorMask(false, false, false, false);

        RandomSource random = RandomSource.create(42L);

        for (Direction direction : Direction.values()) {
            random.setSeed(42L);

            for (BakedQuad quad : model.getQuads(null, direction, random)) {
                buffer.putBulkData(
                        poseStack.last(),
                        quad,
                        1.0F,
                        1.0F,
                        1.0F,
                        1.0f,
                        15728880,
                        OverlayTexture.NO_OVERLAY
                );
            }
        }

        random.setSeed(42L);

        for (BakedQuad quad : model.getQuads(null, null, random)) {
            buffer.putBulkData(
                    poseStack.last(),
                    quad,
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0f,
                    15728880,
                    OverlayTexture.NO_OVERLAY
            );
        }
        if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
            bufferSource1.endBatch();
        }
    }
}
