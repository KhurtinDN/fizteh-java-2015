package ru.mipt.diht.students.andreyzharkov.collectionquery;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

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

    /**
     * @param items
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> Set<T> set(T... items) {
        return new HashSet<>(Arrays.asList(items));
    }

    /**
     * @param inputStream
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassCastException
     */
    public static <T> Stream<T> lines(InputStream inputStream) throws IOException, ClassCastException {
        final int bufferSize = 1000;
        StringBuilder builder = new StringBuilder();
        byte[] data = new byte[bufferSize];
        Stream.Builder<T> stream = Stream.builder();

        while (inputStream.read(data) > 0) {
            builder.append(data);
        }
        String[] lines = builder.toString().split("[\n]");
        //предполагается что каждый элемент на отдельной строке
        for (String line : lines) {
            stream.add((T) line);
        }
        return stream.build();
    }

    /**
     * @param file
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassCastException
     * @throws UnsupportedOperationException if this {@code Path} is not associated with the default provider
     */
    public static <T> Stream<T> lines(Path file) throws IOException, ClassCastException {
        return lines(new FileInputStream(file.toFile()));
    }

}
