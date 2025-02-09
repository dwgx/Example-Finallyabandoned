package com.example.mod.utils.input;

import com.example.mod.injection.mixin.minecraft.client.option.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import static com.example.mod.client.GameAccessor.mc;

public class InputUtils {
    public static boolean isKeyPressed(KeyBinding binding) {
        InputUtil.Key key = ((KeyBindingAccessor) binding).getBoundKey();
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), key.getCode());
    }
}
