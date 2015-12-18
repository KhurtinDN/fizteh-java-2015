package ru.mipt.diht.students.maxdankow.sqlcollections.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UnionStatement<T, R> {

    private List<R> previousItems = new ArrayList<>();
    private List<T> items = new ArrayList<>();

    public final List<R> getPreviousItems() {
        return previousItems;
    }

    public final List<T> getItems() {
        return items;
    }

    public UnionStatement(Iterable<R> iterable) {
        for (R thisElement : iterable) {
            previousItems.add(thisElement);
        }
    }

    public UnionStatement(Iterable<R> pastElements, Iterable<T> elements) {
        for (R thisElement : pastElements) {
            this.previousItems.add(thisElement);
        }
        for (T thisElement : elements) {
            this.items.add(thisElement);
        }
    }

    public final UnionStatement<T, R> from(Iterable<T> elements) {
        return new UnionStatement<>(previousItems, elements);
    }

    @SafeVarargs
    public final SelectStatement<T, R> select(Class<R> returnClass, Function<T, ?>... functions) {
        return new SelectStatement<>(previousItems, items, false, returnClass, functions);
    }

    @SafeVarargs
    public final SelectStatement<T, R> selectDistinct(Class<R> returnClass, Function<T, ?>... functions) {
        return new SelectStatement<>(previousItems, items, true, returnClass, functions);
    }

}
