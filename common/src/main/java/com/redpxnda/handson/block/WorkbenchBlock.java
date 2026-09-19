package com.redpxnda.handson.block;

import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.blockentity.TinkeringMenu;
import com.redpxnda.handson.blockentity.WorkbenchBlockEntity;
import com.redpxnda.handson.client.MovingCinematicScreen;
import com.redpxnda.handson.client.TinkeringScreen;
import com.redpxnda.nucleus.util.MiscUtil;
import dev.architectury.event.EventResult;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import org.joml.Vector3f;
import smartin.miapi.item.modular.Transform;
import smartin.miapi.item.modular.VisualModularItem;

public class WorkbenchBlock extends DirectionalDoubleBlock implements EntityBlock,IWorkbench {
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
            if (player instanceof ServerPlayer sp && level instanceof ServerLevel sl && level.getBlockEntity(pos) instanceof WorkbenchBlockEntity be) {
                BlockState backboardState = level.getBlockState(pos.above());
                if (backboardState.getBlock() instanceof BackboardBlock) {
                    Direction facing = backboardState.getValue(FACING);
                    TinkeringMenu.openMenuFromServer(sp,pos,facing);
                } else {
                    Direction facing = be.getBlockState().getValue(FACING);
                    TinkeringMenu.openMenuFromServer(sp,pos,facing);
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

    @Override
    public Transform getLookingTransform(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);

        // Center of the complete 2x2 workbench.
        float x = 16.0F;
        float y = 16.0F;
        float z = 16.0F;

        // Move 0.5 blocks toward the viewer.
        Direction front = facing.getOpposite();

        x += front.getStepX() * 8.0F;
        z += front.getStepZ() * 8.0F;

        // Transform rotations are XYZ Euler angles.
        // Pitch down 20 degrees.
        float yaw = switch (facing) {
            case SOUTH -> 180.0F;
            case WEST -> 90.0F;
            case EAST -> -90.0F;
            default -> 0.0F;
        };

        return new Transform(
                new Vector3f(20.0F, yaw, 0.0F),
                new Vector3f(x, y, z),
                new Vector3f(1.0F, 1.0F, 1.0F)
        );
    }
    @Override
    public MovingCinematicScreen.CinematicCameraLimits getCamLimits(BlockPos targetBlock, BlockState targetBlockState) {
        return new MovingCinematicScreen.CinematicCameraLimits(0.2f,0.1f,30,40);
    }
}
