package com.redpxnda.handson.neoforge;

import com.redpxnda.handson.HandsOn;
import net.neoforged.fml.common.Mod;

@Mod(HandsOn.MOD_ID)
public final class HandsOnNeoForge {
    public HandsOnNeoForge() {
        // Run our common setup.
        HandsOn.init();
    }
}
