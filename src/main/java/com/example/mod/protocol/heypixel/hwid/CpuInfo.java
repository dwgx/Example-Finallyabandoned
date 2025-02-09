package com.example.mod.protocol.heypixel.hwid;

import com.google.gson.annotations.SerializedName;

public class CpuInfo {
    @SerializedName("name")
    public String name;

    @SerializedName("device")
    public String device;

    @SerializedName("model")
    public String model;

    @SerializedName("identifier")
    public String identifier;

    public CpuInfo(String name, String device, String model, String identifier) {
        this.name = name;
        this.device = device;
        this.model = model;
        this.identifier = identifier;
    }

    public String build() {
        return device + "|" + model + "|" + identifier;
    }
}
