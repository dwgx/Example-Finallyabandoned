package com.example.mod.ui;

import com.example.mod.utils.render.skija.SkijaRenderer;
import com.example.mod.utils.render.skija.text.Texts;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static com.example.mod.client.GameAccessor.mc;

public class TestScreen extends Screen {
    public TestScreen() {
        super(Text.literal("Skija test"));
    }

    private Font font = new Font(Texts.makeFace(Texts.makeData("jet-brains-mono_regular", "ttf")), 30);

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // this.renderBackground(context, mouseX, mouseY, delta);

        if (MinecraftClient.getInstance().world != null) {
            Paint paint = new Paint().setARGB(255, 255, 255, 255);

            SkijaRenderer.drawText(
                    "Hello, World!", 50, 50, font, paint
            );

            SkijaRenderer.drawRRect(
                    50, 60, (float) mc.getWindow().getWidth() / 2, (float) mc.getWindow().getHeight() / 2, 4, paint
            );
        }

    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}
