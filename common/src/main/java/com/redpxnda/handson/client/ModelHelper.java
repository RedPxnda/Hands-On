package com.redpxnda.handson.client;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.Miapi;
import smartin.miapi.client.model.DynamicBakery;
import smartin.miapi.client.model.item.BakedSingleModel;
import smartin.miapi.item.modular.Transform;
import smartin.miapi.modules.properties.render.baked.ModelManager;
import smartin.miapi.modules.properties.render.baked.UnbakedModelHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModelHelper {
    public static final List<String> toLoad = new ArrayList<>();

    public static final Supplier<BakedModel> INVENTORY_MODEL = getModel(Miapi.id("tm_handson:item/inventory"));
    public static final Supplier<BakedModel> SINGLE_SLOT_MODEL = getModel(Miapi.id("tm_handson:item/singleslot"));

    public static Supplier<BakedModel> getModel(ResourceLocation id) {
        String stringID = id.getNamespace() + ":" + id.getPath();
        toLoad.add(stringID);
        return () -> {
            UnbakedModelHolder unbakedModel = ModelManager.modelCache.get(stringID);
            BakedSingleModel model = DynamicBakery.bakeModel(
                    unbakedModel.model(),
                    ModelManager.textureGetter,
                    FastColor.ARGB32.color(255, 255, 255, 255),
                    Transform.IDENTITY);
            return model;
        };
    }

    public static void renderItem(GuiGraphics graphics, @Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, int x, int y, int seed, int guiOffset) {
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
}
