package com.example.mod.storage;

import com.example.mod.events.network.PacketEvent;
import com.example.mod.utils.NetworkUtils;
import com.example.utils.AbstractCallbackImpl;
import com.example.utils.pattern.Singleton;
import net.engio.mbassy.listener.Handler;
import net.minecraft.network.packet.Packet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PacketStorage {
    private final Deque<Packet<?>> packetDeque = new ConcurrentLinkedDeque<>();

    private final List<Class<?>> packetFilters = new ArrayList<>();
    private final List<Class<?>> packetExempts = new ArrayList<>();

    private final Map<Class<?>, Integer> packetStats = new HashMap<>();

    private AbstractCallbackImpl<Packet<?>> releaseCallback;

    private boolean storing, releasing, cancel;

    @Handler(priority = 1337)
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (this.storing && !this.releasing) {
            if (this.packetFilters.isEmpty() || (this.packetFilters.contains(packet.getClass()) && !this.packetExempts.contains(packet.getClass()))) {
                this.packetDeque.add(packet);

                this.packetStats.put(packet.getClass(), this.packetStats.getOrDefault(packet.getClass(), 0) + 1);

                if (this.cancel) {
                    event.cancel();
                }
            }
        }
    }

    public boolean storage(boolean cancel) {
        if (!this.storing) {
            this.storing = true;
            this.releasing = false;
            this.cancel = cancel;

            return true;
        }

        return false;
    }

    public boolean stop(boolean release, boolean reverse) {
        if (!this.storing) {
            return false;
        }

        boolean result = true;

        if (release) {
            this.releasing = true;
            result = this.release(this.getPacketCount(), reverse);
        }

        this.storing = false;
        this.releasing = false;
        this.releaseCallback = null;

        return result;
    }

    public boolean releaseAll(boolean reverse) {
        return this.release(this.getPacketCount(), reverse);
    }

    public boolean release(int count, boolean reverse) {
        if (!this.storing || this.packetDeque.isEmpty() || this.packetDeque.size() < count) {
            if (this.releaseCallback != null) {
                this.releaseCallback.onFailure(null, null);
            }
            return false;
        }

        for (int i = 0; i < count; i++) {
            Packet<?> packet = reverse ? this.packetDeque.removeLast() : this.packetDeque.removeFirst();
            NetworkUtils.sendPacketSilently(packet);

            this.packetStats.compute(packet.getClass(), (key, currentCount) -> currentCount == null || currentCount == 0 ? null : currentCount - 1);

            if (this.releaseCallback != null) {
                this.releaseCallback.onExecute(packet);

                if (this.packetDeque.isEmpty()) {
                    this.releaseCallback.onComplete(packet);
                }
            }
        }

        return true;
    }

    public boolean release(Predicate<Packet<?>> predicate, int count, boolean reverse) {
        if (!this.storing || this.packetDeque.isEmpty()) {
            if (this.releaseCallback != null) {
                this.releaseCallback.onFailure(null, null);
            }
            return false;
        }

        List<Packet<?>> packets = this.packetDeque.stream()
                .filter(predicate)
                .limit(count)
                .collect(Collectors.toList());

        if (packets.isEmpty()) {
            return false;
        }

        this.packetDeque.removeAll(packets);

        if (reverse) {
            packets = packets.reversed();
        }

        //  && i < count
        for (int i = 0; i < packets.size(); i++) {
            Packet<?> packet = packets.removeFirst();
            NetworkUtils.sendPacketSilently(packet);

            this.packetStats.compute(packet.getClass(), (key, currentCount) -> currentCount == null || currentCount == 0 ? null : currentCount - 1);

            if (this.releaseCallback != null) {
                this.releaseCallback.onExecute(packet);

                if (this.packetDeque.isEmpty()) {
                    this.releaseCallback.onComplete(packet);
                }
            }
        }

        return true;
    }

    public void filter(Class<?>... filters) {
        this.packetFilters.clear();
        this.packetFilters.addAll(List.of(filters));
    }

    public void exempt(Class<?>... exempts) {
        this.packetExempts.clear();
        this.packetExempts.addAll(List.of(exempts));
    }

    public void releaseCallback(AbstractCallbackImpl<Packet<?>> callback) {
        this.releaseCallback = callback;
    }

    public int getPacketCount() {
        return packetDeque.size();
    }

    public int getPacketCountByClass(Class<?> packetClass) {
        return this.packetStats.getOrDefault(packetClass, 0);
    }

    public List<Integer> findPacketIndexes(Predicate<Packet<?>> predicate) {
        List<Integer> indexes = new ArrayList<>();

        int index = 0;
        for (Packet<?> packet : this.packetDeque) {
            if (predicate.test(packet)) {
                indexes.add(index);
            }
            index++;
        }

        return indexes;
    }

    public Packet<?> getPacketByIndex(int index) {
        if (index < 0 || index >= packetDeque.size()) {
            return null;
        }

        List<Packet<?>> packetList = new ArrayList<>(packetDeque);
        return packetList.get(index);
    }

    public Deque<Packet<?>> getPacketDeque() {
        return packetDeque;
    }

    public List<Class<?>> getPacketFilters() {
        return packetFilters;
    }

    public List<Class<?>> getPacketExempts() {
        return packetExempts;
    }

    public boolean isStoring() {
        return storing;
    }

    public boolean isCancel() {
        return cancel;
    }

    public static PacketStorage getInstance() {
        return Singleton.getInstance(PacketStorage.class);
    }
}
