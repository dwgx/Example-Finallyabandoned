package com.example.mod.events.network;

import com.example.event.Event;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event.Cancellable {
    public enum Direction {
        SEND,
        RECEIVE
    }

    private Direction direction;
    private Packet<?> packet;
    private ClientConnection connection;

    public PacketEvent(Direction direction, Packet<?> packet, ClientConnection connection) {
        this.direction = direction;
        this.packet = packet;
        this.connection = connection;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }
}
