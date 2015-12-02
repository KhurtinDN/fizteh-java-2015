package ru.mipt.diht.students.pitovsky.collectionquery;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Aggregate functions.
 */
public class Aggregates {

    public interface Aggregate<C, T> extends Function<C, T> {
        T forGroup(Set<C> set);
    }

    private static <C, T> Collection<T> getValues(Collection<C> set, Function<C, T> expression) {
        Set<T> values = new HashSet<>();
        for (C element : set) {
            values.add(expression.apply(element));
        }
        return values;
    }

    /**
     * Maximum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Aggregate<C, T> max(Function<C, T> expression) {
        return new Aggregate<C, T>() {

            @Override
            public T apply(C element) {
                return (T) expression.apply(element);
            }

            @Override
            public T forGroup(Set<C> set) {
                return Collections.max(getValues(set, expression));
            }
        };
    }

    /**
     * Minimum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Number & Comparable<T>> Aggregate<C, T> min(Function<C, T> expression) {
        return new Aggregate<C, T>() {

            @Override
            public T apply(C element) {
                return (T) expression.apply(element);
            }

            @Override
            public T forGroup(Set<C> set) {
                return Collections.min(getValues(set, expression));
            }
        };
    }

    /**
     * Number of items in source collection that turns this expression into not null.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Aggregate<C, Long> count(Function<C, T> expression) {
        return new Aggregate<C, Long>() {

            @Override
            public Long apply(C element) {
                return Long.valueOf(1);
            }

            @Override
            public Long forGroup(Set<C> set) {
                return (long) set.size();
            }
        };
    }

    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Number & Comparable<T>> Aggregate<C, T> avg(Function<C, T> expression) {
        return new Aggregate<C, T>() {

            @Override
            public T apply(C element) {
                return expression.apply(element);
            }

            @Override
            public T forGroup(Set<C> set) {
                T average;
                //todo: how?!!
                throw new UnsupportedOperationException();
            }
        };
    }

}
