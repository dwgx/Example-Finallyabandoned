package com.example.mod.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public class Renderer3D {
    private Renderer3D() {}

    public static void drawFilledBox(Matrix4f matrix4f, Box box, float red, float green, float blue, float alpha) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        /*
                float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
         */

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        bufferBuilder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
}
