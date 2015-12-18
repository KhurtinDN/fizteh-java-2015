package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SelectStmt<T, R> implements Query<R> {
    private Iterable<T> data;
    private Iterable<R> previous;
    private Function<T, ?>[] functions;
    private Class<R> returnedClass;
    private boolean isDistinct;
    private boolean isTupleR;

    @SafeVarargs
    SelectStmt(Iterable<R> prvious, Iterable<T> dta, Class<R> clazz, boolean isDistnct, Function<T, ?>... s) {
        this.data = dta;
        this.previous = prvious;
        functions = s;
        returnedClass = clazz;
        this.isDistinct = isDistnct;
        isTupleR = false;
    }

    SelectStmt(Iterable<R> prvious, Iterable<T> dta, boolean isDistnct, Function<T, R> func) {
        this.data = dta;
        this.previous = prvious;
        functions = new Function[]{func};
        returnedClass = (Class<R>) func.apply(data.iterator().next()).getClass();
        this.isDistinct = isDistnct;
        isTupleR = false;
    }

    <F, S> SelectStmt(Iterable<R> prvious, Iterable<T> dta, boolean isDistnct,
                      Function<T, F> first, Function<T, S> second) {
        this.data = dta;
        this.previous = prvious;
        functions = new Function[]{first, second};
        returnedClass = (Class<R>) (new Tuple(first.apply(data.iterator().next()),
                second.apply(data.iterator().next()))).getClass();
        this.isDistinct = isDistnct;
        isTupleR = true;
    }

    public final WhereStmt<T, R> where(Predicate<T> predicate) {
        return new WhereStmt<>(previous, data, returnedClass, predicate, isDistinct, isTupleR, functions);
    }

    @Override
    public final Iterable<R> execute() throws QueryExecuteException {
        if (data == null) {
            return new ArrayList<>();
        }

        Object[] constructorArguments = new Object[functions.length];
        Class[] resultClasses = new Class[functions.length];
        for (int i = 0; i < functions.length; i++) {
            resultClasses[i] = functions[i].apply(data.iterator().next()).getClass();
        }

        ArrayList<R> result = new ArrayList<>();
        for (T element : data) {
            for (int i = 0; i < functions.length; i++) {
                constructorArguments[i] = functions[i].apply(element);
            }
            try {
                if (isTupleR) {
                    //почему-то не находит его конструктор, пришлось отдельно рассмотреть
                    result.add((R) new Tuple(constructorArguments[0], constructorArguments[1]));
                } else {
                    result.add(returnedClass.getConstructor(resultClasses).newInstance(constructorArguments));
                }
            } catch (Exception ex) {
                throw new QueryExecuteException("Failed to construct output class!", ex);
            }
        }

        if (previous != null) {
            previous.forEach(result::add);
        }

        if (isDistinct) {
            return result.stream().distinct().collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public final Stream<R> stream() throws QueryExecuteException {
        return StreamSupport.stream(execute().spliterator(), false);
    }
}
