package com.example.mod.utils.render.gl;

public class GLProperties {
    // public final float[] orthoProjMatrix = new float[4 * 4];
    public final int[] lastActiveTexture = new int[1];
    public final int[] lastProgram = new int[1];
    public final int[] lastTexture = new int[1];
    public final int[] lastSampler = new int[1];
    public final int[] lastArrayBuffer = new int[1];
    public final int[] lastVertexArrayObject = new int[1];
    public final int[] lastPolygonMode = new int[2];
    public final int[] lastViewport = new int[4];
    public final int[] lastScissorBox = new int[4];
    public final int[] lastBlendSrcRgb = new int[1];
    public final int[] lastBlendDstRgb = new int[1];
    public final int[] lastBlendSrcAlpha = new int[1];
    public final int[] lastBlendDstAlpha = new int[1];
    public final int[] lastBlendEquationRgb = new int[1];
    public final int[] lastBlendEquationAlpha = new int[1];
    public boolean lastEnableBlend = false;
    public boolean lastEnableCullFace = false;
    public boolean lastEnableDepthTest = false;
    public boolean lastEnableStencilTest = false;
    public boolean lastEnableScissorTest = false;
    public boolean lastEnablePrimitiveRestart = false;

    public boolean lastDepthWriteMask = false;
}
