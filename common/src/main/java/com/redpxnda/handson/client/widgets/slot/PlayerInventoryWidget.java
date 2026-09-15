package com.redpxnda.handson.client.widgets.slot;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.client.gui.InteractAbleWidget;

public class PlayerInventoryWidget extends InteractAbleWidget {

    public static final int SLOT_SIZE = 18;

    private final AbstractContainerMenu menu;
    private final Inventory inventory;

    public PlayerInventoryWidget(
            AbstractContainerMenu menu,
            Inventory inventory,
            int x,
            int y
    ) {
        super(
                x,
                y,
                9 * SLOT_SIZE,
                4 * SLOT_SIZE,
                Component.empty()
        );

        this.menu = menu;
        this.inventory = inventory;

        buildSlots();
    }

    private void buildSlots() {
        /*
         * Main inventory:
         *
         * 0  1  2  3  4  5  6  7  8
         * 9 10 11 12 13 14 15 16 17
         * 18 19 20 21 22 23 24 25 26
         *
         * Hotbar:
         *
         * 27 28 29 30 31 32 33 34 35
         */

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addPlayerSlot(
                        9 + row * 9 + column,
                        column,
                        row
                );
            }
        }

        for (int column = 0; column < 9; column++) {
            addPlayerSlot(
                    column,
                    column,
                    3
            );
        }
    }

    private void addPlayerSlot(
            int inventoryIndex,
            int column,
            int row
    ) {
        Slot menuSlot = findPlayerSlot(inventoryIndex);

        if (menuSlot == null)
            return;

        addChild(
                new SlotWidget(
                        menu,
                        menuSlot,
                        column * SLOT_SIZE,
                        row * SLOT_SIZE
                )
        );
    }

    @Nullable
    private Slot findPlayerSlot(int inventoryIndex) {
        for (Slot slot : menu.slots) {
            if (slot.container == inventory &&
                    slot.getContainerSlot() == inventoryIndex) {
                return slot;
            }
        }

        return null;
    }
}