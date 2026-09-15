package com.redpxnda.handson.block.quadblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DirectionalQuadBlock extends Block {

    public static final DirectionProperty FACING =
            HorizontalDirectionalBlock.FACING;

    public static final EnumProperty<QuadBlockPart> PART =
            EnumProperty.create("part", QuadBlockPart.class);

    public DirectionalQuadBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(PART, QuadBlockPart.LOWER_LEFT)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING, PART);
    }

    /**
     * The LOWER_LEFT block is the origin and main owner of the structure.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();

        for (QuadBlockPart part : QuadBlockPart.values()) {
            BlockPos partPos =
                    getPartPosition(origin, facing, part);

            if (!level.getWorldBorder().isWithinBounds(partPos)
                || !level.getBlockState(partPos).canBeReplaced(context)) {
                return null;
            }
        }

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, QuadBlockPart.LOWER_LEFT);
    }

    /**
     * Places the remaining three parts around the LOWER_LEFT origin.
     */
    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide)
            return;

        Direction facing = state.getValue(FACING);

        for (QuadBlockPart part : QuadBlockPart.values()) {
            if (part == QuadBlockPart.LOWER_LEFT)
                continue;

            BlockPos partPos =
                    getPartPosition(pos, facing, part);

            Block partBlock = getPartBlock(part);

            level.setBlock(
                    partPos,
                    partBlock.defaultBlockState()
                            .setValue(FACING, facing)
                            .setValue(PART, part),
                    3
            );
        }
    }

    /**
     * Returns the LOWER_LEFT block of the structure.
     *
     * The returned position is always the main/owner position.
     */
    public static BlockPos getOrigin(
            BlockPos pos,
            Direction facing,
            QuadBlockPart part
    ) {
        Direction left = facing.getCounterClockWise();

        return switch (part) {
            case LOWER_LEFT ->
                    pos;

            case LOWER_RIGHT ->
                    pos.relative(left.getOpposite());

            case UPPER_LEFT ->
                    pos.below();

            case UPPER_RIGHT ->
                    pos.below().relative(left.getOpposite());
        };
    }

    /**
     * Returns the position of a part relative to the LOWER_LEFT origin.
     */
    public static BlockPos getPartPosition(
            BlockPos origin,
            Direction facing,
            QuadBlockPart part
    ) {
        Direction left = facing.getCounterClockWise();

        return switch (part) {
            case LOWER_LEFT ->
                    origin;

            case LOWER_RIGHT ->
                    origin.relative(left);

            case UPPER_LEFT ->
                    origin.above();

            case UPPER_RIGHT ->
                    origin.above().relative(left);
        };
    }

    /**
     * Returns the block type that belongs at a given part.
     */
    protected abstract Block getPartBlock(QuadBlockPart part);

    /**
     * Break all other parts when any part is destroyed.
     */
    @Override
    public BlockState playerWillDestroy(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            QuadBlockPart part = state.getValue(PART);

            BlockPos origin =
                    getOrigin(pos, facing, part);

            for (QuadBlockPart other : QuadBlockPart.values()) {
                if (other == part)
                    continue;

                BlockPos otherPos =
                        getPartPosition(origin, facing, other);

                BlockState otherState =
                        level.getBlockState(otherPos);

                if (otherState.is(getPartBlock(other))
                    && otherState.getValue(FACING) == facing
                    && otherState.getValue(PART) == other) {

                    level.setBlock(
                            otherPos,
                            Blocks.AIR.defaultBlockState(),
                            35
                    );
                }
            }
        }

        return super.playerWillDestroy(
                level,
                pos,
                state,
                player
        );
    }

    /**
     * Redirect interaction from every part to the structure owner.
     */
    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        Direction facing = state.getValue(FACING);
        QuadBlockPart part = state.getValue(PART);

        BlockPos origin =
                getOrigin(pos, facing, part);

        return onQuadBlockUse(
                stack,
                state,
                level,
                pos,
                origin,
                player,
                hand,
                hitResult
        );
    }

    /**
     * Override this in the concrete block to handle interaction.
     *
     * origin is always the LOWER_LEFT position.
     */
    protected ItemInteractionResult onQuadBlockUse(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            BlockPos origin,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * Remove a part if one of the other required parts is missing
     * or has an incorrect state.
     */
    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        Direction facing = state.getValue(FACING);
        QuadBlockPart part = state.getValue(PART);

        BlockPos origin =
                getOrigin(pos, facing, part);

        for (QuadBlockPart other : QuadBlockPart.values()) {
            if (other == part)
                continue;

            BlockPos expectedPos =
                    getPartPosition(origin, facing, other);

            if (!expectedPos.equals(neighborPos))
                continue;

            boolean valid =
                    neighborState.is(getPartBlock(other))
                    && neighborState.getValue(FACING) == facing
                    && neighborState.getValue(PART) == other;

            return valid
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
    }

    public enum QuadBlockPart implements StringRepresentable {
        LOWER_LEFT,
        LOWER_RIGHT,
        UPPER_LEFT,
        UPPER_RIGHT;

        @Override
        public @NotNull String getSerializedName() {
            return switch (this) {
                case LOWER_LEFT -> "lower_left";
                case LOWER_RIGHT -> "lower_right";
                case UPPER_LEFT -> "upper_left";
                case UPPER_RIGHT -> "upper_right";
            };
        }
    }
}