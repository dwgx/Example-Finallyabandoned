package com.example.mod.protocol.heypixel.hwid;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class HeypixelHwid {
    // User id
    @SerializedName("user")
    public String user;

    // Hardware id
    @SerializedName("cpu")
    public CpuInfo cpu;
    @SerializedName("system")
    public String system;
    @SerializedName("nel_disk")
    public String nel_disk;

    // Network
    @SerializedName("network_hwids")
    public List<String> network_hwids;
    @SerializedName("network_interfaces")
    public List<Map<String, String>> network_interfaces;

    // Motherboard
    @SerializedName("baseboards")
    public Map<String, String> baseboards;
    @SerializedName("baseboard_serial")
    public String baseboard_serial;

    // Disks
    @SerializedName("disks")
    public List<Map<String, String>> disks;
    @SerializedName("disk_serials")
    public List<String> disk_serials;
}
