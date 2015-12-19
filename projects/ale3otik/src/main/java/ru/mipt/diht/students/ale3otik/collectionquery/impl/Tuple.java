package ru.mipt.diht.students.ale3otik.collectionquery.impl;

/**
 * Created by alex on 18.12.15.
 */
public class Tuple<F, S> {

    private final F first;
    private final S second;

    public Tuple(F rFirst, S rSecond) {
        this.first = rFirst;
        this.second = rSecond;
    }

    public final F getFirst() {
        return first;
    }

    public final S getSecond() {
        return second;
    }

    @Override
    public final String toString() {
        return "Tuple{"
                + "first=" + first
                + ", second=" + second
                + '}';
    }
}
