package com.redpxnda.handson.client;

import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TinkeringScreen extends Screen {
    public static final InterpolateMode START_ANIM = new InterpolateMode.EaseInOut(4);

    protected BlockPos targetBlock;
    protected Direction workbenchDirection;
    public int animationTicks = 4;
    public boolean closing = false;

    public TinkeringScreen(BlockPos targetBlock, Direction workbenchDirection) {
        super(Component.empty());
        this.targetBlock = targetBlock;
        this.workbenchDirection = workbenchDirection;
    }

    @Override
    protected void init() {
        super.init();
        minecraft.options.hideGui = true;
    }

    @Override
    public void onClose() {
        closing = true;
    }

    public void onCloseReal() {
        super.onClose();
        minecraft.options.hideGui = false;
    }

    @Override
    public void mouseMoved(double d, double e) {
        super.mouseMoved(d, e);
    }

    @Override
    public void tick() {
        super.tick();
        if (animationTicks < 15 && !closing) animationTicks++;
        else if (closing)
            if (animationTicks > 4) animationTicks--;
            else onCloseReal();

    }

    public Vec3 getCamPos(Vec3 initial) {
        double targetX = getTargetBlock().getX() + 0.5;
        double targetY = getTargetBlock().getY() + 3;
        double targetZ = getTargetBlock().getZ() + 0.5;

        if (animationTicks < 15) {
            double cX = initial.x;
            double cY = initial.y;
            double cZ = initial.z;

            float partial = closing ? -Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true) : Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            float prog = Math.min((animationTicks + partial) / 15f, 1f);
            double rX = TinkeringScreen.START_ANIM.interpolate(prog, cX, targetX);
            double rY = TinkeringScreen.START_ANIM.interpolate(prog, cY, targetY);
            double rZ = TinkeringScreen.START_ANIM.interpolate(prog, cZ, targetZ);
            return new Vec3(rX, rY, rZ);
        } else {
            double mX = minecraft.mouseHandler.xpos();
            double mY = minecraft.mouseHandler.ypos();
            double width = minecraft.getWindow().getWidth();
            double height = minecraft.getWindow().getHeight();

            double oX = mX - width/2;
            double oY = mY - height/2;
            Vec2 vec = new Vec2((float) oX, (float) oY);
            Vec2 nVec = vec.normalized();
            float prog = vec.length() / 2000;

            return new Vec3(
                    MathUtil.lerp(prog, targetX, targetX + nVec.x),
                    targetY,
                    MathUtil.lerp(prog, targetZ, targetZ + nVec.y)
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {

    }

    public BlockPos getTargetBlock() {
        return targetBlock;
    }

    public Direction getWorkbenchDirection() {
        return workbenchDirection;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
