package ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by alex on 17.12.15.
 */
public class Min<T, R extends Comparable<R>> implements Aggregator<T, R> {
    private Function<T, R> function;

    public Min(Function<T, R> rcvFunction) {
        this.function = rcvFunction;
    }

    @Override
    public final R apply(List<T> elements) {
        if (elements.size() == 0) {
            return null;
        }
        R curMin = null;

        for (T elem : elements) {
            if (elem == null || function.apply(elem) == null) {
                continue;
            }
            if (curMin == null) {
                curMin = function.apply(elem);
            }
            if (curMin.compareTo(function.apply(elem)) > 0) {
                curMin = function.apply(elem);
            }

        }
        return curMin;
    }

    @Override
    public final R apply(T elem) {
        return function.apply(elem);
    }
}

