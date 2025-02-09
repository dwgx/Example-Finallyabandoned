package com.example.mod.protocol.heypixel.check.c2s;

import com.example.mod.protocol.heypixel.HeypixelHandler;
import com.example.mod.protocol.heypixel.HeypixelProtocol;
import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.utils.BufferHelper;
import com.example.mod.utils.json.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.minecraft.network.PacketByteBuf;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.value.Variable;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.mod.client.GameAccessor.mc;


//@StringEncryption
//@ControlFlowObfuscation
public class ReflectDataC2SPacket extends HeypixelPacket {
    public static String[] allowedPackages = {
        "com.heypixel",
        "com.mojang",
        "net.minecraft",
        "net.minecraftforge",
        "cpw.mods",
        "org.spongepowered",
        "software.bernie.geckolib3",
        "me.jellysquid.mods.sodium",
        "net.fabricmc.loader",
        "repack.joml"
    };
    public String jsonData;
    public static final Gson GSON = GsonUtils.newBuilder().create();

    public ReflectDataC2SPacket(String str) {
        try {
            this.jsonData = str;
            System.out.println("action: " + str);
        } catch (Exception e) {
            e.printStackTrace();
            this.jsonData = "";
        }
    }


    public ReflectDataC2SPacket(PacketByteBuf friendlyByteBuf) {
    }

    public static void sendCheckPacket(HeypixelHandler manager, String str) {
        new ReflectDataC2SPacket(
            !str.startsWith("[{")
                ? JsonParser.parseString(str).getAsJsonObject().get(manager.getEncryptKey()).getAsString()
                :
                //processJsonAction(str)
                getUuid(manager)
        ).set(manager).sendMsgpack();
    }

    private static String getUuid(HeypixelHandler manager) {
        int port = 25565;
        if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null) {
            var addr = mc.getNetworkHandler().getServerInfo().address;
            if (addr.contains(":")) {
                port = Integer.parseInt(addr.split(":")[1]);
            }
        }

        return manager.protocol.getPlayerUUIDA(port);
    }

    public static boolean isAllowedPackage(Map<String, String> map) {
        boolean z = false;
        String[] strArr = allowedPackages;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (map.get("className").startsWith(strArr[i])) {
                z = true;
                break;
            }
            i++;
        }
        return z;
    }

    public static String processJsonAction(String data) {
        List<Map<String, String>> var1 = GSON.fromJson(data, List.class);
        Object result = null;

        try {
            Iterator<Map<String, String>> iterator = var1.iterator();

            while (iterator.hasNext()) {
                Map<String, String> map = iterator.next();
                if (!isAllowedPackage(map)) {
                    return String.valueOf(result);
                }

                String action = map.get("action");
                byte actionByte = switch (action) {
                    case "getEnumOrdinal" -> 1;
                    default -> 0;
                };

                Class clazz;
                return switch (actionByte) {
                    case 0 -> String.valueOf(mc.player.getPlayerListEntry().getProfile().getId());
                    case 1 -> {
                        System.out.println("enum check: " + data);
//                        clazz = Class.forName(map.get("className"));
//                        if (!clazz.isEnum()) {
//                            result = "Error Json!";
//                        } else {
//                            Object[] enumConstants = clazz.getEnumConstants();
//                            Object[] constants = enumConstants;
//                            int length = enumConstants.length;
//
//                            for (int i = 0; i < length; ++i) {
//                                Object var13 = constants[i];
//                                if (var13.toString().equals(map.get("enumName"))) {
//                                    result = ((Enum) var13).ordinal();
//                                    break;
//                                }
//                            }
//                        }

                        yield String.valueOf(result);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + actionByte);
                };
            }

            return String.valueOf(result);
        } catch (Exception e) {
            result = "CheckError";
            return String.valueOf(result);
        }
    }

    @Override
    public void encode(MessageBufferPacker packer) throws IOException {
        packer.packValue(new Variable().setStringValue(this.jsonData));
        packer.packValue(new Variable().setStringValue(HeypixelProtocol.get().getPlayerUUID()));
    }

    @Override
    public void encode(PacketByteBuf buf, BufferHelper bufferHelper) {
        bufferHelper.writeString(buf, this.jsonData);
    }
}
