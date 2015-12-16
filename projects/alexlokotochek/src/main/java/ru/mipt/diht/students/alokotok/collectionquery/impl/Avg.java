package ru.mipt.diht.students.alokotok.collectionquery.impl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by lokotochek on 30.11.15.
 */
public class Avg<T> implements Aggregator<T, Double> {

    private Function<T, ? extends Number> function;

    public Avg(Function<T, ? extends Number> expression) {
        this.function = expression;
    }

    //(наследуем интерфейс от Aggregator)
    @Override
    public final Double apply(List<T> elements) {
        Double elementsAmount = (double) elements.size(), sum = 0.0;
        for (T element : elements) {
            sum += (Double) function.apply(element);
        }
        return sum / elementsAmount;
    }

    @Override
    public final Double apply(T element) {
        return (Double) function.apply(element);
    }

}
