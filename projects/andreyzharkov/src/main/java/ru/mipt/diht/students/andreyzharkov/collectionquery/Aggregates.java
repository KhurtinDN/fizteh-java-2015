package ru.mipt.diht.students.andreyzharkov.collectionquery;

import java.util.Collections;
import java.util.function.Function;

/**
 * Aggregate functions.
 */
public class Aggregates {

    /**
     * Maximum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Function<C, T> max(Function<C, T> expression) {
        return new Function<C, T>() {
            @Override
            public T apply(C collection) {
                return expression.apply(collection);
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
    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        return new Function<C, T>() {
            @Override
            public T apply(C collection) {
                return expression.apply(collection);
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
    public static <C, T extends Comparable<T>> Function<C, T> count(Function<C, T> expression) {
        throw new UnsupportedOperationException();
    }

    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Function<C, T> avg(Function<C, T> expression) {
        throw new UnsupportedOperationException();
    }

}
