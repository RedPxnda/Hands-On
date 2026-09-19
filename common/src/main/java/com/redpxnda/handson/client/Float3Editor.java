package com.redpxnda.handson.client;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import smartin.miapi.editor.MiapiEditor;

import java.util.function.Consumer;

public class Float3Editor implements MiapiEditor {
    private final ImBoolean show = new ImBoolean(true);

    private final ImFloat x;
    private final ImFloat y;
    private final ImFloat z;
    private Consumer<Float3Editor> onChange = (f) -> {

    };

    public Float3Editor(float x, float y, float z) {
        this.x = new ImFloat(x);
        this.y = new ImFloat(y);
        this.z = new ImFloat(z);

        MiapiEditor.editors.add(this);
    }

    public Float3Editor(float x, float y, float z, Consumer<Float3Editor> onChange) {
        this.x = new ImFloat(x);
        this.y = new ImFloat(y);
        this.z = new ImFloat(z);

        MiapiEditor.editors.add(this);
    }

    public float getX() {
        return x.get();
    }

    public float getY() {
        return y.get();
    }

    public float getZ() {
        return z.get();
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!show.get()) {
            MiapiEditor.editors.remove(this);
            return;
        }

        ImGui.setNextWindowSize(300.0F, 150.0F, 4);

        if (ImGui.begin("Float 3 Editor##" + System.identityHashCode(this), show)) {
            ImGui.inputFloat("X", x);
            ImGui.inputFloat("Y", y);
            ImGui.inputFloat("Z", z);

            if (ImGui.button("Remove")) {
                show.set(false);
            }
            onChange.accept(this);
        }

        ImGui.end();
    }
}