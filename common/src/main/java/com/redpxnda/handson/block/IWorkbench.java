package com.redpxnda.handson.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import smartin.miapi.item.modular.Transform;

public interface IWorkbench {

    Transform getLookingTransform(BlockPos pos, BlockState state);
}
