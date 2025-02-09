package com.example.value;

public class RangeNumberValue<T extends Number> extends NumberValue<T> {
    private T secondValue;

    public RangeNumberValue(String name, String description,
                            T defaultValue, T secondDefaultValue,
                            T min, T max, T increment) {
        super(name, description, defaultValue, min, max, increment);
        this.secondValue = secondDefaultValue;
    }

    public RangeNumberValue(String name,
                            T defaultValue, T secondDefaultValue,
                            T min, T max, T increment) {
        this(name, null, defaultValue, secondDefaultValue, min, max, increment);
    }

    public T getSecondValue() {
        return secondValue;
    }

    @SuppressWarnings("unchecked")
    public void setSecondValue(Object value) {
        // 手动类型检查
        if (!secondValue.getClass().isInstance(value)) {
            throw new IllegalArgumentException("Invalid type for secondValue. Expected "
                    + secondValue.getClass() + ", got " + value.getClass());
        }
        T val = (T) value;
        double dv = val.doubleValue();
        if (dv < getMin().doubleValue() || dv > getMax().doubleValue()) {
            throw new IllegalArgumentException("Second value out of range");
        }
        this.secondValue = val;
    }
}