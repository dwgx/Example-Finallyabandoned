package com.example.mod.protocol.heypixel.utils;


import com.example.mod.protocol.heypixel.hwid.HwidProfile;
import com.example.mod.protocol.heypixel.hwid.HwidProvider;
import com.example.mod.protocol.heypixel.hwid.ModuleInfos;
import com.example.utils.math.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class HardwareList {
    public static List<String> randomNetwork() {
        boolean hasNpcap = RandomUtils.nextBoolean();
        boolean hasvbox = RandomUtils.nextBoolean();
        boolean hasZeroTier = RandomUtils.nextBoolean();
        boolean hasHyperV = RandomUtils.nextBoolean();
        boolean hasVmware = RandomUtils.nextBoolean();

        var list = new ArrayList<String>();
        list.add("TAP-Windows Adapter V9-WFP Native MAC Layer LightWeight Filter-0000");
        if (hasNpcap) {
            list.add("TAP-Windows Adapter V9-Npcap Packet Driver (NPCAP)-0000");
        }
        if (hasNpcap && hasZeroTier) {
            list.add("ZeroTier Virtual Port-Npcap Packet Driver (NPCAP)-0000");
        }

        if (RandomUtils.nextBoolean()) {
            list.addAll(HwidProvider.randomNetwork(RandomUtils.nextInt(1,3)));
        }

        list.add("TAP-Windows Adapter V9-QoS Packet Scheduler-0000");

        if (hasHyperV) {
            list.add("Hyper-V Virtual Ethernet Adapter-WFP Native MAC Layer LightWeight Filter-0000");
        }

        list.add("TAP-Windows Adapter V9-WFP 802.3 MAC Layer LightWeight Filter-0000");

        if (hasZeroTier) {
            list.add("ZeroTier Virtual Port-QoS Packet Scheduler-0000");
            list.add("ZeroTier Virtual Port-WFP 802.3 MAC Layer LightWeight Filter-0000");
        }

        list.add("Realtek PCIe GbE Family Controller-WFP Native MAC Layer LightWeight Filter-0000");

        if (hasVmware && hasNpcap) {
            list.add("VMware Virtual Ethernet Adapter for VMnet1-Npcap Packet Driver (NPCAP)-0000");
        }

        if (hasNpcap) {
            list.add("Realtek PCIe GbE Family Controller-Npcap Packet Driver (NPCAP)-0000");
        }
        list.add("Realtek PCIe GbE Family Controller-QoS Packet Scheduler-0000");
        list.add("Realtek PCIe GbE Family Controller-WFP 802.3 MAC Layer LightWeight Filter-0000");
        list.add("Realtek PCIe GbE Family Controller");

        if (hasvbox && hasNpcap) {
            list.add("VirtualBox Host-Only Ethernet Adapter-Npcap Packet Driver (NPCAP)-0000");
        }

        if (hasvbox) {
            list.add("VirtualBox Host-Only Ethernet Adapter");
        }

        list.add("TAP-Windows Adapter V9");
        if (RandomUtils.nextBoolean()) {
            list.add("Famatech RadminVPN Ethernet Adapter");
        }
        if (hasVmware) {
            for (int i = 0; i < RandomUtils.nextInt(0, 5); i++) {
                var net = "VMware Virtual Ethernet Adapter for VMnet" + RandomUtils.nextInt(1, 8);
                if (!list.contains(net)) {
                    list.add(net);
                }
            }
        }
        if (hasZeroTier) {
            list.add("ZeroTier Virtual Port");
        }
        return list;
    }

    public static List<String> randomDLL(HwidProfile hardware) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < RandomUtils.nextInt(1, 2); i++) {
            var path = newDLL(hardware);

            if (path.startsWith("C:/windows") || path.startsWith("C:/WINDOWS")) {
                if (hardware.moduleSystemCount++ > hardware.moduleSystemLimit) {
                    continue;
                }
            }

            int maxLoop = 30;
            int loop = 0;
            while (list.contains(path)) {
                path = newDLL(hardware);

                if (path.startsWith("C:/windows") || path.startsWith("C:/WINDOWS")) {
                    if (hardware.moduleSystemCount > hardware.moduleSystemLimit) {
                        continue;
                    }
                }

                if (loop++ > maxLoop) break;
            }

            if (path.startsWith(hardware.nel_disk+ ":/MCL")) hardware.moduleNeteaseCount++;

            list.add(path);
        }

        return list;
    }

    private static String newDLL(HwidProfile hardware) {
        var moduleInfo = ModuleInfos.random();
        var path = moduleInfo.path;
        if (path.startsWith("D:/MCL")) path = path.replace("D:/MCL",hardware.nel_disk + ":/MCL");
        return path;
    }
}
