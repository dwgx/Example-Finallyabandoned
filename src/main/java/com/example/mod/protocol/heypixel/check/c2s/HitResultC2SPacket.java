package com.example.mod.protocol.heypixel.check.c2s;

import com.example.mod.protocol.heypixel.check.HeypixelPacket;
import com.example.mod.protocol.heypixel.utils.BufferHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.GameMode;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.value.Variable;

import java.io.IOException;
import java.util.List;


@Deprecated
public class HitResultC2SPacket extends HeypixelPacket {
    public static long delay;
    public Hand hand;
    public BlockHitResult hitResult;

    public ClientPlayerEntity player;

    public HitResultC2SPacket(ClientPlayerEntity localPlayer, Hand interactionHand, BlockHitResult blockHitResult) {
        this.player = localPlayer;
        this.hand = interactionHand;
        this.hitResult = blockHitResult;
    }

    public static void check(ClientPlayerEntity localPlayer, Hand clientLevel, Hand interactionHand, BlockHitResult blockHitResult, GameMode gameType) {
        var minecraft = MinecraftClient.getInstance();
        var cameraEntity = minecraft.getCameraEntity();
        if (System.currentTimeMillis() < delay || cameraEntity == null || minecraft.world == null || gameType.equals(GameMode.SPECTATOR) || blockHitResult == null) {
            return;
        }
        delay = System.currentTimeMillis() + 10;
        new HitResultC2SPacket(localPlayer, interactionHand, blockHitResult).sendVanilla();
    }

    @Override
    public void encode(PacketByteBuf buf, BufferHelper bufferHelper) {
        bufferHelper.writeVec3(buf, this.player.getPos());
        buf.writeEnumConstant(this.hand);
        bufferHelper.writeBlockHitResult(buf, this.player, this.hitResult);
    }

    @Override
    public void encode(MessageBufferPacker packer) {
        try {
            packer.packValue(new Variable().setArrayValue(
                    List.of(
                            new Variable().setFloatValue(this.player.getPos().x),
                            new Variable().setFloatValue(this.player.getPos().y),
                            new Variable().setFloatValue(this.player.getPos().z)
                    )));
            packer.packInt(this.hitResult.getSide().ordinal());
            packer.packInt(this.hitResult.getType().ordinal());
            packer.packFloat((float) (this.hitResult.getPos().x - this.hitResult.getBlockPos().getX()));
            packer.packFloat((float) (this.hitResult.getPos().y - this.hitResult.getBlockPos().getY()));
            packer.packFloat((float) (this.hitResult.getPos().z - this.hitResult.getBlockPos().getZ()));
            packer.packValue(new Variable().setFloatValue(MinecraftClient.getInstance().player.getPitch()));
            packer.packValue(new Variable().setFloatValue(MinecraftClient.getInstance().player.getYaw()));
        } catch (IOException e) {
        }
    }
}
