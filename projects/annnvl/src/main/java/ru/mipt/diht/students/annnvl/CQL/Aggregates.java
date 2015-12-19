package ru.mipt.diht.students.annnvl.CQL;
/**
 * Aggregate functions.
 */
import java.util.function.Function;
import ru.mipt.diht.students.annnvl.CQL.impl.Avg;
import ru.mipt.diht.students.annnvl.CQL.impl.Count;
import ru.mipt.diht.students.annnvl.CQL.impl.Max;
import ru.mipt.diht.students.annnvl.CQL.impl.Min;

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
        return new Max<>(expression);
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
        return new Min<>(expression);
    }

    /**
     * Number of items in source collection that turns this expression into not null.
     *
     * @param expression
     * @param <T>
     * @return
     */
    public static <T> Function<T, Integer> count(Function<T, ?> expression) {
        return new Count<>(expression);
    }


    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <T>
     * @return
     */
    public static <T> Function<T, Double> avg(Function<T, ? extends Number> expression) {
        return new Avg<>(expression);
    }

}