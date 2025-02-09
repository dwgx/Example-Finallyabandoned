package com.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Stopwatch {
    private long startTime;
    private long endTime;
    private boolean running;
    private final List<Long> laps = new ArrayList<>();
    private Alarm alarm;

    public Stopwatch() {
        this.running = false;
    }

    public Stopwatch start() {
        if (!this.running) {
            this.startTime = System.nanoTime();
            this.running = true;
        }

        return this;
    }

    public Stopwatch stop() {
        if (this.running) {
            this.endTime = System.nanoTime();
            this.running = false;
        }

        return this;
    }

    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
        this.running = false;
    }

    public long getElapsedTime(TimeUnit unit) {
        long elapsedTime = this.running ? System.nanoTime() - this.startTime : this.endTime - this.startTime;
        return unit.convert(elapsedTime, TimeUnit.NANOSECONDS);
    }

    public String getFormattedElapsedTime() {
        long elapsedTime = this.getElapsedTime(TimeUnit.MILLISECONDS);
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
        long millis = elapsedTime % 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    public void lap() {
        if (this.running) {
            this.laps.add(this.getElapsedTime(TimeUnit.MILLISECONDS));
        }
    }

    public List<Long> getLaps() {
        return List.copyOf(this.laps);
    }

    public boolean hasElapsed(long duration) {
        return this.hasElapsed(duration, TimeUnit.MILLISECONDS);
    }

    public boolean hasElapsed(long duration, TimeUnit unit) {
        return duration != -1 && this.getElapsedTime(unit) >= duration;
    }

    public void restartIfElapsed(long duration, TimeUnit unit) {
        if (this.hasElapsed(duration, unit)) {
            this.reset();
            this.start();
        }
    }

    public void setAlarm(long time, TimeUnit unit, Consumer<Stopwatch> listener) {
        this.alarm = new Alarm(time, unit, listener);
    }

    public void checkAlarm() {
        if (this.alarm != null && this.alarm.check(this)) {
            this.reset();
        }
    }

    private long getRawElapsedTime() {
        return this.running ? System.nanoTime() - this.startTime : this.endTime - this.startTime;
    }

    public boolean isRunning() {
        return this.running;
    }

    public static Stopwatch create() {
        return new Stopwatch();
    }

    public static Stopwatch createStarted() {
        return new Stopwatch().start();
    }

    private static class Alarm {
        private final long time;
        private final TimeUnit unit;
        private final Consumer<Stopwatch> listener;

        public Alarm(long time, TimeUnit unit, Consumer<Stopwatch> listener) {
            this.time = time;
            this.unit = unit;
            this.listener = listener;
        }

        public boolean check(Stopwatch stopwatch) {
            if (stopwatch.hasElapsed(time, unit)) {
                listener.accept(stopwatch);
                return true;
            }
            return false;
        }
    }
}
