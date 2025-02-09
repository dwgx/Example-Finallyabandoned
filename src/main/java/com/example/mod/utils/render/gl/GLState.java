package com.example.mod.utils.render.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.*;

import java.util.HashMap;
import java.util.Map;

public class GLState {
    public int major = 0;
    public int minor = 0;

    public String vendor = null;
    public String renderer = null;
    public String version = null;
    public String shader = null;

    public Program program = new Program();

    public Vao vao = new Vao();
    public Raster raster = new Raster();
    public Stencil stencil = new Stencil();
    public PolygonOffset polygonOffset = new PolygonOffset();
    public Enables enables = new Enables();
    public Buffers buffers = new Buffers();
    public Arrays arrays = new Arrays();
    public Textures textures = new Textures();
    public Pixel pixel = new Pixel();
    public Window window = new Window();

    public byte age = 0;

    public static class Program {
        public int use = 0;

        public void update() {
            this.use = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        }
    }

    public static class Vao {
        public RenderPrimitive p = null;

        public void update() {

        }
    }

    public static class Raster {
        public int frontFace = GL11.GL_CCW;
        public int cullFace = GL11.GL_BACK;
        public int blendEquationRGB = GL14.GL_FUNC_ADD;
        public int blendEquationA = GL14.GL_FUNC_ADD;
        public int blendFunctionSrcRGB = GL11.GL_ONE;
        public int blendFunctionSrcA = GL11.GL_ONE;
        public int blendFunctionDstRGB = GL11.GL_ZERO;
        public int blendFunctionDstA = GL11.GL_ZERO;
        public boolean colorMask = true;
        public boolean depthMask = true;
        public int depthFunc = GL11.GL_LESS;

