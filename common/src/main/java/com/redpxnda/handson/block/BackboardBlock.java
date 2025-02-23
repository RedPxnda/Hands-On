package com.redpxnda.handson.block;

import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BackboardBlock extends DirectionalDoubleBlock {
    public static final VoxelShape NORTH_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape board = Block.box(0, 0, 0, 16, 16, 1);
        VoxelShape trim = Block.box(0, 14, 0, 16, 16, 3);
        return Shapes.or(board, trim);
    });
    public static final VoxelShape EAST_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape board = Block.box(15, 0, 0, 16, 16, 16);
        VoxelShape trim = Block.box(13, 14, 0, 15, 16, 16);
        return Shapes.or(board, trim);
    });
    public static final VoxelShape SOUTH_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape board = Block.box(0, 0, 15, 16, 16, 16);
        VoxelShape trim = Block.box(0, 14, 13, 16, 16, 15);
        return Shapes.or(board, trim);
    });
    public static final VoxelShape WEST_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape board = Block.box(0, 0, 0, 1, 16, 16);
        VoxelShape trim = Block.box(0, 14, 0, 3, 16, 16);
        return Shapes.or(board, trim);
    });

    public BackboardBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        Direction facing = blockState.getValue(FACING);
        return switch (facing) {
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }
}
