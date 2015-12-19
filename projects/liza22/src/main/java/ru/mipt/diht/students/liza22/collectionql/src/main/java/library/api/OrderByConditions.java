package library.api;

import java.util.Comparator;
import java.util.function.Function;

/*
 * "orderBy" helpers - comparators with asc and desc order based on lambda function
 */
public class OrderByConditions {

    public static <T> Comparator<T> asc(Function<T, ? extends Comparable> condition) {
        return (o1, o2) -> condition.apply(o1).compareTo(condition.apply(o2));
    }

    public static <T> Comparator<T> desc(Function<T, ? extends Comparable> condition) {
        return (o1, o2) -> condition.apply(o2).compareTo(condition.apply(o1));
    }
}
