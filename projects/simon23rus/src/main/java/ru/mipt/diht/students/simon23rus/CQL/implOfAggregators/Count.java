package ru.mipt.diht.students.simon23rus.CQL.implOfAggregators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


public class Count<T> implements Aggregator<T, Long> {
    private Function<T, ?> thisFunction;
    public Count(Function<T, ?> givenFunction) {
        this.thisFunction = givenFunction;
    }

    @Override
    public Long apply(List<T> givenElements) {
        Set<Object> differentElements = new HashSet<>();
        for(T elem : givenElements) {
            System.out.println(thisFunction.apply(elem) + "wqe");
            if(thisFunction.apply(elem) != null && !differentElements.contains(thisFunction.apply(elem))) {
                differentElements.add(thisFunction.apply(elem));
            }
        }
        System.out.println(differentElements);
        return ((long) differentElements.size());
    }


    @Override
    public Long apply(T elem) {
        if(thisFunction.apply(elem) != null) {
            return 1L;
        }
        return 0L;
    }
}
