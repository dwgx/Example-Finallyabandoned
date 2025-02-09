package com.example.mod.protocol.heypixel;

import com.example.mod.events.client.world.AddEntityEvent;
import com.example.mod.protocol.IProtocol;
import com.example.mod.protocol.NetPayload;
import com.example.mod.protocol.forge.ForgeProtocol;
import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.hwid.HeypixelHwids;
import com.example.mod.protocol.heypixel.networking.payload.UntypedPayload;
import com.example.mod.protocol.nel.GameSessionProvider;
import com.example.mod.utils.json.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;


public class HeypixelProtocol implements IProtocol {
    public static final String MOD_ID = "heypixel";

    /**
     * Channels
     */
    public static final Identifier S2C_CHANNEL = Identifier.of(MOD_ID, "s2cevent");
    public static final Identifier CHECK_CHANNEL = Identifier.of(MOD_ID, "check");
    public static final Identifier GAME_INFO_CHANNEL = Identifier.of(MOD_ID, "game_info");
    public static final Identifier REGISTER_CHANNEL = Identifier.of("minecraft", "register");
    public static final Gson GSON = GsonUtils.newBuilder().create();
    public static HeypixelProtocol sInstance;
    public ForgeProtocol forge = new ForgeProtocol(true);
    // current ms - server time
    // on s2c event
    public static long randomId = -1L;

    static {
        HeypixelPacket.init();
    }

    public final File HWID_FILE = new File(FOLDER, "hwid_data.json");
    public UUID clientId = UUID.randomUUID();
    public long runTime;
    public HeypixelHwids hwids = new HeypixelHwids();
    public HeypixelHandler manager;
    public boolean registersSent = false;
    public static HeypixelProtocol get() {
        if (sInstance == null) {
            sInstance = new HeypixelProtocol();
            sInstance.init();
        }
        return sInstance;
    }

