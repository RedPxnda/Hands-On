package com.redpxnda.handson.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
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
}
