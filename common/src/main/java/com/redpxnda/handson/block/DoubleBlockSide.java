package com.redpxnda.handson.block;

import net.minecraft.util.StringRepresentable;

public enum DoubleBlockSide implements StringRepresentable {
    LEFT,
    RIGHT;

    public DoubleBlockSide getOtherHalf() {
        return this == LEFT ? RIGHT : LEFT;
    }

    @Override
    public String getSerializedName() {
        return this == LEFT ? "left" : "right";
    }

    @Override
    public String toString() {
        return getSerializedName();
    }
}
