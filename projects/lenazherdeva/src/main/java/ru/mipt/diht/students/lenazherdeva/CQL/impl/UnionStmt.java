package ru.mipt.diht.students.lenazherdeva.CQL.impl;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by admin on 18.11.2015.
 */
public class UnionStmt<T, R> {

    private List<R> pastElements = new ArrayList<>();

    public final List<R> getPastElements() {
        return pastElements;
    }

    public final List<T> getElements() {
        return elements;
    }

    private List<T> elements = new ArrayList<>();

    public UnionStmt(Iterable<R> iterable) {
        for (R curr : iterable) {
            pastElements.add(curr);
        }
    }

    public UnionStmt(Iterable<R> pastElementss, Iterable<T> elementss) {
        for (R curr : pastElementss) {
            this.pastElements.add(curr);
        }
        for (T curr : elementss)  {
            this.elements.add(curr);
        }
    }

    public final UnionStmt<T, R> from(Iterable<T> elementss) {
        return new UnionStmt<>(pastElements, elementss);
    }

}
