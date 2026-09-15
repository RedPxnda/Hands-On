package com.redpxnda.handson.block.quadblock;

import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.block.IWorkbench;
import com.redpxnda.handson.blockentity.TinkeringMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import smartin.miapi.item.modular.Transform;

public class QuadWorkbenchBlock
        extends DirectionalQuadBlock
        implements EntityBlock, IWorkbench {

    public QuadWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return WorkbenchVoxelShape.getShape(state);
    }

    @Override
    protected Block getPartBlock(QuadBlockPart part) {
        return this;
    }

    @Override
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
        if (player instanceof ServerPlayer sp
            && level instanceof ServerLevel sl) {

            Direction facing = state.getValue(FACING);

            TinkeringMenu.openMenuFromServer(
                    sp,
                    origin,
                    facing
            );
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return switch (state.getValue(PART)) {
            case LOWER_LEFT -> HandsOnRegistries.workbenchBEType.create(pos, state);

            case LOWER_RIGHT, UPPER_LEFT, UPPER_RIGHT -> null;
        };
    }

    @Override
    public Transform getLookingTransform(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        QuadBlockPart part = state.getValue(PART);
        BlockPos origin = getOrigin(pos, facing, part);
        Direction left = facing.getCounterClockWise();
        Direction front = facing.getOpposite();

        float x = origin.getX() + 0.5f;
        float y = origin.getY() + 0.5f;
        float z = origin.getZ() + 0.5f;
        x += front.getStepX() * 0.8F;
        z += front.getStepZ() * 0.8F;
        x += left.getStepX() * 0.5F;
        z += left.getStepZ() * 0.5F;
        y += 1.1f;
        float yaw = switch (facing) {
            case SOUTH -> 0.0F;
            case WEST -> 90.0F;
            case EAST -> -90.0F;
            default -> 180.0F;
        };

        float pitch = switch (facing) {
            case SOUTH, EAST, WEST -> 20.0F;
            default -> 20.0F;
        };

        return new Transform(
                new Vector3f(pitch, yaw, 0.0F),
                new Vector3f(x, y, z),
                new Vector3f(1.0F, 1.0F, 1.0F)
        );
    }
}