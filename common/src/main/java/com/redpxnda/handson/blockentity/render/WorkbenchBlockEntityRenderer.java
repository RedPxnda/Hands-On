package com.redpxnda.handson.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.handson.blockentity.WorkbenchBlockEntity;
import com.redpxnda.handson.client.TinkeringScreen;
import com.redpxnda.handson.client.widgets.WorldWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import smartin.miapi.item.modular.Transform;

public class WorkbenchBlockEntityRenderer implements BlockEntityRenderer<WorkbenchBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public WorkbenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    public static Transform createWorkbenchScreenTransform(
            Direction baseDirection,
            Vector3f offset,
            Vector3f rotation,
            float pixelsPerBlock
    ) {
        float baseYRotation = switch (baseDirection) {
            case SOUTH -> 0.0f;
            case WEST -> -90.0f;
            case NORTH -> 180.0f;
            case EAST -> 90.0f;
            default -> 0.0f;
        };
        Transform base = new Transform(
                new Vector3f(
                        0.0f,
                        baseYRotation,
                        0.0f
                ),
                new Vector3f(
                        0.5f,
                        0.0f,
                        0.5f
                ),
                new Vector3f(1.0f)
        );
        Transform offsetTransform = new Transform(
                new Vector3f(0.0f),
                new Vector3f(offset),
                new Vector3f(1.0f)
        );
        Transform rotationTransform = new Transform(
                new Vector3f(rotation),
                new Vector3f(0.0f),
                new Vector3f(1.0f)
        );
        float blockPerPixel = 1.0f / (pixelsPerBlock);

        Transform scaleTransform = new Transform(
                new Vector3f(0.0f),
                new Vector3f(0.0f),
                new Vector3f(
                        blockPerPixel,
                        blockPerPixel,
                        blockPerPixel
                )
        );

        return base
                .merge(offsetTransform)
                .merge(rotationTransform)
                .merge(scaleTransform);
    }

    @Override
    public void render(
            WorkbenchBlockEntity be,
            float partialTick,
            PoseStack ps,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay
    ) {
        if (!be.isBeingInteractedWith()) {
            return;
        }

        if (!(Minecraft.getInstance().screen instanceof TinkeringScreen screen)) {
            return;
        }
        WorldWidget.renderFromBer(
                be,
                partialTick,
                ps,
                vertexConsumers,
                light,
                overlay,
                screen.worldWidgets()
        );
    }
}
