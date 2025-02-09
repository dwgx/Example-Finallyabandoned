package com.example;

import net.fabricmc.loader.api.FabricLoader;

public class Presents {
    public static boolean SODIUM_PRESENT = false;
    public static boolean IRIS_PRESENT = false;
    public static boolean VIA_FABRIC_PLUS_PRESENT = false;

    public Presents() {

    }

    public void init() {
        SODIUM_PRESENT = FabricLoader.getInstance().isModLoaded("sodium");
        IRIS_PRESENT = FabricLoader.getInstance().isModLoaded("iris");
        VIA_FABRIC_PLUS_PRESENT = FabricLoader.getInstance().isModLoaded("viafabricplus");
    }
}
