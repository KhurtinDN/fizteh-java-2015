package ru.mipt.diht.students.elinrin.collectionquery.impl;


public class Tuple<F, S> {

    private final F first;
    private final S second;

    public Tuple(final F first, final S second) {
        this.first = first;
        this.second = second;
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

