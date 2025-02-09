package com.example.mod.protocol.heypixel;

import com.example.mod.protocol.NetPayload;
import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.check.c2s.ClassesC2SPacket;
import com.example.mod.protocol.heypixel.check.s2c.SyncKeysS2CPacket;
import com.example.mod.protocol.heypixel.hwid.HwidProfile;
import com.example.mod.protocol.heypixel.utils.BufferHelper;
import com.example.mod.protocol.heypixel.utils.ClassesRandom;
import com.example.mod.protocol.heypixel.utils.HardwareList;
import com.example.mod.protocol.heypixel.utils.HeypixelVarUtils;
import com.example.mod.utils.player.ChatUtils;
import com.example.utils.math.RandomUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.commons.codec.digest.DigestUtils;
import org.msgpack.core.MessagePack;
import org.msgpack.value.Variable;

import java.io.IOException;
import java.util.*;

import static com.example.mod.client.GameAccessor.mc;


public class HeypixelHandler {
    public final HeypixelProtocol protocol;

    /**
     * Hwids
     */
    public HwidProfile hardware;

    public String rpcS1 = "EMPTY#1";
    public String rpcS2 = "EMPTY#2";
    public String rpcS3 = "EMPTY#3";
    public String rpcV1 = "BLANK";
    public String rpcV2 = "BLANK";
    public String rpcV3 = "BLANK";

    public List<byte[]> players;
    public int playerIndex;

    public byte[] key1;
    public byte[] key2;
    public byte[] key3;

    public byte[] clientToken;
    public byte[] serverToken;
    public int[] unknownArray;
    public byte[] unknownBytes;
    public byte[] encryptionKey;
    public byte[] encryptMode;

    public int modClassCount;
    public int loadClassCount;
    public int jmapLineCount;

    public int dllCount;
    public HashMap<String, String> tokenMap;
    public MessageDecoder msgDecoder = new MessageDecoder(null);
    public byte[] serverId;
    public byte[] encryptMode1;
    public Set<String> uniqueClassNames = new HashSet<>();
    public Set<String> newDLLs = new HashSet<>();
    public Set<String> allDLLS = new HashSet<>();

    public BufferHelper bufferHelper = new BufferHelper();
    public Identifier lastResourceLocation = null;

    public long lastActionTime = -1;
    public long lastUpdateClasses = -1;
    public long lastUpdateDlls = -1;
    public long nextSendClasses = -1;
    public long lastHeartbeatTime = 0;
    public boolean classesSent = false;

    public HeypixelHandler(HeypixelProtocol protocol) {
        this.protocol = protocol;
        tokenMap = new HashMap<>();

        initClasses();
        this.hardware = new HwidProfile();
    }

    public void initClasses() {
        loadClassCount = 73508 + RandomUtils.nextInt(-500, 800);
        modClassCount = 39299;
        jmapLineCount = 13390 + RandomUtils.nextInt(-170, 200);
        dllCount = 114;
    }

    // check
    public void sendGameInfo() {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte(1);
        buf.writeLong(System.currentTimeMillis());
        buf.writeLong(HeypixelProtocol.randomId);

        NetPayload.send(HeypixelProtocol.GAME_INFO_CHANNEL, buf);
        lastHeartbeatTime = System.currentTimeMillis();
    }

    public void sendEnvClasses() {
        Map<String, String> hashMap = ClassesRandom.randomCheck1();

        var packet = new ClassesC2SPacket(
                1,
                hashMap.size(),
                hashMap,
                0,
                modClassCount,
                modClassCount
        );
        packet.sendMsgpack();
    }

