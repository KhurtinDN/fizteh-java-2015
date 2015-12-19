package ru.fizteh.fivt.students.chipak.collectionquery;

import java.util.List;
import java.util.function.Function;


public class Aggregates {


    public static <C, T extends Comparable<T>> Function<C, T> max(Function<C, T> expression) {
        return new AggregationFunction<C, T>() {
            private Function<C, T> converter = expression;

            @Override
            public T apply(List<C> list) {
                T result = null;
                for (C element : list) {
                    if (result == null) {
                        result = converter.apply(element);
                    } else {
                        T currentResult = converter.apply(element);
                        if (currentResult.compareTo(result) > 0) {
                            result = currentResult;
                        }
                    }
                }
                return result;
            }

            @Override
            public T apply(C c) {
                return null;
            }
        };
    }


    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        return new AggregationFunction<C, T>() {
            private Function<C, T> converter = expression;

            @Override
            public T apply(List<C> list) {
                T result = null;
                for (C element : list) {
                    if (result == null) {
                        result = converter.apply(element);
                    } else {
                        T currentResult = converter.apply(element);
                        if (currentResult.compareTo(result) < 0) {
                            result = currentResult;
                        }
                    }
                }
                return result;
            }

            @Override
            public T apply(C c) {
                return null;
            }
        };
    }


    public static <C> Function<C, Integer> count(Function<C, ?> expression) {
        return new AggregationFunction<C, Integer>() {
            @Override
            public Integer apply(List<C> list) {
                return list.size();
            }

            @Override
            public Integer apply(C c) {
                return null;
            }
        };
    }


    public static <C> Function<C, Double> avg(Function<C, ? extends Number> expression) {
        return new AggregationFunction<C, Double>() {
            private Function<C, ? extends Number> converter = expression;

            @Override
            public Double apply(List<C> list) {
                Double result = 0d;
                for (C element : list) {
                    result += converter.apply(element).doubleValue();
                }
                result /= list.size();
                return result;
            }

            @Override
            public Double apply(C t) {
                return null;
            }
        };
    }

}