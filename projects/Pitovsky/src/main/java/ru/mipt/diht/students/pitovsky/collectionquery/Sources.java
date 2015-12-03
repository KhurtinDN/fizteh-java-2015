package ru.mipt.diht.students.pitovsky.collectionquery;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        Set<T> result = new HashSet<>();
        for (T element : items) {
            result.add(element);
        }
        return result;
    }
    
    static final int READ_BLOCK_SIZE = 100;

    /**
     * Create a stream from InputStream.
     * @param inputStream
     * @return
     * @throws IOException
     * @throws ClassCastException if lines from stream can not be casted for T
     */
    public static <T> Stream<T> lines(InputStream inputStream) throws IOException, ClassCastException {
        StringBuilder builder = new StringBuilder();
        byte[] data = new byte[READ_BLOCK_SIZE];
        int readed = inputStream.read(data);
        while (readed > 0) {
            builder.append(data);
            readed = inputStream.read(data);
        }
        String[] lines = builder.toString().split("[\n]");
        Stream.Builder<T> stream = Stream.builder();
        for (String line : lines) {
            stream.add((T)line);
        }
        return stream.build();
    }

    /**
     * @param file
     * @param <T>
     * @return
     * @throws IOException 
     * @throws ClassCastException if lines from file can not be converted to T
     */
    public static <T> Stream<T> lines(Path file) throws ClassCastException, IOException {
        InputStream input = new FileInputStream(file.toFile());
        return lines(input);
    }

}
