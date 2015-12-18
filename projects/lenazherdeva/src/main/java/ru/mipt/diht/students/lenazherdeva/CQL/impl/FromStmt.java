package ru.mipt.diht.students.lenazherdeva.CQL.impl;

/*import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;*/

/**
 * Created by admin on 18.11.2015.


public class FromStmt<T> {
    public List<T> getObjects() {
        return objects;
    }

    private List<T> objects = new ArrayList<T>();

    public FromStmt(Iterable<T> iterable) {
        for (T curr : iterable) {
            objects.add(curr);
        }
    }
    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    /*public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<T>(stream);
    }


    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> returnClazz, Function<T, ?>... functions) {
            return new SelectStmt<>(objects, returnClazz, false, functions);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> returnClazz, Function<T, ?>... functions) {
        return new SelectStmt<>(objects, returnClazz, true, functions);
    }
}*/
