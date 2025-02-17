package com.example.mod.utils.render.gl;

import org.lwjgl.opengl.*;

// TODO: Use RenderSystem/GLStateManager
public class GLStateCache {
    private final GLProperties props = new GLProperties();
    private final GLData data = new GLData();

    public void save() {
        this.data.update();

        GL11.glGetIntegerv(GL13.GL_ACTIVE_TEXTURE, props.lastActiveTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glGetIntegerv(GL20.GL_CURRENT_PROGRAM, props.lastProgram);
        GL11.glGetIntegerv(GL11.GL_TEXTURE_BINDING_2D, props.lastTexture);
        if (this.data.getVersion() >= 330) {
            GL11.glGetIntegerv(GL33.GL_SAMPLER_BINDING, props.lastSampler);
        }
        GL11.glGetIntegerv(GL15.GL_ARRAY_BUFFER_BINDING, props.lastArrayBuffer);
        GL11.glGetIntegerv(GL30.GL_VERTEX_ARRAY_BINDING, props.lastVertexArrayObject);
        if (this.data.getVersion() >= 200) {
            GL11.glGetIntegerv(GL11.GL_POLYGON_MODE, props.lastPolygonMode);
        }
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, props.lastViewport);
        GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, props.lastScissorBox);
        GL11.glGetIntegerv(GL14.GL_BLEND_SRC_RGB, props.lastBlendSrcRgb);
        GL11.glGetIntegerv(GL14.GL_BLEND_DST_RGB, props.lastBlendDstRgb);
        GL11.glGetIntegerv(GL14.GL_BLEND_SRC_ALPHA, props.lastBlendSrcAlpha);
        GL11.glGetIntegerv(GL14.GL_BLEND_DST_ALPHA, props.lastBlendDstAlpha);
        GL11.glGetIntegerv(GL20.GL_BLEND_EQUATION_RGB, props.lastBlendEquationRgb);
        GL11.glGetIntegerv(GL20.GL_BLEND_EQUATION_ALPHA, props.lastBlendEquationAlpha);
        this.props.lastEnableBlend = GL11.glIsEnabled(GL11.GL_BLEND);
        this.props.lastEnableCullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        this.props.lastEnableDepthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        this.props.lastEnableStencilTest = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
        this.props.lastEnableScissorTest = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        if (this.data.getVersion() >= 310) {
            this.props.lastEnablePrimitiveRestart = GL11.glIsEnabled(GL31.GL_PRIMITIVE_RESTART);
        }

        this.props.lastDepthWriteMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
    }

    public void restore() {
        GL20.glUseProgram(this.props.lastProgram[0]);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.props.lastTexture[0]);
        if (this.data.getVersion() >= 330) {
            GL33.glBindSampler(0, this.props.lastSampler[0]);
        }
        GL13.glActiveTexture(this.props.lastActiveTexture[0]);
        GL30.glBindVertexArray(this.props.lastVertexArrayObject[0]);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.props.lastArrayBuffer[0]);
        GL20.glBlendEquationSeparate(this.props.lastBlendEquationRgb[0], this.props.lastBlendEquationAlpha[0]);
        GL14.glBlendFuncSeparate(this.props.lastBlendSrcRgb[0], this.props.lastBlendDstRgb[0], this.props.lastBlendSrcAlpha[0], this.props.lastBlendDstAlpha[0]);
        if (this.props.lastEnableBlend) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
        if (this.props.lastEnableCullFace) GL11.glEnable(GL11.GL_CULL_FACE);
        else GL11.glDisable(GL11.GL_CULL_FACE);
        if (this.props.lastEnableDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (this.props.lastEnableStencilTest) GL11.glEnable(GL11.GL_STENCIL_TEST);
        else GL11.glDisable(GL11.GL_STENCIL_TEST);
        if (this.props.lastEnableScissorTest) GL11.glEnable(GL11.GL_SCISSOR_TEST);
        else GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GL11.glDepthMask(this.props.lastDepthWriteMask);

        if (this.data.getVersion() >= 310) {
            if (this.props.lastEnablePrimitiveRestart) {
                GL11.glEnable(GL31.GL_PRIMITIVE_RESTART);
            } else {
                GL11.glDisable(GL31.GL_PRIMITIVE_RESTART);
            }
        }
        if (this.data.getVersion() >= 200) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, this.props.lastPolygonMode[0]);
        }
        GL11.glViewport(this.props.lastViewport[0], this.props.lastViewport[1], this.props.lastViewport[2], this.props.lastViewport[3]);
        GL11.glScissor(this.props.lastScissorBox[0], this.props.lastScissorBox[1], this.props.lastScissorBox[2], this.props.lastScissorBox[3]);
    }
}
