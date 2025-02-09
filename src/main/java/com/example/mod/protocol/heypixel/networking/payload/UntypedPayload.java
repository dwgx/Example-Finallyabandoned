//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.mod.protocol.heypixel.networking.payload;

import com.example.mod.protocol.heypixel.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record UntypedPayload(Identifier id, PacketByteBuf buffer) implements ResolvedPayload {
    public UntypedPayload(Identifier id, PacketByteBuf buffer) {
        this.id = id;
        this.buffer = buffer;
    }

    public ResolvedPayload resolve(@Nullable PacketType<?> type) {
        if (type == null) {
            return this;
        } else {
            PacketByteBuf copy = PacketByteBufs.copy(this.buffer);
            TypedPayload typed = new TypedPayload(type.read(copy));
            int dangling = copy.readableBytes();
            if (dangling > 0) {
                throw new IllegalStateException("Found " + dangling + " extra bytes when reading packet " + String.valueOf(this.id));
            } else {
                return typed;
            }
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeBytes(this.buffer.copy());
    }

    public PacketByteBuf buffer() {
        return PacketByteBufs.copy(this.buffer);
    }

    public Identifier id() {
        return this.id;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return null;
    }
}
