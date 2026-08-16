package com.redpxnda.handson.block;

import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.blockentity.WorkbenchBlockEntity;
import com.redpxnda.nucleus.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.item.modular.VisualModularItem;

public class WorkbenchBlock extends DirectionalDoubleBlock implements EntityBlock {
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !context.getLevel().getBlockState(context.getClickedPos().below()).isAir() ? super.getStateForPlacement(context) : null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.CONSUME;
        } else {
            if (state.getValue(HALF) == DoubleBlockSide.RIGHT) {
                pos = pos.relative(state.getValue(FACING).getCounterClockWise());
                System.out.println("other pos: " + pos);
                state = level.getBlockState(pos);
            }
            if (player instanceof ServerPlayer sp && level instanceof ServerLevel sl && level.getBlockEntity(pos) instanceof WorkbenchBlockEntity be) {
                if (stack.getItem() instanceof VisualModularItem) {
                    be.setItem(stack);
                    be.saveAndSync();
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockSide.RIGHT ? null : HandsOnRegistries.workbenchBEType.create(pos, state);
    }
}
