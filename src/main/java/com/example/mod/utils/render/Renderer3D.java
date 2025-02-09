package com.example.mod.utils.render;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public class Renderer3D {
    public static void drawBox(MatrixStack matrixStack, Box box, float r, float g, float b, float a) {
        matrixStack.push();

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();
    }
}
