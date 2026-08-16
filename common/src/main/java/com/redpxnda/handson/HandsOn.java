package com.redpxnda.handson;

import com.redpxnda.handson.client.ClientEvents;
import com.redpxnda.handson.client.TinkeringScreen;
import com.redpxnda.nucleus.registration.RegistryAnalyzer;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HandsOn {
    public static final String MOD_ID = "tm_handson";
    public static final Logger LOGGER = LoggerFactory.getLogger("Truly Modular: Hands On");

    public static void init() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            if (pos.getX() == 0 && pos.getZ() == 0 && pos.getY() == 100) {
                if (player.level().isClientSide) {
                    Minecraft.getInstance().setScreen(new TinkeringScreen(pos, Direction.NORTH));
                }
                return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        RegistryAnalyzer.register(MOD_ID, () -> HandsOnRegistries.class);

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ClientEvents.init();
        });
    }
}
