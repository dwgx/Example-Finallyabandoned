package com.example.mod.protocol.heypixel.check.s2c;

import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.check.c2s.EncryptionCheckC2SPacket;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.nio.charset.StandardCharsets;


//@StringEncryption
//@ControlFlowObfuscation
public class TokenDataS2CPacket extends HeypixelPacket {
    public byte[] ketC;
    public byte[] keyB;
    public byte[] keyA;

    public TokenDataS2CPacket(PacketByteBuf buf) {
        this.keyA = helper.readByteArray(buf);
        this.keyB = helper.readByteArray(buf);
        this.ketC = helper.readByteArray(buf);
    }

    @Override
    public void handle(ClientPlayerEntity player) {
        var r0 = new String(this.keyA, StandardCharsets.UTF_8);
        var r1 = new String(this.keyB, StandardCharsets.UTF_8);
        var r2 = new String(this.ketC, StandardCharsets.UTF_8);

        var s1 = new String(decodeBase64(r0), StandardCharsets.UTF_8);
        var s2 = new String(decodeBase64(r1), StandardCharsets.UTF_8);
        var s3 = new String(decodeBase64(r2), StandardCharsets.UTF_8);

        var splitStr = " ";
        for (char c : s1.toCharArray()) {
            try {
                Integer.valueOf(c + "");
            } catch (NumberFormatException e) {
                splitStr = String.valueOf(c);
                break;
            }
        }

        String[] split = s1.split(splitStr);
        String[] split2 = s2.split(splitStr);
        String[] split3 = s3.split(splitStr);

        var decoder = manager.msgDecoder;
        manager.tokenMap.put(decoder.decode(split[0]), decoder.decode(split[1]));
        manager.tokenMap.put(decoder.decode(split2[0]), decoder.decode(split2[1]));
        manager.tokenMap.put(decoder.decode(split3[0]), decoder.decode(split3[1]));
        new EncryptionCheckC2SPacket(getPacketId()).set(manager).sendVanilla();
    }

    private byte[] decodeBase64(String s) {
        return java.util.Base64.getDecoder().decode(s);
    }
}
