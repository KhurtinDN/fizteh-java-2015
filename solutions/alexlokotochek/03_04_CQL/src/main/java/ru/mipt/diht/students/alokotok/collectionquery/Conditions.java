package ru.mipt.diht.students.alokotok.collectionquery;

import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Created by lokotochek on 30.11.15.
 */
public class Conditions<T> {

    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp) {
        //throw new UnsupportedOperationException();
        return element -> expression.apply(element).matches(regexp);
    }

    public static <T> Predicate<T> like(Function<T, String> expression, String pattern) {
        //throw new UnsupportedOperationException();
        return element -> expression.apply(element).matches(pattern);
    }

}
