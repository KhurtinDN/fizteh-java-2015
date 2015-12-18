package ru.mipt.diht.students.maxdankow.sqlcollections.statements;

import java.util.Comparator;

public class ItemsComparator<T> implements Comparator<T> {
    private Comparator<T>[] comparators;

    @SafeVarargs
    public ItemsComparator(Comparator<T>... newComparators) {
        comparators = newComparators;
    }

    @Override
    public final int compare(T first, T second) {
        for (Comparator<T> comparator : comparators) {
            // Сравниваем до первого несовпадения.
            if (comparator.compare(first, second) != 0) {
                return comparator.compare(first, second);
            }
        }
        return 0;
    }
}