        public void update() {
            this.frontFace = GL11.glGetInteger(GL11.GL_FRONT_FACE);
            this.cullFace = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE);
            this.blendEquationRGB = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
            this.blendEquationA = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
            this.blendFunctionSrcRGB = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
            this.blendFunctionSrcA = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
            this.blendFunctionDstRGB = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
            this.blendFunctionDstA = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
            this.colorMask = GL11.glGetBoolean(GL11.GL_COLOR_WRITEMASK);
            this.depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            this.depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        }
    }

    public static class Stencil {
        public StencilFunc front = new StencilFunc();
        public StencilFunc back = new StencilFunc();
        public StencilOp frontOp = new StencilOp();
        public StencilOp backOp = new StencilOp();

        public void update() {
            this.front.func = GL11.glGetInteger(GL30.GL_STENCIL_FUNC);
            this.front.ref = GL11.glGetInteger(GL30.GL_STENCIL_REF);
            this.front.mask = GL11.glGetInteger(GL30.GL_STENCIL_VALUE_MASK);

            this.back.func = GL11.glGetInteger(GL30.GL_STENCIL_FUNC);
            this.back.ref = GL11.glGetInteger(GL30.GL_STENCIL_REF);
            this.back.mask = GL11.glGetInteger(GL30.GL_STENCIL_VALUE_MASK);
            // this.back.stencilMask = GL11.glGetInteger(GL30.GL_STENCIL_BACK_VALUE_MASK);

            this.frontOp.sfail = GL11.glGetInteger(GL30.GL_STENCIL_FAIL);
            this.frontOp.dpfail = GL11.glGetInteger(GL30.GL_STENCIL_PASS_DEPTH_FAIL);
            this.frontOp.dppass = GL11.glGetInteger(GL30.GL_STENCIL_PASS_DEPTH_PASS);

            this.backOp.sfail = GL11.glGetInteger(GL30.GL_STENCIL_FAIL);
            this.backOp.dpfail = GL11.glGetInteger(GL30.GL_STENCIL_PASS_DEPTH_FAIL);
            this.backOp.dppass = GL11.glGetInteger(GL30.GL_STENCIL_PASS_DEPTH_PASS);
        }

        public static class StencilFunc {
            public int func = GL11.GL_ALWAYS;
            public int ref = 0;
            public int mask = ~0;

            public boolean notEquals(StencilFunc rhs) {
                return func != rhs.func || ref != rhs.ref || mask != rhs.mask;
            }
        }

        public static class StencilOp {
            public int sfail = GL11.GL_KEEP;
            public int dpfail = GL11.GL_KEEP;
            public int dppass = GL11.GL_KEEP;

            public boolean notEquals(StencilOp rhs) {
                return sfail != rhs.sfail || dpfail != rhs.dpfail || dppass != rhs.dppass;
            }
        }
    }

    public static class PolygonOffset {
        public float factor = 0.0f;
        public float units = 0.0f;

        public void update() {
            this.factor = GL11.glGetFloat(GL11.GL_POLYGON_OFFSET_FACTOR);
            this.units = GL11.glGetFloat(GL11.GL_POLYGON_OFFSET_UNITS);
        }

        public boolean notEquals(PolygonOffset rhs) {
            return factor != rhs.factor || units != rhs.units;
        }
    }

    public static class Enables {
        public Bitset32 caps = new Bitset32();

        public void update() {
            caps.set(GL11.GL_BLEND, GL11.glIsEnabled(GL11.GL_BLEND));
            caps.set(GL11.GL_CULL_FACE, GL11.glIsEnabled(GL11.GL_CULL_FACE));
            caps.set(GL11.GL_SCISSOR_TEST, GL11.glIsEnabled(GL11.GL_SCISSOR_TEST));
            caps.set(GL11.GL_DEPTH_TEST, GL11.glIsEnabled(GL11.GL_DEPTH_TEST));
            caps.set(GL11.GL_STENCIL_TEST, GL11.glIsEnabled(GL11.GL_STENCIL_TEST));
            caps.set(GL11.GL_DITHER, GL11.glIsEnabled(GL11.GL_DITHER));
            caps.set(GL13.GL_SAMPLE_ALPHA_TO_COVERAGE, GL11.glIsEnabled(GL13.GL_SAMPLE_ALPHA_TO_COVERAGE));
            caps.set(GL13.GL_SAMPLE_COVERAGE, GL11.glIsEnabled(GL13.GL_SAMPLE_COVERAGE));
            caps.set(GL11.GL_POLYGON_OFFSET_FILL, GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_FILL));
        }
    }

    public static class Buffers {
        public final Map<Integer, Integer> bufferMap = new HashMap<>();

        public void update() {
            bufferMap.put(GL15.GL_ARRAY_BUFFER, GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING));
            bufferMap.put(GL15.GL_ELEMENT_ARRAY_BUFFER, GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING));
            bufferMap.put(GL13.GL_SAMPLE_BUFFERS, GL11.glGetInteger(GL13.GL_SAMPLE_BUFFERS));
            bufferMap.put(GL31.GL_UNIFORM_BUFFER, GL11.glGetInteger(GL31.GL_UNIFORM_BUFFER_BINDING));
            bufferMap.put(GL30.GL_TRANSFORM_FEEDBACK_BUFFER, GL11.glGetInteger(GL30.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING));
            bufferMap.put(GL43.GL_SHADER_STORAGE_BUFFER, GL11.glGetInteger(GL43.GL_SHADER_STORAGE_BUFFER_BINDING));
            bufferMap.put(GL21.GL_PIXEL_PACK_BUFFER, GL11.glGetInteger(GL21.GL_PIXEL_PACK_BUFFER_BINDING));
            bufferMap.put(GL21.GL_PIXEL_UNPACK_BUFFER, GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING));
        }

        public void bind(int target) {
            int buffer = this.bufferMap.getOrDefault(target, -1337);
            if (buffer != -1337) {
                RenderSystem.glBindBuffer(target, buffer);
            }
        }
    }

    public static class Arrays {
        public int vertexArray;

        public void update() {
            this.vertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        }
    }

    public static class Textures {
        public int active = 0;
        public int samplerBinding = 0;

        public void update() {
            this.active = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            this.samplerBinding = GL11.glGetInteger(GL33.GL_SAMPLER_BINDING);
        }
  /*
        public Unit[] units = new Unit[MAX_TEXTURE_UNIT_COUNT];

        public static class Unit {
            public int sampler = 0;
            public int target = 0;
            public int id = 0;
        }
   */
    }

    public static class Pixel {
        public final Map<Integer, Integer> pixelStateMap = new HashMap<>();

        public void update() {
            this.clear();
            this.put(GL11.GL_PACK_SWAP_BYTES, GL11.glGetInteger(GL11.GL_PACK_SWAP_BYTES));
            this.put(GL11.GL_PACK_LSB_FIRST, GL11.glGetInteger(GL11.GL_PACK_LSB_FIRST));
            this.put(GL11.GL_PACK_ROW_LENGTH, GL11.glGetInteger(GL11.GL_PACK_ROW_LENGTH));
            this.put(GL12.GL_PACK_IMAGE_HEIGHT, GL12.glGetInteger(GL12.GL_PACK_IMAGE_HEIGHT));
            this.put(GL11.GL_PACK_SKIP_PIXELS, GL11.glGetInteger(GL11.GL_PACK_SKIP_PIXELS));
            this.put(GL11.GL_PACK_SKIP_ROWS, GL11.glGetInteger(GL11.GL_PACK_SKIP_ROWS));
            this.put(GL12.GL_PACK_SKIP_IMAGES, GL12.glGetInteger(GL12.GL_PACK_SKIP_IMAGES));
            this.put(GL11.GL_PACK_ALIGNMENT, GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT));

            this.put(GL11.GL_UNPACK_SWAP_BYTES, GL11.glGetInteger(GL11.GL_UNPACK_SWAP_BYTES));
            this.put(GL11.GL_UNPACK_LSB_FIRST, GL11.glGetInteger(GL11.GL_UNPACK_LSB_FIRST));
            this.put(GL11.GL_UNPACK_ROW_LENGTH, GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH));
            this.put(GL12.GL_UNPACK_IMAGE_HEIGHT, GL12.glGetInteger(GL12.GL_UNPACK_IMAGE_HEIGHT));
            this.put(GL11.GL_UNPACK_SKIP_PIXELS, GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS));
            this.put(GL11.GL_UNPACK_SKIP_ROWS, GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS));
            this.put(GL12.GL_UNPACK_SKIP_IMAGES, GL12.glGetInteger(GL12.GL_UNPACK_SKIP_IMAGES));
            this.put(GL11.GL_UNPACK_ALIGNMENT, GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT));
        }

        public void put(int pname, int param) {
            this.pixelStateMap.put(pname, param);
        }

        public void clear() {
            this.pixelStateMap.clear();
        }

        public Map<Integer, Integer> getPixelStateMap() {
            return pixelStateMap;
        }
    }

    public static class Window {
        public Vec4gli scissor = new Vec4gli(0, 0, 0, 0);
        public Vec4gli viewport = new Vec4gli(0, 0, 0, 0);
        public Vec2glf depthRange = new Vec2glf(0.0f, 1.0f);

        public void update() {
            int[] scissorBox = new int[4];
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissorBox);
            this.scissor = new Vec4gli(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3]);

            int[] viewport = new int[4];
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            this.viewport = new Vec4gli(viewport[0], viewport[1], viewport[2], viewport[3]);

            float[] depthRange = new float[2];
            GL11.glGetFloatv(GL11.GL_DEPTH_RANGE, depthRange);
            this.depthRange = new Vec2glf(depthRange[0], depthRange[1]);
        }
    }

    public static class Vec4gli {
        public int x, y, z, w;

        public Vec4gli(int x, int y, int z, int w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
    }

    public static class Vec2glf {
        public float x, y;

        public Vec2glf(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    // Helper class for Bitset32
    public static class Bitset32 {
        private int bits;

        public void set(int index, boolean value) {
            if (value) {
                bits |= (1 << index);
            } else {
                bits &= ~(1 << index);
            }
        }

        public boolean get(int index) {
            return (bits & (1 << index)) != 0;
        }
    }
}
