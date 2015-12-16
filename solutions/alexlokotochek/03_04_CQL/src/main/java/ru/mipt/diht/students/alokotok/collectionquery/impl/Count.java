package ru.mipt.diht.students.alokotok.collectionquery.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by lokotochek on 30.11.15.
 */
public class Count<T> implements Aggregator<T, Integer> {

    private Function<T, ?> function;

    public Count(Function<T, ?> expression) {
        this.function = expression;
    }

    //(наследуем интерфейс от Aggregator)
    @Override
    public Integer apply(List<T> elements) {

        // считаем лямбду от элементов, а затем -
        // сколько уникальных результатов получилось

        Set<Object> newElements = new HashSet<>();

        for (T element : elements) {
            if (!newElements.contains(function.apply(element))) {
                newElements.add(function.apply(element));
            }
        }
        return newElements.size();
    }

    @Override
    public Integer apply(T t) {
        return null;
    }

}
