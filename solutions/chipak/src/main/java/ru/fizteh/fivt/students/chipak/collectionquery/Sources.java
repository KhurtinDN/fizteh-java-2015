package ru.fizteh.fivt.students.chipak.collectionquery;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Helper methods to create collections.
 *
 * @author akormushin
 */
public class Sources {

    /**
     * @param items
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> List<T> list(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    /**
     * @param items
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> Set<T> set(T... items) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param inputStream
     * @param <T>
     * @return
     */
    public static <T> Stream<T> lines(InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param file
     * @param <T>
     * @return
     */
    public static <T> Stream<T> lines(Path file) {
        throw new UnsupportedOperationException();
    }

}