package com.example.mod.protocol.heypixel.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public class BufferHelper {

    public static int readVarInt(ByteBuf byteBuf, BufferHelper bufferHelper) {
        return HeypixelVarUtils.readVarInt(byteBuf);
    }

    public <T> void writeCollection(ByteBuf byteBuf, Collection<T> collection, ToLongFunction<ByteBuf> toLongFunction, BiFunction<ByteBuf, BufferHelper, T> biFunction) {
        long applyAsLong = toLongFunction.applyAsLong(byteBuf);
        for (int i = 0; i < applyAsLong; i++) {
            collection.add(biFunction.apply(byteBuf, this));
        }
    }

    public <T> void readCollection(ByteBuf byteBuf, Collection<T> collection, Function<ByteBuf, T> function) {
        int readVarInt = HeypixelVarUtils.readVarInt(byteBuf);
        for (int i = 0; i < readVarInt; i++) {
            collection.add(function.apply(byteBuf));
        }
    }

    public void writeVector3i(ByteBuf byteBuf, Vector3i vector3i) {
        HeypixelVarUtils.writeVarInt(byteBuf, vector3i.x);
        HeypixelVarUtils.writeVarInt(byteBuf, vector3i.y);
        HeypixelVarUtils.writeVarInt(byteBuf, vector3i.z);
    }

    public ByteBuf readRetainedSlice(ByteBuf byteBuf) {
        return byteBuf.readRetainedSlice(HeypixelVarUtils.readVarInt(byteBuf));
    }

    public <T> void readCustomCollection(ByteBuf byteBuf, Collection<T> collection, BiFunction<ByteBuf, BufferHelper, T> biFunction) {
        writeCollection(byteBuf, collection, HeypixelVarUtils::readVarInt, biFunction);
    }

    public void writeVector2(ByteBuf byteBuf, Vector2f vector2f) {
        byteBuf.writeFloatLE(vector2f.x());
        byteBuf.writeFloatLE(vector2f.y());
    }

    public Object[] toArray(ByteBuf byteBuf, Object[] objArr, BiFunction<ByteBuf, BufferHelper, Object> biFunction) {
        ObjectArrayList<Object> objectArrayList = new ObjectArrayList<>();
        readCustomCollection(byteBuf, objectArrayList, biFunction);
        return objectArrayList.toArray(objArr);
    }

    public void writeByteArray(ByteBuf byteBuf, byte[] bArr) {
        HeypixelVarUtils.writeUnsignedInt(byteBuf, bArr.length + 1);
        byteBuf.writeBytes(bArr);
    }

    public void writeUUID(ByteBuf byteBuf, UUID uuid) {
        byteBuf.writeLongLE(uuid.getMostSignificantBits());
        byteBuf.writeLongLE(uuid.getLeastSignificantBits());
    }

    public Object[] readObjectArray(ByteBuf byteBuf, Object[] objArr, Function<ByteBuf, Object> function) {
        ObjectArrayList<Object> objectArrayList = new ObjectArrayList<>();
        readCollection(byteBuf, objectArrayList, function);
        return objectArrayList.toArray(objArr);
    }

    public void writeVec3(ByteBuf byteBuf, Vec3d vec3) {
        byteBuf.writeDoubleLE(vec3.x);
        byteBuf.writeDoubleLE(vec3.y);
        byteBuf.writeDoubleLE(vec3.z);
    }

    public Vector3f readVec3f(ByteBuf byteBuf) {
        return new Vector3f(byteBuf.readFloatLE(), byteBuf.readFloatLE(), byteBuf.readFloatLE());
    }

    public void writeBlockHitResult(PacketByteBuf buf, ClientPlayerEntity player, BlockHitResult result) {
        var blockPos = result.getBlockPos();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        var location = result.getPos();
        var eyePosition = player.getEyePos();
        writeVector3i(buf, new Vector3i(x, y, z));
        HeypixelVarUtils.writeVarInt(buf, result.getSide().ordinal());
        HeypixelVarUtils.writeVarInt(buf, result.getType().ordinal());
        buf.writeFloat((float) (location.x - blockPos.getX()));
        buf.writeFloat((float) (location.y - blockPos.getY()));
        buf.writeFloat((float) (location.z - blockPos.getZ()));
        buf.writeBoolean(result.isInsideBlock());
        buf.writeDouble(eyePosition.x);
        buf.writeDouble(eyePosition.y);
        buf.writeDouble(eyePosition.z);
        buf.writeFloat(player.getYaw());
        buf.writeFloat(player.getPitch());
    }

    public byte[] readByteArray(ByteBuf byteBuf) {
        int readVarInt = HeypixelVarUtils.readVarInt(byteBuf);
        byte[] bArr = new byte[readVarInt];
        byteBuf.readBytes(bArr);
        return bArr;
    }

    public byte[] readByteArrayHeypixelClient(ByteBuf byteBuf) {
        int readVarInt = HeypixelVarUtils.readVarInt(byteBuf);
        byte[] bArr = new byte[readVarInt - 1];
        byteBuf.readBytes(bArr);
        return bArr;
    }

    public void writeString(ByteBuf byteBuf, String str) {
        HeypixelVarUtils.writeUnsignedInt(byteBuf, ByteBufUtil.utf8Bytes(str));
        byteBuf.writeCharSequence(str, StandardCharsets.UTF_8);
    }

    public void writeVec2(ByteBuf byteBuf, Vec2f vec2) {
        writeVector2(byteBuf, new Vector2f(vec2.x, vec2.y));
    }

    public List<Integer> readUnsignedByteList(ByteBuf byteBuf) {
        ObjectArrayList<Integer> objectArrayList = new ObjectArrayList<>();
        writeCollection(byteBuf, objectArrayList, ByteBuf::readUnsignedByte, BufferHelper::readVarInt);
        return objectArrayList;
    }

    public void writeEnum(ByteBuf byteBuf, Enum<?> r5) {
        HeypixelVarUtils.writeVarInt(byteBuf, r5.ordinal());
    }

    public void writeStringCollection(ByteBuf byteBuf, List<String> list) {
        writeCollectionWithBiConsumer(byteBuf, list, this::writeString);
    }

    public <T> void writeCollectionWithBiConsumer(ByteBuf byteBuf, Collection<T> collection, BiConsumer<ByteBuf, T> biConsumer) {
        HeypixelVarUtils.writeUnsignedInt(byteBuf, collection.size());
        for (T o : collection) {
            biConsumer.accept(byteBuf, o);
        }
    }
}
