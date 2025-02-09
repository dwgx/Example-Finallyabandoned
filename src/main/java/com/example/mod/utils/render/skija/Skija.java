package com.example.mod.utils.render.skija;

import com.example.mod.utils.render.gl.GLContextCacheManager;
import com.example.utils.pattern.Singleton;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.impl.Stats;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL33;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.example.mod.client.GameAccessor.mc;

public class Skija {
    private static final Logger LOGGER = LoggerFactory.getLogger(Skija.class);

    private final GLContextCacheManager cacheManager = new GLContextCacheManager();

    private int width, height;

    private Surface surface;
    private DirectContext context;
    private BackendRenderTarget renderTarget;
    private Canvas canvas;

    private float dpi = 1f;

    private DrawData drawData = new DrawData();

    public void initSkia(Window window) {
        Stats.enabled = true;

        if (this.surface != null) {
            this.surface.close();
        }

        if (this.renderTarget != null) {
            this.renderTarget.close();
        }

        Framebuffer framebuffer = mc.getFramebuffer();

        this.width = window.getFramebufferWidth();
        this.height = window.getFramebufferHeight();

        // int fbId = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int fbId = framebuffer.fbo;
        this.context = DirectContext.makeGL();

        this.renderTarget = BackendRenderTarget.makeGL(
                (int) (width * dpi),
                (int) (height * dpi),
                /*samples*/ 0,
                /*stencil*/ 8,
                fbId,
                FramebufferFormat.GR_GL_RGBA8
        );

        this.surface = Surface.wrapBackendRenderTarget(
                this.context,
                this.renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.getSRGB()
        );

        this.canvas = this.surface.getCanvas();

/*
        if (this.drawData != null) {
            this.drawData.reset();
        }
 */

        LOGGER.info("FramebufferSize {}x{}, scale {}, window {}x{}", window.getFramebufferWidth(), window.getFramebufferHeight(), this.dpi, window.getWidth(), window.getHeight());
    }

    public void resize() {
        this.initSkia(mc.getWindow());
    }

    public void end() {
        if (this.context == null) {
            return;
        }

        this.context.resetAll();

        BufferRenderer.reset();
        GL33.glBindSampler(0, 0);

        RenderSystem.disableBlend();

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        RenderSystem.disableScissor();
        // Maybe need ScissorStack

        GL11.glDisable(GL11.GL_STENCIL_TEST);

        RenderSystem.disableDepthTest();
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);

        this.surface.flush();
    }

    public void begin() {
        if (this.context == null) {
            return;
        }

        RenderSystem.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
        RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        RenderSystem.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);

        RenderSystem.clearColor(0f, 0f, 0f, 0f);

        this.canvas.restore();
    }

    public void draw(Consumer<Canvas> consumer) {
        if (this.context == null) {
            return;
        }

        this.begin();

        // this.drawData.submit(consumer);
        consumer.accept(this.canvas);

        this.end();
    }

    public void render() {
        if (this.drawData == null) {
            return;
        }

        this.drawData.valid();

        Consumer<Canvas> command;
        while ((command = this.drawData.getCmdQueue().poll()) != null) {
            command.accept(this.canvas);
        }
    }

/*
    public void draw(Consumer<Canvas> consumer) {
        if (this.context == null) {
            return;
        }
        this.context.flush();

        this.cacheManager.save();

        RenderSystem.clearColor(0f, 0f, 0f, 0f);
        this.context.resetGLAll();

        // GL11.glDisable(GL11.GL_ALPHA_TEST);

        consumer.accept(this.getCanvas());

        SkijaDrawEvent event = new SkijaDrawEvent(this.surface, this.context, this.renderTarget, this.getCanvas());
        Global.getEventBus().post(event).now();


        this.cacheManager.restore();
    }
 */

    public GLContextCacheManager getCacheManager() {
        return cacheManager;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Surface getSurface() {
        return surface;
    }

    public DirectContext getContext() {
        return context;
    }

    public BackendRenderTarget getRenderTarget() {
        return renderTarget;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public float getDpi() {
        return dpi;
    }

    public DrawData getDrawData() {
        return drawData;
    }

    public static Skija getInstance() {
        return Singleton.getInstance(Skija.class);
    }
}
