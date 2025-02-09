package com.example.mod.utils.io;

import com.example.Global;
import net.minecraft.resource.Resource;

import static com.example.mod.client.GameAccessor.mc;

public class ResourceUtils {
    public static Resource getResource(String path) {
        return mc.getResourceManager().getResource(Global.identifier(path)).orElse(null);
    }
}
