package ru.mipt.diht.students.annnvl.CQL.impl;

import java.util.List;
import java.util.function.Function;

public class Min<T, R extends Comparable<R>> implements Aggregator<T, R> {

    private Function<T, R> function;
    public Min(Function<T, R> expression) {
        this.function = expression;
    }

    @Override
    public R apply(List<T> elements) {
        return elements
                .stream()
                .map(function)
                
                .reduce(null, (a, b) -> {
                    if (a == null) {
                        return b;
                    }
                    if (b == null) {
                        return a;
                    }
                    if (a.compareTo(b) < 0) {
                        return a;
                    } else {
                        return b;
                    }
                });
    }

    @Override
    public R apply(T t) {
        return null;
    }
}
