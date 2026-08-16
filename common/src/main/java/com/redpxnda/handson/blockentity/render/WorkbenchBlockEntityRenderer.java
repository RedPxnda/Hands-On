package com.redpxnda.handson.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.redpxnda.handson.block.WorkbenchBlock;
import com.redpxnda.handson.blockentity.WorkbenchBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.*;
import smartin.miapi.item.modular.VisualModularItem;

public class WorkbenchBlockEntityRenderer implements BlockEntityRenderer<WorkbenchBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public WorkbenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(WorkbenchBlockEntity be, float partialTick, PoseStack ps, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemStack stack = be.getItem();
        if (stack.isEmpty()) return;

        Direction facingDir = be.getBlockState().getValue(WorkbenchBlock.FACING);
        Vec3i translationNormal = facingDir.getClockWise().getNormal();
        boolean isModular = stack.getItem() instanceof VisualModularItem;
        ps.pushPose();
        ps.translate(8 / 16f, 16.5f / 16, 8 / 16f);
        ps.translate(translationNormal.getX()/2f, translationNormal.getY()/2f, translationNormal.getZ()/2f);

        float rotAmnt = facingDir.toYRot();
        if (!(stack.getItem() instanceof Equipable) && (
                isModular ||
                stack.getItem() instanceof TieredItem ||
                stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof ArrowItem ||
                //stack.getItem() instanceof CrossbowItem ||
                stack.getItem() instanceof ProjectileWeaponItem))
            rotAmnt -= 45;
        else
            rotAmnt -= 90;
        ps.mulPose(Axis.YP.rotationDegrees(rotAmnt));
        ps.mulPose(Axis.XP.rotationDegrees(90));
        ps.scale(0.75f, 0.75f, 0.75f);

        try {
            //if (isModular)
                // todo alpha property - ItemModule.getModules(stack).getProperty();
            context.getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    light, overlay,
                    ps, vertexConsumers,
                    be.getLevel(), 1
            );
        } catch (Exception ignored) {
        }
        ps.popPose();
    }
}
