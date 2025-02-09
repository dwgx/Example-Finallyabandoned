package com.example.utils.unsafe;

import java.lang.ref.Cleaner;

public class MemAlloc {
    private static final Cleaner CLEANER = Cleaner.create();
    private long address;
    private final long size;

    private final Cleaner.Cleanable cleanable;

    public MemAlloc(long size) {
        this.size = size;
        this.address = UnsafeUtils.allocateMemory(size);
        this.cleanable = CLEANER.register(this, new MemoryCleanup(address));
    }

    public static MemAlloc calloc(long num, long size) {
        return new MemAlloc(num * size);
    }

    public long getSize() {
        return size;
    }

    public long getAddress() {
        return address;
    }

    public byte readByte() {
        return readByte(0);
    }

    public int readInt() {
        return readInt(0);
    }

    public long readLong() {
        return readLong(0);
    }

    public byte readByte(long offset) {
        checkBounds(offset, 1);
        return UnsafeUtils.getByte(address + offset);
    }

    public int readInt(long offset) {
        checkBounds(offset, Integer.BYTES);
        return UnsafeUtils.getInt(address + offset);
    }

    public long readLong(long offset) {
        checkBounds(offset, Long.BYTES);
        return UnsafeUtils.getLong(address + offset);
    }

    public void writeByte(byte value) {
        writeByte(0, value);
    }

    public void writeInt(int value) {
        writeInt(0, value);
    }

    public void writeLong(long value) {
        writeLong(0, value);
    }

    public void writeByte(long offset, byte value) {
        checkBounds(offset, 1);
        UnsafeUtils.putByte(address + offset, value);
    }

    public void writeInt(long offset, int value) {
        checkBounds(offset, Integer.BYTES);
        UnsafeUtils.putInt(address + offset, value);
    }

    public void writeLong(long offset, long value) {
        checkBounds(offset, Long.BYTES);
        UnsafeUtils.putLong(address + offset, value);
    }

    public void free() {
        if (address != 0) {
            UnsafeUtils.freeMemory(address);
            address = 0;
        }
    }

    private void checkBounds(long offset, long length) {
        if (offset < 0 || offset + length > size) {
            throw new IndexOutOfBoundsException("Memory access out of bounds");
        }
    }

    public Cleaner.Cleanable getCleanable() {
        return cleanable;
    }

    private record MemoryCleanup(long address) implements Runnable {
        @Override
            public void run() {
                if (address != 0) {
                    UnsafeUtils.freeMemory(address);
                }
            }
        }
}
