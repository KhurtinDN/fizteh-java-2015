package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.util.HashSet;
import java.util.Set;

class FinalRow<T, R> {
    private R row;
    private Set<T> from;

    FinalRow(R newRow, T elementFrom) {
        from = new HashSet<T>();
        from.add(elementFrom);
        row = newRow;
    }

    public R get() {
        return row;
    }

    public Set<T> getFrom() {
        return from;
    }

    public T getAnyFrom() {
        return from.iterator().next();
    }

    public void updateRow(R newRow) {
        row = newRow;
    }
}
