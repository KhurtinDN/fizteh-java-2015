package ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by alex on 17.12.15.
 */
public class Count<T> implements Aggregator<T, Long> {
    private Function<T, ?> function;

    public Count(Function<T, ?> rcvFunction) {
        this.function = rcvFunction;
    }

    @Override
    public final Long apply(List<T> elements) {
        long answer = 0;
        for (T e : elements) {
            answer += apply(e);
        }
        return answer;
    }

    @Override
    public final Long apply(T elem) {
        if (function.apply(elem) != null) {
            return 1L;
        }
        return 0L;
    }
}
