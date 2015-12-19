package library.api;

import library.core.QuerySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * "from" statement sources.
 */
public class Sources {

    public static <T> List<T> list(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    public static <S> Source<S> from(List<S> list) {
        return new QuerySource<>(list);
    }
}
