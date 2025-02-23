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

public class WorkbenchBlock extends DirectionalDoubleBlock {
    public static final VoxelShape NORTH_LEFT_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape tabletop = Block.box(0, 14, 0, 16, 16, 16);
        VoxelShape leg1 = Block.box(1, 0, 1, 3, 14, 3);
        VoxelShape leg2 = Block.box(1, 0, 13, 3, 14, 15);
        return Shapes.or(tabletop, leg1, leg2);
    });
    public static final VoxelShape NORTH_RIGHT_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape tabletop = Block.box(0, 14, 0, 16, 16, 16);
        VoxelShape leg1 = Block.box(13, 0, 1, 15, 14, 3);
        VoxelShape leg2 = Block.box(13, 0, 13, 15, 14, 15);
        return Shapes.or(tabletop, leg1, leg2);
    });
    public static final VoxelShape EAST_LEFT_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape tabletop = Block.box(0, 14, 0, 16, 16, 16);
        VoxelShape leg1 = Block.box(1, 0, 1, 3, 14, 3);
        VoxelShape leg2 = Block.box(13, 0, 1, 15, 14, 3);
        return Shapes.or(tabletop, leg1, leg2);
    });
    public static final VoxelShape EAST_RIGHT_SHAPE = MiscUtil.evaluateSupplier(() -> {
        VoxelShape tabletop = Block.box(0, 14, 0, 16, 16, 16);
        VoxelShape leg1 = Block.box(1, 0, 13, 3, 14, 15);
        VoxelShape leg2 = Block.box(13, 0, 13, 15, 14, 15);
        return Shapes.or(tabletop, leg1, leg2);
    });
    public static final VoxelShape SOUTH_LEFT_SHAPE = NORTH_RIGHT_SHAPE;
    public static final VoxelShape SOUTH_RIGHT_SHAPE = NORTH_LEFT_SHAPE;
    public static final VoxelShape WEST_LEFT_SHAPE = EAST_RIGHT_SHAPE;
    public static final VoxelShape WEST_RIGHT_SHAPE = EAST_LEFT_SHAPE;

    public WorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        Direction facing = blockState.getValue(FACING);
        DoubleBlockSide half = blockState.getValue(HALF);
        return switch (facing) {
            case EAST -> half == DoubleBlockSide.LEFT ? EAST_LEFT_SHAPE : EAST_RIGHT_SHAPE;
            case SOUTH -> half == DoubleBlockSide.LEFT ? SOUTH_LEFT_SHAPE : SOUTH_RIGHT_SHAPE;
            case WEST -> half == DoubleBlockSide.LEFT ? WEST_LEFT_SHAPE : WEST_RIGHT_SHAPE;
            default -> half == DoubleBlockSide.LEFT ? NORTH_LEFT_SHAPE : NORTH_RIGHT_SHAPE;
        };
    }
}