    public void save() {
        try {
            String json = GSON.toJson(hwids);
            if (!HWID_FILE.exists()) HWID_FILE.createNewFile();
            Files.writeString(HWID_FILE.toPath(), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameSessionProvider provider() {
        return GameSessionProvider.get();
    }

    @Override
    public void init() {
        try {
            if (!FOLDER.exists()) FOLDER.mkdirs();

            this.hwids.hwids.clear();
            if (HWID_FILE.exists()) {
                HeypixelHwids hwids1 = GSON.fromJson(Files.readString(HWID_FILE.toPath(), StandardCharsets.UTF_8), HeypixelHwids.class);
                this.hwids.hwids.addAll(hwids1.hwids);
            } else save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sInstance = this;
        runTime = System.currentTimeMillis() - 15000;
        manager = new HeypixelHandler(this);

        NetPayload.register(S2C_CHANNEL);
        NetPayload.register(CHECK_CHANNEL);
        NetPayload.register(GAME_INFO_CHANNEL);
        NetPayload.register(REGISTER_CHANNEL);
    }

    public String getPlayerUUID() {
        return MinecraftClient.getInstance().player.getUuidAsString();
    }

    public String getPlayerUUIDA(int port) {
        var profile = getPlayerProfile();

        if (profile != null && profile.getId() != null) {
            return String.valueOf(profile.getId());
        }

        return provider().getPlayerUUID(port);
    }

    @Override
    public void reload() {
        clientId = UUID.randomUUID();
        runTime = System.currentTimeMillis() - 15000;
        manager = new HeypixelHandler(this);
    }

    @Override
    public String name() {
        return "Heypixel";
    }

    @Override
    public boolean onPacket(Packet packet) {
        try {
            if (packet instanceof CustomPayloadS2CPacket p && p.payload() instanceof NetPayload.PayloadPacket payload) {
                // FORGE CHANNEL INDEX
                var buf = payload.buf;
                var index = buf.readUnsignedByte();
                switch (index) {
                    case 233 -> { // S2C DEFAULT
                        JsonObject asJsonObject = JsonParser.parseString(buf.toString(StandardCharsets.UTF_8)).getAsJsonObject();
                        if (asJsonObject.has("plugin") && asJsonObject.has("event") && asJsonObject.has("data")) {
                            var s2c = new S2CEvent(asJsonObject.get("plugin").getAsString(), asJsonObject.get("event").getAsString(), asJsonObject.getAsJsonObject("data"));
                            manager.onServerEvent(s2c);
                        }

                    }
                    case 250 -> { // HEYPIXEL SESSION
                        try {
                            registersSent = false;

                            var pkt = manager.decode(buf);
                            if (pkt != null) manager.process(pkt);
                        } catch (Throwable t) {
                            //t.printStackTrace();
                        }
                    }
                }

                return true;
            }


            if (packet instanceof CustomPayloadC2SPacket p && !(p.payload() instanceof NetPayload.PayloadPacket)) {
                if (p.payload() instanceof BrandCustomPayload brand && !registersSent) {
//                var buf = new PacketByteBuf(Unpooled.buffer());
//
//                var bytes = new byte[] {
//                    -25, 1, 104, 101, 121, 112, 105, 120, 101, 108, 58, 97, 114, 109, 111, 117, 114, 101, 114, 115, 95, 119, 111, 114, 107, 115, 104, 111, 112, 32, 102, 109, 108, 58, 108, 111, 103, 105, 110, 119, 114, 97, 112, 112, 101, 114, 32, 102, 111, 114, 103, 101, 58, 116, 105, 101, 114, 95, 115, 111, 114, 116, 105, 110, 103, 32, 115, 116, 111, 114, 101, 109, 111, 100, 58, 98, 117, 121, 32, 104, 101, 121, 112, 105, 120, 101, 108, 58, 115, 50, 99, 101, 118, 101, 110, 116, 32, 102, 109, 108, 58, 112, 108, 97, 121, 32, 102, 108, 111, 111, 100, 103, 97, 116, 101, 58, 110, 101, 116, 101, 97, 115, 101, 32, 97, 114, 109, 111, 117, 114, 101, 114, 115, 95, 119, 111, 114, 107, 115, 104, 111, 112, 58, 112, 108, 97, 121, 32, 102, 109, 108, 58, 104, 97, 110, 100, 115, 104, 97, 107, 101, 32, 103, 101, 99, 107, 111, 108, 105, 98, 51, 58, 109, 97, 105, 110, 32, 102, 111, 114, 103, 101, 58, 115, 112, 108, 105, 116, 32, 102, 108, 111, 111, 100, 103, 97, 116, 101, 58, 102, 111, 114, 109, 32, 110, 101, 116, 101, 97, 115, 101, 95, 111, 102, 102, 105, 99, 105, 97, 108, 58, 109, 97, 105, 110, 95, 99, 104, 97, 110, 110, 101, 108
//                };
//
//                buf.writeBytes(bytes);
//                NetPayload.send(REGISTER_CHANNEL, buf);
//                mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(brand));
//                return true;
                }

               //ChatUtils.display(Text.literal(p.payload().getClass().getName() + " " + p.payload().getId().id().toString()));
            if (p.payload() instanceof UntypedPayload) {
                if (p.payload().getId().id().equals(REGISTER_CHANNEL)) {
                    registersSent = false;

                    var buf = new PacketByteBuf(Unpooled.buffer());

                    var registries = new String[] {
                            "heypixel:armourers_workshop",
                            "fml:loginwrapper",
                            "forge:tier_sorting",
                            "storemod:buy",
                            "heypixel:s2cevent",
                            "fml:play",
                            "floodgate:netease",
                            "armourers_workshop:play",
                            "fml:handshake",
                            "geckolib3:main",
                            "forge:split",
                            "floodgate:form",
                            "netease_official:main_channel"
                    };

                    boolean first = true;

                    for (var channel : registries) {
                        if (first) {
                            first = false;
                        } else {
                            buf.writeByte(0);
                        }

                        buf.writeBytes(channel.getBytes(StandardCharsets.US_ASCII));
                    }

                    NetPayload.send(REGISTER_CHANNEL, buf);

                    return true;
                }
            }

            }

            forge.onPacket(packet);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onMessage(Text text) {
    }

    @Override
    public void onAddEntity(AddEntityEvent e) {
        manager.onAddEntity(e.getWorld(),e.getEntity());
    }


    @Override
    public void tick() {
        manager.onClientTick();
    }
}
