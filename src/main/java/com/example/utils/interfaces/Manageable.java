package com.example.utils.interfaces;

import java.util.List;

public interface Manageable<T> {
    boolean add(T element);

    boolean remove(T element);

    List<T> items();
}