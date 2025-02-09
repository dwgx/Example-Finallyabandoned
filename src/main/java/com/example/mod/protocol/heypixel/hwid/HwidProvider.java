package com.example.mod.protocol.heypixel.hwid;

import com.example.utils.math.RandomUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

public class HwidProvider {
    public static final List<CpuInfo> CPU_INFOS = List.of(
        new CpuInfo("i9-11900K ", "BFEBFBFF000A0671", "11th Gen Intel(R) Core(TM) i9-11900K @ 3.50GHz", "Family 6 Model 167 Stepping 1"),
        new CpuInfo("i7-12700K", "BFEBFBFF00090672", "12th Gen Intel(R) Core(TM) i7-12700K", "Family 6 Model 151 Stepping 2"),
        new CpuInfo("i7-11700K", "BFEBFBFF000A0671", "11th Gen Intel(R) Core(TM) i5-11600K @ 3.90GHz", "Family 6 Model 167 Stepping 1"),
        new CpuInfo("i7-10850K", "BFEBFBFF000A0655", "Intel(R) Core(TM) i7-10850K CPU @ 3.60GHz", "Family 6 Model 165 Stepping 5"),
        new CpuInfo("i7-10750H", "BFEBFBFF000A0652", "Intel(R) Core(TM) i7-10750H CPU @ 2.60GHz", "Family 6 Model 165 Stepping 2"),
        new CpuInfo("i7-10700K", "BFEBFBFF000A0654", "Intel(R) Core(TM) i7-10700K CPU @ 3.80GHz", "Family 6 Model 165 Stepping 5"),
        new CpuInfo("i7-10700", "BFEBFBFF000A0654", "Intel(R) Core(TM) i7-10700 CPU @ 2.90GHz", "Family 6 Model 165 Stepping 5"),
        new CpuInfo("i7-9700K", "BFEBFBFF000906EC", "Intel(R) Core(TM) i7-9700K CPU @ 3.60GHz", "Family 6 Model 158 Stepping 13"),
        new CpuInfo("i7-9700K", "BFEBFBFF000906EC", "Intel(R) Core(TM) i7-9700K CPU @ 3.60GHz", "Family 6 Model 158 Stepping 12"),
        new CpuInfo("i7-9700", "BFEBFBFF000906ED", "Intel(R) Core(TM) i7-9700 CPU @ 3.00GHz", "Family 6 Model 158 Stepping 13"),
        new CpuInfo("i7-9700F", "BFEBFBFF000906ED", "Intel(R) Core(TM) i7-9700F CPU @ 3.00GHz", "Family 6 Model 158 Stepping 13"),
        new CpuInfo("i7-8700K", "BFEBFBFF000906EA", "Intel(R) Core(TM) i7-8700K CPU @ 3.70GHz", "Family 6 Model 158 Stepping 10"),
        new CpuInfo("i7-8700", "BFEBFBFF000906EA", "Intel(R) Core(TM) i7-8700 CPU @ 3.20GHz", "Family 6 Model 158 Stepping 10"),
        new CpuInfo("i7-8700T", "BFEBFBFF000906EA", "Intel(R) Core(TM) i7-8700T CPU @ 2.40GHz", "Family 6 Model 158 Stepping 10"),
        new CpuInfo("i7-6700K", "BFEBFBFF000506E3", "Intel(R) Core(TM) i5-6700K CPU @ 4.00GHz", "Family 6 Model 94 Stepping 3"),
        new CpuInfo("i5-12500", "BFEBFBFF00090672", "12th Gen Intel(R) Core(TM) i5-12500", "Family 6 Model 151 Stepping 5"),
        new CpuInfo("i5-11400F", "BFEBFBFF000A0671", "11th Gen Intel(R) Core(TM) i5-11400F @ 2.60GHz", "Family 6 Model 167 Stepping 1"),
        new CpuInfo("i5-11400H", "BFEBFBFF000806D1", "11th Gen Intel(R) Core(TM) i5-11400H @ 2.70GHz", null),
        new CpuInfo("i5-10500", "BFEBFBFF000A0655", "Intel(R) Core(TM) i5-10500 CPU @ 3.10GHz", "Family 6 Model 165 Stepping 3"),
        new CpuInfo("i5-10400F", "BFEBFBFF000A0650", "Intel(R) Core(TM) i5-10400F CPU @ 2.90GHz", "Family 6 Model 165 Stepping 3"),
        new CpuInfo("i5-9600K", "BFEBFBFF000906EC", "Intel(R) Core(TM) i5-9600K CPU @ 3.70GHz", "Family 6 Model 158 Stepping 13"),
        new CpuInfo("i5-9600KF", "BFEBFBFF000906EC", "11th Gen Intel(R) Core(TM) i5-9600KF @ 3.70GHz", "Family 6 Model 158 Stepping 13"),
        new CpuInfo("i5-9500", "BFEBFBFF000906EA", "Intel(R) Core(TM) i5-9500 CPU @ 3.00GHz", "Family 6 Model 158 Stepping 10"),
        new CpuInfo("i5-1035G1", "BFEBFBFF000706E5", "Intel(R) Core(TM) i5-1035G1 CPU @ 1.00GHz", "Family 6 Model 126 Stepping 5"),
        new CpuInfo("i5-10210U", "BFEBFBFF000806EC", "Intel(R) Core(TM) i5-10210U CPU @ 1.60GHz", "Family 6 Model 142 Stepping 12"),
        new CpuInfo("i5-9400", "BFEBFBFF000906EA", "Intel(R) Core(TM) i5-9400F CPU @ 2.90GHz", "Family 6 Model 158 Stepping 13"),
        new CpuInfo("i5-9400F", "BFEBFBFF000906EA", "Intel(R) Core(TM) i5-9400F CPU @ 2.90GHz", "Family 6 Model 158 Stepping 10"),
        new CpuInfo("Ryzen 9 7950X", "178BFBFF00A60F12", "AMD Ryzen 9 7950X 16-Core Processor", "Family 25 Model 97 Stepping 2"),
        new CpuInfo("Ryzen 5 7600X", "178BFBFF00A60F12", "AMD Ryzen 5 7600X 6-Core Processor", "Family 25 Model 97 Stepping 2"),
        new CpuInfo("Ryzen 9 9950X", "178BFBFF00B40F40", "AMD Ryzen 9 9950X 16-Core Processor", "Family 26 Model 68 Stepping 0"),
        new CpuInfo("Ryzen 9 7950X3D", "178BFBFF00A60F12", "AMD Ryzen 9 7950X3D 16-Core Processor", "Family 25 Model 97 Stepping 2"),
        new CpuInfo("Ryzen 9 7900X3D", "178BFBFF00A60F12", "AMD Ryzen 9 7900X3D 12-Core Processor", "Family 25 Model 97 Stepping 2"),
        new CpuInfo("Ryzen 9 3950X", "178BFBFF00A60F12", "AMD Ryzen 9 3950X3D 16-Core Processor", "Family 23 Model 110 Stepping 0")
    );

