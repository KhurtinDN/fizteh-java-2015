package ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by alex on 17.12.15.
 */
public interface Aggregator<T, C> extends Function<T, C> {
    default C apply(List<T> list) {
        return null;
    }
}
