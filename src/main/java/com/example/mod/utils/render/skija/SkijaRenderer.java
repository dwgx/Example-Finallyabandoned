package com.example.mod.utils.render.skija;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;

public class SkijaRenderer {
    public static void drawBackground(int a, int r, int g, int b) {
        Skija.getInstance().draw(canvas -> {
            canvas.clear(Color.makeARGB(a, r, g, b));
        });
    }

    public static void drawRRect(float x, float y, float width, float height, float radius, Paint paint) {
        Skija.getInstance().draw(canvas -> {
            canvas.drawRRect(RRect.makeXYWH(x, y, width, height, radius), paint);
        });
    }

    public static void drawRect(float x, float y, float width, float height, Paint paint) {
        Skija.getInstance().draw(canvas -> {
            canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint);
        });
    }

    public static void drawText(String text, float x, float y, Font font, Paint paint) {
        Skija.getInstance().draw(canvas -> {
            canvas.drawString(text, x, y, font, paint);
        });
    }

    public static void drawCircle(float cx, float cy, float radius, Paint paint) {
        Skija.getInstance().draw(canvas -> {
            canvas.drawCircle(cx, cy, radius, paint);
        });
    }

    public static void drawPath(Path path, Paint paint) {
        Skija.getInstance().draw(canvas -> {
            canvas.drawPath(path, paint);
        });
    }

    public static void drawImage(Image image, float x, float y) {
        Skija.getInstance().draw(canvas -> {
            canvas.drawImage(image, x, y);
        });
    }
}