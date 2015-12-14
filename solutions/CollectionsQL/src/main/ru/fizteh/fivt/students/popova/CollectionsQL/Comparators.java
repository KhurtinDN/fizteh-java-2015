import java.util.*;
import java.util.function.Function;

/**
 * Created by V on 05.12.2015.
 */
public class Comparators<T> {
    public static <T  extends Comparable<T>> Comparator<T> asc(Function<Student, T> func){
        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
        return comparator;
    }
    public static <T  extends Comparable<T>> Comparator<T> desc(Function<Student, T> func){
        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
        return comparator.reversed();
    }
}
