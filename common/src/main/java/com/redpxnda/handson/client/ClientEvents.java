package com.redpxnda.handson.client;

import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.blockentity.render.WorkbenchBlockEntityRenderer;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;

public class ClientEvents {
    public static void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(mc -> {
            BlockEntityRendererRegistry.register(HandsOnRegistries.workbenchBEType, WorkbenchBlockEntityRenderer::new);
        });

        MenuRegistry.registerScreenFactory(
                HandsOnRegistries.TINKERING_SCREEN_MENU_TYPE,
                TinkeringScreen::new
        );
    }
}
