package com.example.mod.protocol.heypixel.hwid;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

//@StringEncryption
//@ControlFlowObfuscation
public class HeypixelHwids {
    @SerializedName("data")
    public List<HeypixelHwid> hwids = new ArrayList<>();

    public boolean has(String s) {
        return hwids.stream().anyMatch(h -> s.equals(h.user));
    }

    public HeypixelHwid get(String s) {
        return hwids.stream().filter(h -> h.user.equals(s)).findFirst().orElse(null);
    }

    public void add(HeypixelHwid hwid) {
        if (!has(hwid.user)) {
            hwids.add(hwid);
        }
    }
}
