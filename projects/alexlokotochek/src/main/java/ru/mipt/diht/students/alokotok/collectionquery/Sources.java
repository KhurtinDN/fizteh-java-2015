package ru.mipt.diht.students.alokotok.collectionquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lokotochek on 30.11.15.
 */
public class Sources {

    @SafeVarargs
    public static <T> List<T> list(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
