package com.example.mod.utils.render.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.*;

import java.util.Arrays;
import java.util.Map;

@Deprecated
public class GLContextCache {
    private final GLState state = new GLState();
    private final GLGets gets = new GLGets();
    private final GLFeatures features = new GLFeatures();

    public GLContextCache() {
        this.state.major = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
        this.state.minor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);

        this.state.vendor = GL11.glGetString(GL11.GL_VENDOR);
        this.state.renderer = GL11.glGetString(GL11.GL_RENDERER);
        this.state.version = GL11.glGetString(GL11.GL_VERSION);
        this.state.shader = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);

        this.gets.maxCombinedTextureImageUnits = GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
        this.gets.maxDrawBuffers = GL11.glGetInteger(GL30.GL_MAX_DRAW_BUFFERS);
        this.gets.maxRenderbufferSize = GL11.glGetInteger(GL30.GL_MAX_RENDERBUFFER_SIZE);
        this.gets.maxSamples = GL11.glGetInteger(GL30.GL_MAX_SAMPLES);
        this.gets.maxTextureImageUnits = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
        this.gets.maxTransformFeedbackSeparateAttribs = GL11.glGetInteger(GL30.GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS);
        this.gets.maxUniformBlockSize = GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE);
        this.gets.maxUniformBufferBindings = GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS);
        this.gets.numProgramBinaryFormats = GL11.glGetInteger(GL41.GL_NUM_PROGRAM_BINARY_FORMATS);
        this.gets.uniformBufferOffsetAlignment = GL11.glGetInteger(GL31.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT);
    }

    public void save() {
        this.state.vao.update();
        this.state.program.update();
        this.state.raster.update();
        this.state.stencil.update();
        this.state.polygonOffset.update();
        this.state.enables.update();
        this.state.buffers.update();
        this.state.arrays.update();
        this.state.textures.update();
        this.state.pixel.update();
        this.state.window.update();
    }

    public void restore() {
/*
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.state.drawFbo);
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.state.readFbo);
 */

        // state.program
        GlStateManager._glUseProgram(this.state.program.use);

        // state.raster
        GL11.glFrontFace(this.state.raster.frontFace);
        GL11.glCullFace(this.state.raster.cullFace);
        GL20.glBlendEquationSeparate(
                this.state.raster.blendEquationRGB,
                this.state.raster.blendEquationA
        );
        RenderSystem.blendFuncSeparate(
                this.state.raster.blendFunctionSrcRGB,
                this.state.raster.blendFunctionDstRGB,
                this.state.raster.blendFunctionSrcA,
                this.state.raster.blendFunctionDstA
        );
        RenderSystem.colorMask(
                this.state.raster.colorMask,
                this.state.raster.colorMask,
                this.state.raster.colorMask,
                this.state.raster.colorMask
        );
        RenderSystem.depthMask(this.state.raster.depthMask);
        RenderSystem.depthFunc(this.state.raster.depthFunc);

        // state.stencil
        GL20.glStencilFuncSeparate(
                GL11.GL_FRONT,
                this.state.stencil.front.func,
                this.state.stencil.front.ref,
                this.state.stencil.front.mask
        );
        GL20.glStencilFuncSeparate(
                GL11.GL_BACK,
                this.state.stencil.back.func,
                this.state.stencil.back.ref,
                this.state.stencil.back.mask
        );
        GL20.glStencilOpSeparate(
                GL11.GL_FRONT,
                this.state.stencil.frontOp.sfail,
                this.state.stencil.frontOp.dpfail,
                this.state.stencil.frontOp.dppass
        );
        GL20.glStencilOpSeparate(
                GL11.GL_BACK,
                this.state.stencil.backOp.sfail,
                this.state.stencil.backOp.dpfail,
                this.state.stencil.backOp.dppass
        );

/*
        GL20.glStencilMaskSeparate(
                GL11.GL_FRONT,
                this.state.stencil.front.stencilMask
        );
        GL20.glStencilMaskSeparate(
                GL11.GL_BACK,
                this.state.stencil.back.stencilMask
        );
 */

        // state.polygonOffset
        RenderSystem.polygonOffset(
                this.state.polygonOffset.factor,
                this.state.polygonOffset.units
        );

        // state.enables
        for (int index : Arrays.asList(
                GL11.GL_BLEND,
                GL11.GL_CULL_FACE,
                GL11.GL_SCISSOR_TEST,
                GL11.GL_DEPTH_TEST,
                GL11.GL_STENCIL_TEST,
                GL11.GL_DITHER,
                GL13.GL_SAMPLE_ALPHA_TO_COVERAGE,
                GL13.GL_SAMPLE_COVERAGE,
                GL11.GL_POLYGON_OFFSET_FILL
        )) {
            if (this.state.enables.caps.get(index)) {
                GL11.glEnable(index);
            } else {
                GL11.glDisable(index);
            }
        }

        // state.arrays
        RenderSystem.glBindVertexArray(this.state.arrays.vertexArray);

        // state.textures
        RenderSystem.activeTexture(this.state.textures.active);
        GL33.glBindSampler(0, this.state.textures.samplerBinding);

        // state.buffers
        this.state.buffers.bind(GL15.GL_ARRAY_BUFFER);
        this.state.buffers.bind(GL15.GL_ELEMENT_ARRAY_BUFFER);
        //this.state.buffers.bind(GL13.GL_SAMPLE_BUFFERS);
        this.state.buffers.bind(GL31.GL_UNIFORM_BUFFER);
        this.state.buffers.bind(GL30.GL_TRANSFORM_FEEDBACK_BUFFER);
        this.state.buffers.bind(GL43.GL_SHADER_STORAGE_BUFFER);
        this.state.buffers.bind(GL21.GL_PIXEL_PACK_BUFFER);
        this.state.buffers.bind(GL21.GL_PIXEL_UNPACK_BUFFER);

        // state.pixel
        for (Map.Entry<Integer, Integer> entry : this.state.pixel.getPixelStateMap().entrySet()) {
            RenderSystem.pixelStore(entry.getKey(), entry.getValue());
        }

        // state.window
        RenderSystem.enableScissor(
                this.state.window.scissor.x,
                this.state.window.scissor.y,
                this.state.window.scissor.z,
                this.state.window.scissor.w
        );

        RenderSystem.viewport(
                this.state.window.viewport.x,
                this.state.window.viewport.y,
                this.state.window.viewport.z,
                this.state.window.viewport.w
        );

        GL41.glDepthRangef(
                this.state.window.depthRange.x,
                this.state.window.depthRange.y
        );
    }
}
