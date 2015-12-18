package ru.mipt.diht.students.simon23rus.CQL.data;

import java.util.Arrays;
import java.util.List;

/**
 * Helper methods to create collections.
 */
public class Sources {

    /**
     * @param items
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> List<T> list(T... items) {
        return Arrays.asList(items);
    }

//    /**
//     * @param items
//     * @param <T>
//     * @return
//     */
//    @SafeVarargs
//    public static <T> Set<T> set(T... items) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * @param inputStream
//     * @param <T>
//     * @return
//     */
//    public static <T> Stream<T> lines(InputStream inputStream) {
//        throw new UnsupportedOperationException();
//    }
//
//    /**
//     * @param file
//     * @param <T>
//     * @return
//     */
//    public static <T> Stream<T> lines(Path file) {
//        throw new UnsupportedOperationException();
//    }
//
}
