package ru.mipt.diht.students.simon23rus.CQL.impl;

import java.util.Comparator;

public class BestComparatorEver<R> implements Comparator<R> {
    Comparator<R>[] currentComparators;

    BestComparatorEver(Comparator<R>... givenComparators) {
        currentComparators = givenComparators;
    }

    @Override
    public int compare(R first, R second) {
        for(Comparator<R> thisComparator : currentComparators) {
            int comparisonResult = thisComparator.compare(first, second);
            if(comparisonResult != 0) {
                return comparisonResult;
            }
        }

        return 0;
    }

}
