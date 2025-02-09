package com.example.mod.enums.cmd;

public enum CmdType {
    LOCAL("Local Command", 1),
    NETWORK("Network Command", 2),
    RENDER("Render Command", 3),
    NOOP("No Operation Command", 4);

    private final String description;
    private final int code;

    CmdType(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public boolean has(CmdType type) {
        return this == type;
    }

    public static CmdType of(int code) {
        for (CmdType cmdType : CmdType.values()) {
            if (cmdType.getCode() == code) {
                return cmdType;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}