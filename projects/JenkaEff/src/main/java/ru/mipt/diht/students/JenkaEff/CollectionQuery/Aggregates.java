package ru.fizteh.fivt.students.JenkaEff.CollectionQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Aggregates {
    public static <T> Function<T, Integer> count(Function<T, ?> expression) {
        return new Count<>(expression);
    }

    public static <T> Function<T, Double> avg(Function<T, ? extends Number> expression) {
        return new Avg<>(expression);
    }

    public interface Aggregator<T, C> extends Function<T, C> {
        C apply(List<T> list);
    }

    public static class Count<T> implements Aggregator<T, Integer> {

        private Function<T, ?> function;
        public Count(Function<T, ?> expression) {
            this.function = expression;
        }

        @Override
        public Integer apply(List<T> list) {
            Set<Object> distinctedList = new HashSet<>();
            list.stream().filter(e -> !distinctedList.contains(function.apply(e))).forEach(e -> {
                distinctedList.add(function.apply(e));
            });
            return distinctedList.size();
        }
        @Override
        public Integer apply(T t) {
            return null;
        }
    }

    public static class Avg<T> implements Aggregator<T, Double> {
        private Function<T, ? extends Number> function;
        public Avg(Function<T, ? extends Number> expression) {
            this.function = expression;
        }

        @Override
        public Double apply(List<T> list) {
            Double result = 0.0;
            for (T e : list) {
                result += function.apply(e).floatValue();
            }
            return result / list.size();
        }

        @Override
        public Double apply(T t) {
            return null;
        }
    }
}