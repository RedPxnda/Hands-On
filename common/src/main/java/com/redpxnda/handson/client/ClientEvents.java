package com.redpxnda.handson.client;

import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.blockentity.render.WorkbenchBlockEntityRenderer;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.datapack.ReloadEvents;
import smartin.miapi.modules.properties.render.baked.ModelManager;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClientEvents {
    public static void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(mc -> {
            BlockEntityRendererRegistry.register(HandsOnRegistries.workbenchBEType, WorkbenchBlockEntityRenderer::new);
        });

        MenuRegistry.registerScreenFactory(
                HandsOnRegistries.TINKERING_SCREEN_MENU_TYPE,
                TinkeringScreen::new
        );
        //ModelManager.loadModelsByPath("tm_handson:item/inventory3d.json");
        ReloadEvents.MAIN.subscribe(new ReloadEvents.EventListener() {
            @Override
            public void onEvent(boolean b, @Nullable RegistryAccess registryAccess, Consumer<CompletableFuture<?>> consumer) {
                if (b) {
                    ModelHelper.toLoad.forEach(id->{
                        ModelManager.loadModelsByPath(id);
                    });
                }
            }
        });
    }
}
