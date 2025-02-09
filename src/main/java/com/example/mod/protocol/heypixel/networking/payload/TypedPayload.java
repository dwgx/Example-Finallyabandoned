
package com.example.mod.protocol.heypixel.networking.payload;

import com.example.mod.protocol.heypixel.networking.v1.FabricPacket;
import com.example.mod.protocol.heypixel.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record TypedPayload(FabricPacket packet) implements ResolvedPayload {
	@Override
	public ResolvedPayload resolve(@Nullable PacketType<?> type) {
		if (type == null) {
			PacketByteBuf buf = PacketByteBufs.create();
			write(buf);
			return new UntypedPayload(packet.getType().getId(), buf);
		} else {
			return this;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		packet.write(buf);
	}

	@Override
	public Identifier id() {
		return packet.getType().getId();
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return null;
	}
}
