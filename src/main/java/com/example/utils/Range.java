package com.example.utils;

public class Range {
    private final int start;
    private final int end;

    public Range(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Start value must be less than or equal to end value");
        }
        this.start = start;
        this.end = end;
    }

    public Range(char start, char end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Range union(Range other) {
        if (this.end >= other.start - 1 && other.end >= this.start - 1) {
            int newStart = Math.min(this.start, other.start);
            int newEnd = Math.max(this.end, other.end);
            return new Range(newStart, newEnd);
        }
        return null;
    }

    public boolean contains(char c) {
        return c >= start && c <= end;
    }

    public boolean contains(int var1) {
        return var1 >= start && var1 <= end;
    }
}
