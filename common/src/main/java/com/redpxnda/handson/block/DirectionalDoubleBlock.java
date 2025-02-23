package com.redpxnda.handson.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public abstract class DirectionalDoubleBlock extends Block {
    public static final EnumProperty<DoubleBlockSide> DOUBLE_BLOCK_SIDE = EnumProperty.create("half", DoubleBlockSide.class);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockSide> HALF = DOUBLE_BLOCK_SIDE;

    public DirectionalDoubleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockSide.LEFT)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        Direction clockwise = context.getHorizontalDirection().getClockWise();
        BlockPos blockPos = context.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(clockwise);
        Level level = context.getLevel();
        return level.getBlockState(blockPos2).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockPos2)
                ? this.defaultBlockState().setValue(FACING, direction)
                : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos blockPos = pos.relative(state.getValue(FACING).getClockWise());
            level.setBlock(blockPos, state.setValue(HALF, DoubleBlockSide.RIGHT), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockSide half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);

        if (dir == getNeighborDirection(half, facing))
            return neighborState.is(this) && neighborState.getValue(HALF) != half ? state : Blocks.AIR.defaultBlockState();
        else
            return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    public static Direction getNeighborDirection(DoubleBlockSide half, Direction direction) {
        return half == DoubleBlockSide.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return super.playerWillDestroy(level, blockPos, blockState, player);
    }
}