    public void sendHeartbeat() {
        try (var buffer = MessagePack.newDefaultBufferPacker()) {
            buffer.packInt(0);
            buffer.packString(protocol.getPlayerUUID());
            buffer.packValue(new Variable().setIntegerValue(protocol.runTime));
            buffer.packValue(new Variable().setIntegerValue(System.currentTimeMillis()));

            var buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBytes(buffer.toByteArray());
            NetPayload.send(HeypixelProtocol.CHECK_CHANNEL, buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendClasses() {
        uniqueClassNames.add("com.sun.jmx.remote.util");
        uniqueClassNames.add("com.sun.org.apache.xerces");
        uniqueClassNames.add("sun.text.resources.cldr.ext");
        uniqueClassNames.add("com.sun.xml.internal.stream");
        uniqueClassNames.add("org.openjdk.nashorn.internal.scripts");

        if (RandomUtils.nextBoolean())
            uniqueClassNames.add("jdk.internal.net.http.SocketTube$InternalWriteSubscriber$WriteSubscription");
        if (RandomUtils.nextBoolean())
            uniqueClassNames.add("net.minecraftforge.common.loot.CanToolPerformAction$Serializer");

        uniqueClassNames.add("sun.util.resources.cldr.provider");

        var classCheck = new ClassesC2SPacket(
                -1,
                uniqueClassNames.size(),
                uniqueClassNames,
                // 好像最新版又改回来了
                jmapLineCount,

//                -3,
                loadClassCount,
                loadClassCount
        );
        classCheck.sendMsgpack();
        uniqueClassNames.clear();

        var dllCheck = new ClassesC2SPacket(
                3,
                newDLLs.size(),
                newDLLs,
                0,
                dllCount,
                dllCount
        );
        dllCheck.sendMsgpack();
        allDLLS.clear();
    }

    public void sendClientInfo() {
        try {
            checkUserInfo();

            if (hardware.user == null) {
                ChatUtils.display(Text.literal("无法获取游戏用户信息"));
                return;
            }

            try (var buffer = MessagePack.newDefaultBufferPacker()) {
                buffer.packString(protocol.clientId.toString());
                buffer.packString(protocol.getPlayerUUID());
                buffer.packValue(new Variable().setIntegerValue(protocol.runTime));
                buffer.packBoolean(false);

                var mods = new ArrayList<String>();
                mods.add("minecaft");
                mods.add("entityculling");
                mods.add("armourers_workshop");
                mods.add("netease_official");
                mods.add("immediatelyfast");
                mods.add("culllessleaves");
                mods.add("heypixel");
                mods.add("nochatlagforge");
                mods.add("memoryleakfix");
                mods.add("reeses_sodium_options");
                mods.add("forge");
                mods.add("rubidium");
                mods.add("embeddiumplus");
                mods.add("iceberg");
                mods.add("geckolib3");

                buffer.packValue(new Variable().setStringValue(mods.toString()));

                var local = hardware.nel_disk;
                buffer.packValue(new Variable().setStringValue(local + ":\\MCLDownload\\Game\\.minecraft"));
                buffer.packValue(new Variable().setStringValue(local + ":\\MCLDownload\\ext\\jre-v64-220420\\jdk17"));

                buffer.packValue(hardware.cpu);
                buffer.packValue(hardware.baseboardSerial);
                buffer.packValue(hardware.diskSerials);
                buffer.packValue(hardware.networkHardware);
                buffer.packValue(hardware.user);
                buffer.packValue(hardware.baseboard);
                buffer.packValue(hardware.disks);
                buffer.packValue(hardware.networkInterfaces);
                buffer.packValue(hardware.system);
                buffer.packValue(hardware.neteaseUsersHash);
                var buf = new PacketByteBuf(Unpooled.buffer());

                HeypixelVarUtils.writeUnsignedInt(buf, 1);
                bufferHelper.writeByteArray(buf, buffer.toByteArray());

                NetPayload.send(HeypixelProtocol.CHECK_CHANNEL, buf);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // handler
    public HeypixelPacket decode(PacketByteBuf buf) {
        int pid = buf.readInt();
        var packet = HeypixelPacket.newS2CInstance(pid);
        if (packet != null) {
//            System.out.println("decodedd: " + pid);
            return packet.apply(buf);
        }
        return null;
    }

    public void syncServerInfo(JsonObject obj) {
        HeypixelProtocol.randomId = System.currentTimeMillis() - obj.get("ServerTime").getAsLong();
        JsonArray arr = obj.get("YkdsemRBPT0").getAsJsonArray();
        List<String> list = new ArrayList<>();
        for (JsonElement element : arr) {
            list.add(element.getAsString());
        }

        rpcS1 = DigestUtils.sha256Hex(obj.get(list.get(0)).getAsString());
        rpcS2 = DigestUtils.sha256Hex(obj.get(list.get(1)).getAsString());
        rpcS3 = DigestUtils.sha256Hex(obj.get(list.get(2)).getAsString());
        rpcV1 = DigestUtils.sha256Hex(obj.get(list.get(3)).getAsString());
        rpcV2 = String.valueOf(hardware.networkHardware);
        rpcV3 = String.valueOf(hardware.diskSerials);
    }

    public void processSyncKeys(SyncKeysS2CPacket syncKeysPacket) {
        String str = tokenMap.get(msgDecoder.decode(serverToken));
        String str2 = tokenMap.get(msgDecoder.decode(clientToken));
        String str3 = tokenMap.get(msgDecoder.decode(serverId));
        String decodedKeyA = msgDecoder.decode(syncKeysPacket.keyA);
        String decodedKeyB = msgDecoder.decode(syncKeysPacket.keyB);
        String decodedKeyC = msgDecoder.decode(syncKeysPacket.keyC);
        if (decodedKeyA.equals(str)) {
            key1 = syncKeysPacket.keyListA;
        } else if (decodedKeyA.equals(str2)) {
            key2 = syncKeysPacket.keyListA;
        } else if (decodedKeyA.equals(str3)) {
            key3 = syncKeysPacket.keyListA;
        }
        if (decodedKeyB.equals(str)) {
            key1 = syncKeysPacket.keyListB;
        } else if (decodedKeyB.equals(str2)) {
            key2 = syncKeysPacket.keyListB;
        } else if (decodedKeyB.equals(str3)) {
            key3 = syncKeysPacket.keyListB;
        }
        if (decodedKeyC.equals(str)) {
            key1 = syncKeysPacket.keyListC;
        } else if (decodedKeyC.equals(str2)) {
            key2 = syncKeysPacket.keyListC;
        } else if (decodedKeyC.equals(str3)) {
            key3 = syncKeysPacket.keyListC;
        }
    }

    public void process(HeypixelPacket heypixelPacket) {
        try {
//            System.out.println("apply: " + heypixelPacket.getClass().getName());
            heypixelPacket.manager = this;
            heypixelPacket.handle(MinecraftClient.getInstance().player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // class actions
    public void tickHeartbeat() {
        if (System.currentTimeMillis() - lastHeartbeatTime > 5 * 1000L) {
            sendHeartbeat();
            lastHeartbeatTime = System.currentTimeMillis();
        }
    }

    public void checkEnvClasses() {
        if (classesSent) {
            return;
        }
        classesSent = true;
        sendEnvClasses();
    }

    public void updateClasses() {
        final var second = 1000;

        // 随机17 - 38秒 加 170 - 300个class
        if (System.currentTimeMillis() - lastUpdateClasses > RandomUtils.nextInt(second * 17, second * 38)) {
            loadClassCount += RandomUtils.nextInt(170, 300);
            jmapLineCount += RandomUtils.nextInt(-10, 37);

            if (RandomUtils.nextBoolean()) {
                if (System.currentTimeMillis() - lastUpdateDlls > RandomUtils.nextInt(second * 27, second * 50)) {
                    var list = new ArrayList<String>();
                    for (int i = 0; i < RandomUtils.nextInt(1, 4); i++) {
                        var data = HardwareList.randomDLL(hardware);
                        for (String grab : data) {
                            if (!newDLLs.contains(grab) && !list.contains(grab)) {
                                list.add(grab);
                            }
                        }
                    }
                    newDLLs.addAll(list);
                    dllCount += list.size();

                    lastUpdateDlls = System.currentTimeMillis();
                }
            }

            lastUpdateClasses = System.currentTimeMillis();
        }
    }

    // events
    public void onClientTick() {
        checkEnvClasses();
        tickHeartbeat();
        updateClasses();
    }

    public void onServerEvent(S2CEvent s2CEvent) {
        String event = s2CEvent.getEvent();
        JsonObject data = s2CEvent.getData();
        if (event.equals("ServerRPC")) {
            rpcS1 = data.get("s1").getAsString();
            rpcS2 = data.get("s2").getAsString();
            rpcS3 = data.get("s3").getAsString();
        }
        if ("SyncServerInfo".equals(s2CEvent.getEvent())) {
            syncServerInfo(data);
        }
    }

    public void onAddEntity(World world, Entity entity) {
        try {
            if (!entity.getUuid().equals(mc.player.getUuid())) return;

            var location = world.getRegistryKey().getValue();
            sendClientInfo();
            if (System.currentTimeMillis() > nextSendClasses) {
                nextSendClasses = System.currentTimeMillis() + 1000;
                classesSent = false;
            }
            if (System.currentTimeMillis() > this.lastActionTime + 180000) {
                sendClasses();
                this.lastActionTime = System.currentTimeMillis();
            } else {
                if (this.lastResourceLocation == null || location.equals(this.lastResourceLocation)) {
                    return;
                }
                this.lastResourceLocation = location;
                if (System.currentTimeMillis() > this.lastActionTime + 60000) {
                    sendClasses();
                    this.lastActionTime = System.currentTimeMillis();
                }
            }
        } catch (Exception ex) {
        }
    }

    // helper
    public void checkUserInfo() {
        try {
            protocol.provider().refresh();

            if (mc.isInSingleplayer()) return;
            int port = 25565;
            if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null) {
                var addr = mc.getNetworkHandler().getServerInfo().address;
                if (addr.contains(":")) {
                    port = Integer.parseInt(addr.split(":")[1]);
                }
            }

            long userId = Long.parseLong(protocol.provider().getUserId(port, protocol.getPlayerProfile()));
            var hash = DigestUtils.sha256Hex(protocol.provider().getToken(port, protocol.getPlayerProfile()));
            hardware.setUserInfo(userId, hash);

            var hwids = protocol.hwids;
            if (hwids.has(String.valueOf(userId))) {
                hardware.loadFromObj(hwids.get(String.valueOf(userId)));
            } else {
                hwids.add(hardware.toObj());
                protocol.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEncryptMode() {
        return msgDecoder.decode(encryptMode);
    }

    public String getEncryptMode1() {
        return msgDecoder.decode(encryptMode1);
    }

    public String getEncryptKey() {
        return msgDecoder.decode(encryptionKey);
    }
}
