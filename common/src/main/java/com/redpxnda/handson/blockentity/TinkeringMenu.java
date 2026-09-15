package com.redpxnda.handson.blockentity;

import com.redpxnda.handson.HandsOn;
import com.redpxnda.handson.HandsOnRegistries;
import com.redpxnda.handson.block.IWorkbench;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TinkeringMenu extends AbstractContainerMenu {
    private final BlockPos targetBlock;
    private final Direction workbenchDirection;
    public Player player;
    public Slot mainInteractableSlot;

    // Client constructor
    public TinkeringMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(
                containerId,
                inventory,
                buf.readBlockPos(),
                buf.readEnum(Direction.class)
        );
        this.player = inventory.player;
        init();
    }

    public void writeBufferForClient(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.targetBlock);
        buf.writeEnum(this.workbenchDirection);
    }

    public static void openMenuFromServer(ServerPlayer player, BlockPos targetBlock, Direction workbenchDirection) {
        if (player.level().getBlockEntity(targetBlock) instanceof WorkbenchBlockEntity be) {
            be.setInteractingPlayer(player.getUUID());
        }

        MenuRegistry.openExtendedMenu(
                player,
                new SimpleMenuProvider(
                        (id, inventory, p) ->
                                new TinkeringMenu(
                                        id,
                                        inventory,
                                        targetBlock,
                                        workbenchDirection
                                ),
                        Component.empty()
                ),
                buf -> {
                    buf.writeBlockPos(targetBlock);
                    buf.writeEnum(workbenchDirection);
                }
        );
    }

    @Nullable
    private WorkbenchBlockEntity getWorkbenchBlockEntity() {
        if (player.level().getBlockEntity(targetBlock) instanceof WorkbenchBlockEntity be) {
            return be;
        }
        return null;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (player.level().getBlockEntity(targetBlock) instanceof WorkbenchBlockEntity be) {
            if (player.getUUID().equals(be.getInteractingPlayer())) {
                be.setInteractingPlayer(null);
            }
        }
    }

    // Server constructor
    public TinkeringMenu(
            int containerId,
            Inventory inventory,
            BlockPos targetBlock,
            Direction workbenchDirection
    ) {
        super(HandsOnRegistries.TINKERING_SCREEN_MENU_TYPE, containerId);
        this.player = inventory.player;
        this.targetBlock = targetBlock;
        this.workbenchDirection = workbenchDirection;
        init();
    }

    public void init() {
        SimpleContainer container = new SimpleContainer(54);
        mainInteractableSlot = this.addSlot(new Slot(container, 0, -10, -10));
        addPlayerInventory(this.player.getInventory());
        if (player.level().getBlockEntity(targetBlock) instanceof WorkbenchBlockEntity be) {
            be.setInteractingPlayer(player.getUUID());
        }
    }

    private void addPlayerInventory(Inventory container) {
        // Main inventory: 3 rows × 9 slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = col + row * 9 + 9;
                this.addSlot(new Slot(container, slot, -1000, -1000));
            }
        }

        // Hotbar: 9 slots
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(container, col, -1000, -1000));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().getBlockState(targetBlock).getBlock() instanceof IWorkbench
               && player.distanceToSqr(
                targetBlock.getX() + 0.5,
                targetBlock.getY() + 0.5,
                targetBlock.getZ() + 0.5
        ) < 64.0;
    }

    public IWorkbench getWorkbench() {
        if (player.level().getBlockState(targetBlock).getBlock() instanceof IWorkbench iWorkbench) {
            return iWorkbench;
        }
        return null;
    }

    public BlockPos getTargetBlock() {
        return targetBlock;
    }

    public BlockState getTargetBlockState() {
        return player.level().getBlockState(targetBlock);
    }

    public Direction getWorkbenchDirection() {
        return workbenchDirection;
    }
}