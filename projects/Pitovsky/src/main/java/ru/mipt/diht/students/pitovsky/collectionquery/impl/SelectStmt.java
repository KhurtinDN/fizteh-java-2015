package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import ru.mipt.diht.students.pitovsky.collectionquery.impl.UnionStmt;
//import ru.mipt.diht.students.pitovsky.collectionquery.impl.WhereStmt;

public class SelectStmt<T, R> {

    private Iterable<T> base;
    private Class<R> outputClass;
    private Function<T, ?>[] convertFunctions;

    @SafeVarargs
    public SelectStmt(Iterable<T> baseCollection, Class<R> clazz, Function<T, ?>... s) {
        base = baseCollection;
        outputClass = clazz;
        convertFunctions = s;
    }

    public WhereStmt<T, R> where(Predicate<T> predicate) {
        throw new UnsupportedOperationException();
    }

    public Iterable<R> execute() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ArrayList<R> output = new ArrayList<>();
        Class<?>[] outputParametrsTypes = new Class<?>[convertFunctions.length];
        for (int i = 0; i < convertFunctions.length; ++i) {
            outputParametrsTypes[i] = convertFunctions[i].apply(null).getClass(); //FIXME: find return class of function
        }
        Constructor<R> constructor = outputClass.getConstructor(outputParametrsTypes);
        for (T element : base) {
            Object[] parametrs = new Object[convertFunctions.length];
            for (int i = 0; i < convertFunctions.length; ++i) {
                parametrs[i] = convertFunctions[i].apply(element);
            }
            output.add(constructor.newInstance(parametrs)); //TODO: exceptions
        }
        return output;
    }

    public Stream<R> stream() {
        throw new UnsupportedOperationException();
    }

    public class WhereStmt<T, R> {
        @SafeVarargs
        public final WhereStmt<T, R> groupBy(Function<T, ?>... expressions) {
            throw new UnsupportedOperationException();
        }

        @SafeVarargs
        public final WhereStmt<T, R> orderBy(Comparator<T>... comparators) {
            throw new UnsupportedOperationException();
        }

        public WhereStmt<T, R> having(Predicate<R> condition) {
            throw new UnsupportedOperationException();
        }

        public WhereStmt<T, R> limit(int amount) {
            throw new UnsupportedOperationException();
        }

        public UnionStmt union() {
            throw new UnsupportedOperationException();
        }
    }

}
