package ru.mipt.diht.students.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by mikhail on 01.02.16.
 */
public class Utils {
    public static <T> Stream<T> iterableToStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        for (T item : iterable) {
            result.add(item);
        }

        return result;
    }

    public static <T> List<T> streamToList(Stream<T> stream) {
        List<T> result = new ArrayList<>();
        stream.forEach(result::add);

        return result;
    }

    public static <T> ArrayList<T> arrayListFromElement(T item) {
        ArrayList<T> newList = new ArrayList<>();
        newList.add(item);

        return newList;
    }
}
