package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnionStmt<R> {
    private Iterable<R> previous;

    UnionStmt(Iterable<R> iterable) {
        previous = iterable;
    }

    public <T> FromStmt<T> from(Iterable<T> list) throws UnequalUnionClassesException {
        try {
            if (!list.iterator().hasNext()) return new FromStmt<>((Iterable<T>) previous);
            if (!previous.iterator().hasNext()) return new FromStmt<>(list);
            List<T> iterable = new ArrayList<>();
            Set<String> wasPresented = new HashSet<>();
            previous.forEach(el -> {
                if (!wasPresented.contains(((T) el).toString())) {
                    wasPresented.add(((T) el).toString());
                    iterable.add((T) el);
                }
            });
            list.forEach(el -> {
                if (!wasPresented.contains((el).toString())) {
                    wasPresented.add((el).toString());
                    iterable.add(el);
                }
            });
            return new FromStmt<>(iterable);
        } catch (ClassCastException ex) {
            throw new UnequalUnionClassesException("Different classes can't be union.", ex);
        }
    }
}
