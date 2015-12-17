package ru.mipt.diht.students.maxDankow.sqlcollections.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FromStatement<T> {
    private List<T> items = new ArrayList<>();
    ;

    public FromStatement(Iterable<T> iterable) {
        for (T item : iterable) {
            items.add(item);
        }
    }

    public static <T> FromStatement<T> from(Iterable<T> iterable) {
        return new FromStatement<T>(iterable);
    }

    @SafeVarargs
    public final <R> SelectStatement<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStatement<T, R>(items, false, clazz, s);
    }

    @SafeVarargs
    public final <R> SelectStatement<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStatement<T, R>(items, true, clazz, s);
    }
}
