package com.example.mod.enums;

/*
public class ModuleCategory {
    public static final String RAGE = "Rage";
    public static final String LEGIT = "Legit";
    public static final String MOVEMENT = "Movement";
    public static final String VISUAL = "Visual";
}
 */
public enum ModuleCategory {
    RAGE("Rage"),
    LEGIT("Legit"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    VISUAL("Visual"),
    MISCELLANEOUS("Miscellaneous"),
    EXPLOIT("Exploit");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}