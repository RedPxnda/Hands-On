package com.redpxnda.handson.block;

import com.redpxnda.handson.client.MovingCinematicScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import smartin.miapi.item.modular.Transform;

public interface IWorkbench {

    Transform getLookingTransform(BlockPos pos, BlockState state);

    MovingCinematicScreen.CinematicCameraLimits getCamLimits(BlockPos targetBlock, BlockState targetBlockState);
}
