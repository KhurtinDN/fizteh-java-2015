package ru.mipt.diht.students.ale3otik.collectionquery.impl;

import java.util.Comparator;

/**
 * Created by alex on 17.12.15.
 */
public class GeneralComparator<T> implements Comparator<T> {
    private Comparator<T>[] varietyOfComparators;

    public GeneralComparator(Comparator<T>... comparators) {
        this.varietyOfComparators = comparators;
    }

    @Override
    public final int compare(T first, T second) {
        for (Comparator<T> cmp : varietyOfComparators) {
            int result = cmp.compare(first, second);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
