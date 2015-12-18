package ru.mipt.diht.students.maxdankow.sqlcollections;

import java.util.function.Function;
import java.util.function.Predicate;

public class Conditions<T> {

    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp) {
        return (value) -> expression.apply(value).matches(regexp);
    }

    public static <T> Predicate<T> like(Function<T, String> expression, String pattern) {
        return (value) -> expression.apply(value).matches(pattern);
    }

}
