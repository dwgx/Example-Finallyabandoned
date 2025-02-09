package com.example.utils.filter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Filter<T> {
    private final List<Predicate<T>> conditions = new ArrayList<>();
    private int limit = -1;
    private Comparator<T> priorityComparator = (a, b) -> 0;

    public Filter<T> condition(Predicate<T> condition) {
        conditions.add(condition);
        return this;
    }

    public Filter<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Filter<T> priorityComparator(Comparator<T> comparator) {
        this.priorityComparator = comparator;
        return this;
    }

    public List<T> filter(List<T> candidates) {
        return candidates.stream()
                .filter(item -> conditions.stream().allMatch(c -> c.test(item)))
                .limit(limit != -1 ? limit : Long.MAX_VALUE)
                .sorted(priorityComparator)
                .collect(Collectors.toList());
    }
}
