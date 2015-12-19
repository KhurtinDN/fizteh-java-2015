package ru.mipt.diht.students.elinrin.collectionquery.impl;


public class Tuple<F, S> {

    private final F first;
    private final S second;

    public Tuple(final F first1, final S second2) {
        first = first1;
        second = second2;
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
                + "}\n";
    }
}

