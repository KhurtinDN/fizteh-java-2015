package ru.fizteh.fivt.students.popova.CollectionQl2;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by V on 19.12.2015.
 */
public class Comparators {
    public static <T  extends Comparable<T>> Comparator<Student> asc(Function<Student, T> func){
        Comparator<Student> comparator = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return func.apply(o1).compareTo(func.apply(o2));
            }
        };
        return comparator;
    }
    public static <T  extends Comparable<T>> Comparator<Student> desc(Function<Student, T> func){
        Comparator<Student> comparator = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return func.apply(o1).compareTo(func.apply(o2));
            }
        };
        return comparator.reversed();
    }
}
