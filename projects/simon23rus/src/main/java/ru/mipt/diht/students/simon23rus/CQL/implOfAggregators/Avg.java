package ru.mipt.diht.students.simon23rus.CQL.implOfAggregators;

import java.util.List;
import java.util.function.Function;


public class Avg<T> implements Aggregator<T, Double> {

    private Function<T, ? extends Number> thisFunction;

    public Avg(Function<T, ? extends Number> givenFunction) {
        this.thisFunction = givenFunction;
    }

    @Override
    public Double apply(List<T> givenElements) {
        Double result = 0.0;
        for (T elem : givenElements) {
            result += ((Double) thisFunction.apply(elem));
            System.out.println(thisFunction.apply(elem));
        }
        return result / givenElements.size();
    }

    @Override
    public Double apply(T toCheck) {
        if(thisFunction.apply(toCheck) != null)
            return ((Double) thisFunction.apply(toCheck));
        return 0D;
    }
}
