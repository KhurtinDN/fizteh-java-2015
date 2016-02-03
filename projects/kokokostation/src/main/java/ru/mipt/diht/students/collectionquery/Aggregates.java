package ru.mipt.diht.students.collectionquery;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * Aggregate functions.
 */
public class Aggregates {

    /**
     * Maximum value for expression for elements of given collection.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> AggregateFunction<C, T> max(Function<C, T> expression) {
        Collector<C, Wrapper<T>, T> collector = new CompareCollector<>(CompareCollector::max, expression);

        return new AggregateFunctionImplementation<>(collector);
    }

    /**
     * Minimum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> AggregateFunction<C, T> min(Function<C, T> expression) {
        Collector<C, Wrapper<T>, T> collector = new CompareCollector<>(CompareCollector::min, expression);

        return new AggregateFunctionImplementation<>(collector);
    }

    /**
     * Number of items in source collection that turns this expression into not null.
     *
     * @param expression
     * @param <C>
     * //@param <T>
     * @return
     */
    public static <C> AggregateFunction<C, Long> count(Function<C, ?> expression) {
        Collector<C, Wrapper<Long>, Long> collector = new Collector<C, Wrapper<Long>, Long>() {
            @Override
            public Supplier<Wrapper<Long>> supplier() {
                return () -> new Wrapper<>(0L);
            }

            @Override
            public BiConsumer<Wrapper<Long>, C> accumulator() {
                return (accumulator, obj) -> {
                        if (expression.apply(obj) != null) {
                            accumulator.set(accumulator.get() + 1);
                        }
                    };
            }

            @Override
            public BinaryOperator<Wrapper<Long>> combiner() {
                return (accumulator1, accumulator2) -> new Wrapper<>(accumulator1.get() + accumulator2.get());
            }

            @Override
            public Function<Wrapper<Long>, Long> finisher() {
                return Wrapper::get;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }
        };

        return new AggregateFunctionImplementation<>(collector);
    }

    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Number> AggregateFunction<C, Double> avg(Function<C, T> expression) {
        Collector<C, Wrapper<Pair<Double, Long>>, Double> collector =
                new Collector<C, Wrapper<Pair<Double, Long>>, Double>() {
            @Override
            public Supplier<Wrapper<Pair<Double, Long>>> supplier() {
                return () -> new Wrapper<>(new Pair<>(0.0, 0L));
            }

            @Override
            public BiConsumer<Wrapper<Pair<Double, Long>>, C> accumulator() {
                return (accumulator, obj) -> accumulator.set(
                        new Pair<>(accumulator.get().getKey() + expression.apply(obj).doubleValue(),
                        accumulator.get().getValue() + 1));
            }

            @Override
            public BinaryOperator<Wrapper<Pair<Double, Long>>> combiner() {
                return (accumulator1, accumulator2) -> new Wrapper<>(new Pair<>(
                        accumulator1.get().getKey() + accumulator2.get().getKey(),
                        accumulator1.get().getValue() + accumulator2.get().getValue()));
            }

            @Override
            public Function<Wrapper<Pair<Double, Long>>, Double> finisher() {
                return pair -> pair.get().getKey() / pair.get().getValue();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }
        };

        return new AggregateFunctionImplementation<>(collector);
    }
}
class Wrapper<T> {
    private T obj;

    Wrapper(T obj) {
        this.obj = obj;
    }

    T get() {
        return obj;
    }

    void set(T obj) {
        this.obj = obj;
    }
}

class CompareCollector<C, T extends Comparable<T>> implements Collector<C, Wrapper<T>, T> {
    private final BiFunction<T, T, T> comparator;
    private final Function<C, T> expression;

    CompareCollector(BiFunction<T, T, T> comparator, Function<C, T> expression) {
        this.comparator = comparator;
        this.expression = expression;
    }

    static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) < 0 ? b : a;
    }

    static <T extends Comparable<T>> T min(T a, T b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    private T compare(T a, T b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return comparator.apply(a, b);
        }
    }

    @Override
    public Supplier<Wrapper<T>> supplier() {
        return () -> new Wrapper<>(null);
    }

    @Override
    public BiConsumer<Wrapper<T>, C> accumulator() {
        return (accumulator, obj) -> accumulator.set(compare(accumulator.get(), expression.apply(obj)));
    }

    @Override
    public BinaryOperator<Wrapper<T>> combiner() {
        return (a, b) -> new Wrapper<>(compare(a.get(), b.get()));
    }

    @Override
    public Function<Wrapper<T>, T> finisher() {
        return Wrapper::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }
}