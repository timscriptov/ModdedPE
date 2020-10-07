package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MultiSelection<T> {
    private HashSet<T> selection = new HashSet<>();

    public void add(T object) {
        selection.add(object);
    }

    public void remove(T object) {
        selection.remove(object);
    }

    public boolean contains(T object) {
        return selection.contains(object);
    }

    public boolean isEmpty() {
        return selection.isEmpty();
    }

    public ArrayList<T> toArrayList() {
        return new ArrayList<>(selection);
    }

    public void reset() {
        selection.clear();
    }

    public int size() {
        return selection.size();
    }
}
