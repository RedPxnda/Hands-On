package com.redpxnda.handson.client;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;
import smartin.miapi.editor.MiapiEditor;
import smartin.miapi.item.modular.Transform;

import java.util.function.Consumer;

public class TransformEditor implements MiapiEditor {
    private final ImBoolean show = new ImBoolean(true);
    private final ImFloat rotationX;
    private final ImFloat rotationY;
    private final ImFloat rotationZ;
    private final ImFloat translationX;
    private final ImFloat translationY;
    private final ImFloat translationZ;
    private final ImFloat scaleX;
    private final ImFloat scaleY;
    private final ImFloat scaleZ;
    private final Consumer<TransformEditor> onChange;

    public TransformEditor() {this(Transform.IDENTITY);}

    public TransformEditor(Transform transform) {this(transform, t -> {});}

    public TransformEditor(Transform transform, Consumer<TransformEditor> onChange) {
        Vector3f rotation = transform.getRotation();
        Vector3f translation = transform.getTranslation();
        Vector3f scale = transform.getScale();
        this.rotationX = new ImFloat(rotation.x);
        this.rotationY = new ImFloat(rotation.y);
        this.rotationZ = new ImFloat(rotation.z);
        this.translationX = new ImFloat(translation.x);
        this.translationY = new ImFloat(translation.y);
        this.translationZ = new ImFloat(translation.z);
        this.scaleX = new ImFloat(scale.x);
        this.scaleY = new ImFloat(scale.y);
        this.scaleZ = new ImFloat(scale.z);
        this.onChange = onChange;
        MiapiEditor.editors.add(this);
    }

    public Vector3f getRotation() {return new Vector3f(rotationX.get(), rotationY.get(), rotationZ.get());}

    public Vector3f getTranslation() {return new Vector3f(translationX.get(), translationY.get(), translationZ.get());}

    public Vector3f getScale() {return new Vector3f(scaleX.get(), scaleY.get(), scaleZ.get());}

    public Transform getTransform() {return new Transform(getRotation(), getTranslation(), getScale());}

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!show.get()) {
            MiapiEditor.editors.remove(this);
            return;
        }
        ImGui.setNextWindowSize(350.0F, 250.0F, 4);
        boolean open = ImGui.begin("Transform Editor##" + System.identityHashCode(this), show);
        if (!open) {
            MiapiEditor.editors.remove(this);
            ImGui.end();
            return;
        }
        if (ImGui.collapsingHeader("Rotation")) {
            ImGui.inputFloat("X##rotation", rotationX);
            ImGui.inputFloat("Y##rotation", rotationY);
            ImGui.inputFloat("Z##rotation", rotationZ);
        }
        if (ImGui.collapsingHeader("Translation")) {
            ImGui.inputFloat("X##translation", translationX);
            ImGui.inputFloat("Y##translation", translationY);
            ImGui.inputFloat("Z##translation", translationZ);
        }
        if (ImGui.collapsingHeader("Scale")) {
            ImGui.inputFloat("X##scale", scaleX);
            ImGui.inputFloat("Y##scale", scaleY);
            ImGui.inputFloat("Z##scale", scaleZ);
        }
        onChange.accept(this);
        ImGui.end();
    }
}