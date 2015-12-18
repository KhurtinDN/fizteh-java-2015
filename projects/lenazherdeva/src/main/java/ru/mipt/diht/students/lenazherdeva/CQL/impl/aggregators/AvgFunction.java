package ru.mipt.diht.students.lenazherdeva.CQL.impl.aggregators;


import java.util.List;

/**
 * Created by admin on 17.11.2015.
**/
public class AvgFunction<T> implements Aggregator<T, Double> {


    @Override
    public final Double apply(T t) {
        return null;
    }

    @Override
    public final Double applyOnList(List<T> list) {
        Double result = 0d;
        return result;
    }

}
