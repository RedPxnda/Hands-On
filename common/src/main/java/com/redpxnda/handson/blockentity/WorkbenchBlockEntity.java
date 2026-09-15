package com.redpxnda.handson.blockentity;

import com.redpxnda.handson.HandsOn;
import com.redpxnda.handson.HandsOnRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WorkbenchBlockEntity extends BlockEntity {
    private ItemStack stack = ItemStack.EMPTY;

    public WorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(HandsOnRegistries.workbenchBEType, pos, blockState);
    }

    public void setItem(ItemStack stack) {
        this.stack = stack == null ? ItemStack.EMPTY : stack.copy();
    }

    public ItemStack getItem() {
        return stack;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider wrapperLookup) {
        super.saveAdditional(tag, wrapperLookup);
        if (!getItem().isEmpty()) {
            try {
                tag.put("item", ItemStack.CODEC.encodeStart(
                        RegistryOps.create(NbtOps.INSTANCE, level.registryAccess()),
                        getItem()).getOrThrow());
            } catch (Exception e) {
                HandsOn.LOGGER.error("Could not save Item in Workbench! This indicates the item is broken and will cause more crashes later on.", e);
                try {
                    HandsOn.LOGGER.error(getItem().toString());
                } catch (RuntimeException ignored) {}
            }
        } else {
            tag.remove("item");
        }
    }

    @Nullable
    private UUID interactingPlayer;

    public void setInteractingPlayer(@Nullable UUID playerUuid) {
        this.interactingPlayer = playerUuid;
    }

    @Nullable
    public UUID getInteractingPlayer() {
        return interactingPlayer;
    }

    public boolean isBeingInteractedWith() {
        return interactingPlayer != null;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider wrapperLookup) {
        super.loadAdditional(tag, wrapperLookup);

        if (tag.contains("item")) {
            try {
                stack = ItemStack.parse(wrapperLookup, tag.getCompound("item")).get();
                setItem(stack);
            } catch (RuntimeException e) {
                HandsOn.LOGGER.error("Failed to load workbench item!", e);
                setItem(ItemStack.EMPTY);
            }
        } else
            setItem(ItemStack.EMPTY);
    }

    public void saveAndSync() {
        setChanged();
        if (hasLevel()) level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), getBlockState(), 3);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::saveWithFullMetadata);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return new CompoundTag();
    }
}
