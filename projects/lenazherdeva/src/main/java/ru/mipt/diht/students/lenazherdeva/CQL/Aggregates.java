package ru.mipt.diht.students.lenazherdeva.CQL;

import ru.mipt.diht.students.lenazherdeva.CQL.impl.aggregators.AvgFunction;
import ru.mipt.diht.students.lenazherdeva.CQL.impl.aggregators.CountFunction;
import ru.mipt.diht.students.lenazherdeva.CQL.impl.aggregators.MinFunction;

import java.util.function.Function;

/**
 * Created by admin on 17.11.2015.
**/
public class Aggregates {

    public static <C, T extends Comparable<T>> Function<C, T> max(Function<C, T> expression) {
        return new MinFunction<>(expression);
    }

    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        return new MinFunction<>(expression);
    }


    public static <C> Function<C, Integer> count(Function<C, ?> expression) {
        return new CountFunction<C>();
    }



    public static <C> Function<C, Double> avg(Function<C, ? extends Number> expression) {
        return new AvgFunction<>();
    }

}

