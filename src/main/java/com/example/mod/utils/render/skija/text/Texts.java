package com.example.mod.utils.render.skija.text;

import com.example.mod.utils.io.ResourceUtils;
import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Typeface;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Texts {
    public static final Map<Integer, FontEntry> JET_BRAINS_MONO_REGULAR = new ConcurrentHashMap<>();

    public static void init() {
    }

    public static Data makeData(String name, String extension) {
        try {
            return Data.makeFromBytes(ResourceUtils.getResource("fonts/" + name + "." + extension).getInputStream().readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Data.makeEmpty();
    }

    public static Typeface makeFace(Data data) {
        return Typeface.makeFromData(data);
    }

    public static Font makeFont(Typeface typeface, int size) {
        return new Font(typeface, size);
    }

    public static Font makeFont(String name, String extension, int size) {
        return makeFont(makeFace(makeData(name, extension)), size);
    }

    public static FontEntry makeEntry(String name, String extension, int size) {
        Typeface typeface = makeFace(makeData(name, extension));
        Font font = new Font(typeface, size);

        return new FontEntry(typeface, font);
    }

    public static FontEntry getEntry(Map<Integer, FontEntry> map, int size) {
        return map.computeIfAbsent(
                size,
                key -> makeEntry("jet-brains-mono_regular", "ttf", key)
        );
    }
}