    public final static List<String> MANUFACTURER = List.of(
        "Micro-Star International Co., Ltd.", "COLORFUL", "HUAWEI",
        "ASUS", "Gigabyte", "Not Applicable", "HASEE", "Lenovo",
        "HP", "Dell"
    );

    public final static List<String> VERSIONS = List.of(
        "P??I", "A??", "B??", "C??", "m??", "R??",
        "Not Applicable", "unknown", "??", "Z??", "??"
    );

    public static final List<String> wifi_names_1 = List.of(
        "Realtek PCIe GbE Family Controller-WFP Native MAC Layer LightWeight Filter-0000",
        "Realtek PCIe GbE Family Controller-Npcap Packet Driver (NPCAP)-0000",
        "Realtek PCIe GbE Family Controller-QoS Packet Scheduler-0000",
        "Realtek PCIe GbE Family Controller-WFP 802.3 MAC Layer LightWeight Filter-0000"
    );

    public static final List<String> wifi_names_2 = List.of(
        "Microsoft Wi-Fi Direct Virtual Adapter-WFP Native MAC Layer LightWeight Filter-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter-Native WiFi Filter Driver-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter-Npcap Packet Driver (NPCAP)-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter-QoS Packet Scheduler-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter-WFP 802.3 MAC Layer LightWeight Filter-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter #2-WFP Native MAC Layer LightWeight Filter-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter #2-Native WiFi Filter Driver-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter #2-Npcap Packet Driver (NPCAP)-0000",
        "Microsoft Wi-Fi Direct Virtual Adapter #2-QoS Packet Scheduler-0000"
    );

