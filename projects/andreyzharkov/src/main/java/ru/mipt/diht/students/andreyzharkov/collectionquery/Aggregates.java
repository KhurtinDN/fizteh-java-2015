package ru.mipt.diht.students.andreyzharkov.collectionquery;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * Aggregate functions.
 */
public class Aggregates {

    public interface Agregator<C, T> extends Function<C, T>{
        T apply(Collection<C> collection);
    }

    /**
     * Maximum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Agregator<C, T> max(Function<C, T> expression) {
        return new Agregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) {
                if (collection.isEmpty()){
                    return null;
                }
                T res = expression.apply(collection.iterator().next());
                for (C element : collection){
                    if (expression.apply(element).compareTo(res) > 0){
                        res = expression.apply(element);
                    }
                }
                return res;
            }

            @Override
            public T apply(C c) {
                return null;
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
    public static <C, T extends Comparable<T>> Agregator<C, T> min(Function<C, T> expression) {
        return new Agregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) {
                if (collection.isEmpty()){
                    return null;
                }
                T res = expression.apply(collection.iterator().next());
                for (C element : collection){
                    if (expression.apply(element).compareTo(res) < 0){
                        res = expression.apply(element);
                    }
                }
                return res;
            }

            @Override
            public T apply(C c) {
                return null;
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
    public static <C, T extends Comparable<T>> Function<C, Long> count(Function<C, T> expression) {
        return new Agregator<C, Long>() {
            @Override
            public Long apply(Collection<C> collection) {
                long counter = 0;
                for (C element : collection){
                    if (expression.apply(element) != null){
                        counter++;
                    }
                }
                return  counter;
            }

            @Override
            public Long apply(C c) {
                if (expression.apply(c) != null){
                    return (long)1;
                }
                return (long)0;
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
    public static <C, T extends Number> Agregator<C, T> avg(Function<C, T> expression) {
        return new Agregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) {
                return null;
            }

            @Override
            public T apply(C c) {
                return null;
            }
        };
    }

}
