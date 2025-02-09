package com.example.value;

/**
 * 继承自 BasicValue<T>，增加了 min / max / increment，用于存储数字类型
 */
public class NumberValue<T extends Number> extends BasicValue<T> {
    protected final T min;
    protected final T max;
    protected final T increment;

    public NumberValue(String name, String description, T defaultValue, T min, T max, T increment) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public NumberValue(String name, T defaultValue, T min, T max, T increment) {
        this(name, null, defaultValue, min, max, increment);
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public T getIncrement() {
        return increment;
    }

    @Override
    public void setValue(T value) {
        double dv = value.doubleValue();
        if (dv < min.doubleValue() || dv > max.doubleValue()) {
            throw new IllegalArgumentException("Value must be between " + min + " and " + max);
        }
        super.setValue(value);
    }
}
