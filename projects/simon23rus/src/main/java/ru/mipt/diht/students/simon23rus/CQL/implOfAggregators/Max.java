package ru.mipt.diht.students.simon23rus.CQL.implOfAggregators;

import java.util.List;
import java.util.function.Function;


public class Max<T, R extends Comparable<R>> implements Aggregator<T, R> {
    private Function<T, R> thisFunction;

    public Max(Function<T, R> givenFunction) {
        this.thisFunction = givenFunction;
    }

    @Override
    public R apply(List<T> elements) {
        if(elements.size() == 0) {
            return null;
        }
        else {
            R ourMax = thisFunction.apply(elements.get(0));
            for(T elem : elements) {
                if (ourMax.compareTo(thisFunction.apply(elem)) < 0) {
                    ourMax = thisFunction.apply(elem);
                }
            }
            return ourMax;
        }
    }

    @Override
    public R apply(T elem) {
        return thisFunction.apply(elem);
    }
}
