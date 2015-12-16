package ru.mipt.diht.students.alokotok.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by lokotochek on 30.11.15.
 */
public class UnionStmt<T, R> {

    public List<R> getPastElements() {
        return pastElements;
    }

    private List<R> pastElements = new ArrayList<>();

    public List<T> getElements() {
        return elements;
    }

    private List<T> elements = new ArrayList<>();

    public UnionStmt(Iterable<R> iterable) {
        iterable.forEach(e -> pastElements.add(e));
    }

    public UnionStmt(Iterable<R> pastElements, Iterable<T> elements) {
        for (R thisElement : pastElements) {
            this.pastElements.add(thisElement);
        }
        for (T thisElement : elements) {
            this.elements.add(thisElement);
        }
    }

    public UnionStmt<T, R> from(Iterable<T> elements) {
        return new UnionStmt<>(pastElements, elements);
    }


    @SafeVarargs
    public final SelectStmt<T, R> select(Class<R> returnClass, Function<T, ?>... functions) {
        return new SelectStmt<T, R> ((List<R>) pastElements, elements, returnClass, false, functions);
    }

    @SafeVarargs
    public final SelectStmt<T, R> selectDistinct(Class<R> returnClass, Function<T, ?>... functions) {
        return new SelectStmt<T, R>((List<R>) pastElements, elements, returnClass, true, functions);
    }
}