    public static final List<String> wifi_names_3 = List.of(
        "Intel(R) Wi-Fi 6 AX101",
        "Intel(R) Wi-Fi 6 AX200 160MHz",
        "Intel(R) Wi-Fi 6 AX201 160MHz",
        "Intel(R) Wi-Fi 6 AX203",
        "Intel(R) Wi-Fi 6 AX204 160MHz",
        "Killer(R) Wi-Fi 6 AX1650w 160MHz Wireless Network Adapter (200D2W)",
        "Killer(R) Wi-Fi 6 AX1650x 160MHz Wireless Network Adapter (200NGW)",
        "Killer(R) Wi-Fi 6 AX1650s 160MHz Wireless Network Adapter (201D2W)",
        "Killer(R) Wi-Fi 6 AX1650i 160MHz Wireless Network Adapter (201NGW)"
    );

    public static final List<String> disk_names = List.of(
        "SAMSUNG MZVKW512HMJP-000H1",
        "SAMSUNG MZVL21T0HCLR-00B00",
        "Samsung SSD 990 EVO Plus 4TB",
        "KINGSTON SV300S37A480G",
        "WD_BLACK SN850X 2000GB",
        "Samsung SSD 990 PRO 2TB",
        "Samsung SSD 850 EVO 1TB",
        "NVMe ADATA SX8200PNP",
        "KINGSTON SKC400S371T",
        "Samsung SSD 980 500GB",
        "Sabrent ROCKET 4.0 2TB",
        "INTEL SSDPEKNW010T8",
        "WDS500G3X0C-00SJG0",
        "NVMe CT1000T700SSD3",
        "WD_BLACK SN850 1TB",
        "HP SSD EX950 512GB",
        "ZTSSDPG3-480G-GE",
        "KINGSTON SHSS37A",
        "SanDisk SDSSDHP2",
        "ADATA SX8200NP",
        "CT2000T705SSD3",
        "CT1000P1SSD8",
        "ADATA SU800",
        "ADATA SP550",
        "R3SL480G"
        );


    public static CpuInfo randomCpuInfo() {
        return RandomUtils.nextList(CPU_INFOS);
    }

    public static String randomDisk() {
        return RandomUtils.nextList(disk_names);
    }

    public static String randomVersion(int id) {
        return RandomUtils.nextList(VERSIONS).replace("??", DigestUtils.sha1Hex(String.valueOf(id)).substring(10, 12));
    }

    public static String randomManufacturer() {
        return RandomUtils.nextList(MANUFACTURER);
    }

    public static List<String> randomNetwork(int count) {
        List<String> networks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var list = switch (RandomUtils.nextInt(0, 2)) {
                case 0 -> wifi_names_1;
                case 1 -> wifi_names_2;
                default -> wifi_names_3;
            };

            var id = RandomUtils.nextList(list);
            int loop = 0;
            int maxLoop = list.size();
            while (networks.contains(id)) {
                id = RandomUtils.nextList(list);
                if (loop++ > maxLoop) break;
            }

            networks.add(id);
        }

        return networks;
    }
}
