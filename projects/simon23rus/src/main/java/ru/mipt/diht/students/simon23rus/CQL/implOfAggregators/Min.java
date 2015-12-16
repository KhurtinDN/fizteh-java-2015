package ru.mipt.diht.students.simon23rus.CQL.implOfAggregators;

import java.util.List;
import java.util.function.Function;

public class Min<T, R extends Comparable<R>> implements Aggregator<T, R> {
    private Function<T, R> thisFunction;

    public Min(Function<T, R> givenFunction) {
        this.thisFunction = givenFunction;
    }

    @Override
    public R apply(List<T> elements) {
        if(elements.size() == 0) {
            return null;
        }
        else {
            R ourMin = thisFunction.apply(elements.get(0));
            for(T elem : elements) {
                if (ourMin.compareTo(thisFunction.apply(elem)) > 0) {
                    ourMin = thisFunction.apply(elem);
                }
            }
            return ourMin;
        }
    }


//    //pereopredelyayu t,k, compiler trebuet pereopredelit' abstraktniy method
    @Override
    public R apply(T elem) {
        return thisFunction.apply(elem);
    }
}

