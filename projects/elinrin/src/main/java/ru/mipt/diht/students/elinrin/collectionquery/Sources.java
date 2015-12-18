package ru.mipt.diht.students.elinrin.collectionquery;


import java.util.Arrays;
import java.util.List;

public class Sources {

    @SafeVarargs
    public static <T> List<T> list(final T... items) {
        return Arrays.asList(items);
    }
}
