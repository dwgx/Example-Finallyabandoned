package com.example.mod.utils.render.skija;

import com.example.mod.utils.render.gl.GLStateCacheManager;
import com.example.utils.pattern.Singleton;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.impl.Stats;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.example.client.GameAccessor.mc;

public class Skija {
    private static final Logger LOGGER = LoggerFactory.getLogger(Skija.class);

    private final GLStateCacheManager cacheManager = new GLStateCacheManager();

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
                /*stencil*/ 16, // 8
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

    public void draw(Consumer<Canvas> consumer) {
        if (this.context == null) {
            return;
        }

        this.cacheManager.save();

        RenderSystem.clearColor(0f, 0f, 0f, 0f);

        this.context.resetGLAll();

        this.canvas.save();
        consumer.accept(this.canvas);

        RenderSystem.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
        RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        RenderSystem.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);

        this.canvas.restore();

        this.surface.flushAndSubmit();

        this.cacheManager.restore();
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

    public GLStateCacheManager getCacheManager() {
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
