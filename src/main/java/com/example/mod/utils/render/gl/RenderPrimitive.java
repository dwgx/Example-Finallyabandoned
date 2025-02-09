package com.example.mod.utils.render.gl;

import java.util.BitSet;

public class RenderPrimitive {
    public static final int MAX_VERTEX_ATTRIBUTE_COUNT = 16;

    public int[] vao = new int[2];
    public int elementArray = 0;
    public int indicesType = 0;

    public BitSet vertexAttribArray = new BitSet(16);

    public byte[] reserved = new byte[2];

    public byte vertexBufferVersion = 0;
    public byte stateVersion = 0;
    public byte nameVersion = 0;

    public byte indicesShift = 0;

    public int getIndicesType() {
        return indicesType;
    }
}
