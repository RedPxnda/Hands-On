package com.redpxnda.handson.block.quadblock;

import com.redpxnda.handson.block.quadblock.DirectionalQuadBlock.QuadBlockPart;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WorkbenchVoxelShape {

    private WorkbenchVoxelShape() {
    }

    /*
     * ============================================================
     * WORKBENCH
     * ============================================================
     *
     * Canonical orientation: NORTH
     *
     *        UPPER_LEFT   UPPER_RIGHT
     *
     *        LOWER_LEFT   LOWER_RIGHT
     *
     *                NORTH
     */

    private static final VoxelShape WORKBENCH_NORTH_LEFT =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape tabletop =
                        Block.box(0, 14, 0, 16, 16, 16);

                VoxelShape leg1 =
                        Block.box(1, 0, 1, 3, 14, 3);

                VoxelShape leg2 =
                        Block.box(1, 0, 13, 3, 14, 15);

                return Shapes.or(tabletop, leg1, leg2);
            });

    private static final VoxelShape WORKBENCH_NORTH_RIGHT =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape tabletop =
                        Block.box(0, 14, 0, 16, 16, 16);

                VoxelShape leg1 =
                        Block.box(13, 0, 1, 15, 14, 3);

                VoxelShape leg2 =
                        Block.box(13, 0, 13, 15, 14, 15);

                return Shapes.or(tabletop, leg1, leg2);
            });

    /*
     * EAST
     *
     * The NORTH shapes rotated clockwise.
     */

    private static final VoxelShape WORKBENCH_EAST_LEFT =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape tabletop =
                        Block.box(0, 14, 0, 16, 16, 16);

                VoxelShape leg1 =
                        Block.box(1, 0, 1, 3, 14, 3);

                VoxelShape leg2 =
                        Block.box(13, 0, 1, 15, 14, 3);

                return Shapes.or(tabletop, leg1, leg2);
            });

    private static final VoxelShape WORKBENCH_EAST_RIGHT =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape tabletop =
                        Block.box(0, 14, 0, 16, 16, 16);

                VoxelShape leg1 =
                        Block.box(1, 0, 13, 3, 14, 15);

                VoxelShape leg2 =
                        Block.box(13, 0, 13, 15, 14, 15);

                return Shapes.or(tabletop, leg1, leg2);
            });

    /*
     * SOUTH/WEST are mirrored versions of NORTH/EAST.
     */

    private static final VoxelShape WORKBENCH_SOUTH_LEFT =
            WORKBENCH_NORTH_RIGHT;

    private static final VoxelShape WORKBENCH_SOUTH_RIGHT =
            WORKBENCH_NORTH_LEFT;

    private static final VoxelShape WORKBENCH_WEST_LEFT =
            WORKBENCH_EAST_RIGHT;

    private static final VoxelShape WORKBENCH_WEST_RIGHT =
            WORKBENCH_EAST_LEFT;


    /*
     * ============================================================
     * BACKBOARD
     * ============================================================
     *
     * Both halves use the same shape. The part is only split
     * horizontally because the two BlockPos instances are separate.
     */

    private static final VoxelShape BACKBOARD_NORTH =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape board =
                        Block.box(0, 0, 0, 16, 16, 1);

                VoxelShape trim =
                        Block.box(0, 14, 0, 16, 16, 3);

                return Shapes.or(board, trim);
            });

    private static final VoxelShape BACKBOARD_EAST =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape board =
                        Block.box(15, 0, 0, 16, 16, 16);

                VoxelShape trim =
                        Block.box(13, 14, 0, 15, 16, 16);

                return Shapes.or(board, trim);
            });

    private static final VoxelShape BACKBOARD_SOUTH =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape board =
                        Block.box(0, 0, 15, 16, 16, 16);

                VoxelShape trim =
                        Block.box(0, 14, 13, 16, 16, 15);

                return Shapes.or(board, trim);
            });

    private static final VoxelShape BACKBOARD_WEST =
            MiscUtil.evaluateSupplier(() -> {
                VoxelShape board =
                        Block.box(0, 0, 0, 1, 16, 16);

                VoxelShape trim =
                        Block.box(0, 14, 0, 3, 16, 16);

                return Shapes.or(board, trim);
            });

    public static VoxelShape getWorkbenchShape(BlockState state) {
        Direction facing = state.getValue(DirectionalQuadBlock.FACING);
        QuadBlockPart part = state.getValue(DirectionalQuadBlock.PART);

        return getWorkbenchShape(facing, part);
    }

    public static VoxelShape getWorkbenchShape(
            Direction facing,
            QuadBlockPart part
    ) {
        return switch (part) {
            case LOWER_RIGHT -> switch (facing) {
                case NORTH -> WORKBENCH_NORTH_LEFT;
                case EAST  -> WORKBENCH_EAST_LEFT;
                case SOUTH -> WORKBENCH_SOUTH_LEFT;
                case WEST  -> WORKBENCH_WEST_LEFT;
                default -> Shapes.empty();
            };

            case LOWER_LEFT -> switch (facing) {
                case NORTH -> WORKBENCH_NORTH_RIGHT;
                case EAST  -> WORKBENCH_EAST_RIGHT;
                case SOUTH -> WORKBENCH_SOUTH_RIGHT;
                case WEST  -> WORKBENCH_WEST_RIGHT;
                default -> Shapes.empty();
            };

            case UPPER_LEFT, UPPER_RIGHT -> Shapes.empty();
        };
    }

    public static VoxelShape getBackboardShape(BlockState state) {
        return getBackboardShape(state.getValue(DirectionalQuadBlock.FACING));
    }

    public static VoxelShape getBackboardShape(Direction facing) {
        return switch (facing) {
            case NORTH -> BACKBOARD_NORTH;
            case EAST  -> BACKBOARD_EAST;
            case SOUTH -> BACKBOARD_SOUTH;
            case WEST  -> BACKBOARD_WEST;
            default -> Shapes.empty();
        };
    }

    public static VoxelShape getShape(BlockState state) {
        QuadBlockPart part =
                state.getValue(DirectionalQuadBlock.PART);

        return switch (part) {
            case LOWER_LEFT, LOWER_RIGHT ->
                    getWorkbenchShape(state);

            case UPPER_LEFT, UPPER_RIGHT ->
                    getBackboardShape(state);
        };
    }
}