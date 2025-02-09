package com.example.mod.managers;

import com.example.utils.pattern.Singleton;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class ScriptManager {
    private final Globals globals = JsePlatform.standardGlobals();

    public static ScriptManager getInstance() {
        return Singleton.getInstance(ScriptManager.class);
    }
}
